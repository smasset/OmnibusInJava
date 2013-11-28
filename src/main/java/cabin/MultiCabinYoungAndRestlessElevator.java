package cabin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import cabin.comparator.MultiCabinYoungAndRestlessComparator;
import cabin.util.Cabin;
import cabin.util.Direction;
import cabin.util.FloorRequest;
import cabin.util.Mode;
import cabin.util.Test;

public class MultiCabinYoungAndRestlessElevator extends MultiCabinElevator {

	protected Integer ageLimit = null;
	protected Map<Integer, FloorRequest> oldRequests = new ConcurrentHashMap<Integer, FloorRequest>();
	protected Map<Integer, FloorRequest> youngRequests = new ConcurrentHashMap<Integer, FloorRequest>();

	public MultiCabinYoungAndRestlessElevator(Integer cabinCount) {
		this(null, null, null, cabinCount);
	}

	public MultiCabinYoungAndRestlessElevator(Integer minFloor, Integer maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
		this.ageLimit = 10;
	}

	private Integer getNextFloor(Integer cabinId, Map<Integer, FloorRequest> requests, boolean forceOut) {
		Integer nextFloor = null;

		Cabin currentCabin = this.cabins.get(cabinId);
		if (currentCabin != null) {
			if (cabinId == 0 && (this.currentTick  < (this.maxFloor - 1))) {
				nextFloor = this.maxFloor - 1;
			} else {
				MultiCabinYoungAndRestlessComparator comparator = new MultiCabinYoungAndRestlessComparator(currentCabin);

				SortedSet<FloorRequest> requestSet = new TreeSet<>(comparator);
				requestSet.addAll(requests.values());
				boolean serveOnlyOutRequests = forceOut || (!Mode.NORMAL.equals(currentCabin.getMode()));

				FloorRequest currentRequest = null;
				Iterator<FloorRequest> requestIterator = requestSet.iterator();
				while ((nextFloor == null) && (requestIterator.hasNext())) {
					currentRequest = requestIterator.next();

					if (serveOnlyOutRequests) {
						if (currentRequest.getOutCount(cabinId) > 0) {
							nextFloor = currentRequest.getFloor();
						}
					} else {
						nextFloor = currentRequest.getFloor();
					}
				}
			}
		}

		return nextFloor;
	}

	public Integer[] getNextFloors() {
		Integer[] results = null;

		Map<Integer, Test> tests = new HashMap<>(this.cabins.size());
		

		Cabin currentCabin = null;
		TreeSet<FloorRequest> currentYoungRequests = null;
		TreeSet<FloorRequest> currentOldRequests = null;
		for (int cabinIndex =0 ; cabinIndex < this.cabins.size() ; ++cabinIndex) {
			currentCabin = this.cabins.get(cabinIndex);

			// Save sorted young requests for each cabin
			currentYoungRequests = new TreeSet<>(new MultiCabinYoungAndRestlessComparator(currentCabin));
			currentYoungRequests.addAll(this.youngRequests.values());

			// Save sorted old requests for each cabin
			currentOldRequests = new TreeSet<>(new MultiCabinYoungAndRestlessComparator(currentCabin));
			currentOldRequests.addAll(this.oldRequests.values());

			tests.put(cabinIndex, new Test(currentCabin, currentYoungRequests, currentOldRequests));
		}

//		boolean serveOnlyOutRequests = false;
		boolean done = false;

		Map<Integer, FloorRequest> currentRequests = new HashMap<>(this.cabins.size());
		while (!done) {
			Iterator<FloorRequest> currentIterator = null;
			for(Entry<Integer, Test> currentTest : tests.entrySet()) {
				currentIterator = currentTest.getValue().getYoungRequests().iterator();
				currentRequests.put(currentTest.getKey(), currentIterator.hasNext() ? currentIterator.next() : null);
			}

//			if (serveOnlyOutRequests) {
//				if (currentRequest.getOutCount(cabinId) != 0) {
//					nextFloor = currentRequest.getFloor();
//				}
//			} else {
//				nextFloor = currentRequest.getFloor();
//			}
		}

		return results;
	}

	@Override
	public Integer getNextFloor(Integer cabinId) {
		Integer nextFloor = null;

		Cabin currentCabin = this.cabins.get(cabinId);
		if (currentCabin != null) {
			nextFloor = this.getNextFloor(cabinId, this.youngRequests, false);

			if (nextFloor == null) {
				nextFloor = this.getNextFloor(cabinId, this.oldRequests, false);
			}

			if (this.ageLimit != null) {
				Entry<Integer, FloorRequest> currentEntry = null;
				for (Iterator<Entry<Integer, FloorRequest>> moveToOldIterator = this.youngRequests.entrySet().iterator(); moveToOldIterator.hasNext();) {
					currentEntry = moveToOldIterator.next();

					// Remove old requests from youngRequests
					if (currentEntry.getValue().getAge(this.currentTick) > this.ageLimit) {
						// Don't remove next floor as we are serving it
						if (!currentEntry.getKey().equals(nextFloor)) {
							moveToOldIterator.remove();
						}
					}
				}
			}
		}

		return nextFloor;
	}

	private boolean removeRequest(Integer cabinId, Map<Integer, FloorRequest> requests, String direction) {
		boolean removed = false;

		Cabin cabin = this.cabins.get(cabinId);
		if (cabin != null) {

			FloorRequest currentFloorRequest = requests.get(cabin.getCurrentFloor());

			if (currentFloorRequest != null) {
				removed = true;

				if (currentFloorRequest.getCount() == 1) {
					requests.remove(currentFloorRequest.getFloor());
				} else {
					requests.put(currentFloorRequest.getFloor(), currentFloorRequest.decrementCount(cabinId, direction));
				}
			}
		}

		return removed;
	}

	private void removeRequest(Integer cabinId, String direction) {
		this.removeRequest(cabinId, this.youngRequests, direction);
		this.removeRequest(cabinId, this.oldRequests, direction);
	}

	private boolean addRequest(Map<Integer, FloorRequest> requests, FloorRequest defaultRequest, Integer floor, Integer cabinId, String direction) {
		boolean added = false;

		if ((floor >= this.minFloor) && (floor <= this.maxFloor)) {
			added = true;

			FloorRequest newRequest = requests.get(floor);
			if (newRequest == null) {
				if (defaultRequest != null) {
					newRequest = defaultRequest;
				} else {
					newRequest = new FloorRequest(floor);
				}
			}

			newRequest.setLatestBirthDate(this.currentTick);
			requests.put(floor, newRequest.incrementCount(cabinId, direction));
		}

		return added;
	}

	private void addRequest(Integer cabinId, Integer floor, String direction) {
		Integer shortestDistance = null;

		if (cabinId == null) {
			// Request from outside any cabin : compute closest cabin distance
			Integer currentDistance = null;
			for (Cabin currentCabin : this.cabins.values()) {
				currentDistance = Math.abs(floor - currentCabin.getCurrentFloor());
				if ((shortestDistance == null) || (currentDistance.compareTo(shortestDistance) < 0)) {
					shortestDistance = currentDistance;
				}
			}

		} else {
			// User just got in a cabin get the distance from its current floor
			// to destination
			Cabin cabin = this.cabins.get(cabinId);
			if (cabin != null) {
				shortestDistance = Math.abs(floor - cabin.getCurrentFloor());
			}
		}

		// Only add it to young requests if it's close enough
		boolean isCloseEnough = shortestDistance <= this.ageLimit;
		if (isCloseEnough) {
			this.addRequest(this.youngRequests, this.oldRequests.get(floor), floor, cabinId, direction);
		}

		// Always add it to old requests
		this.addRequest(this.oldRequests, null, floor, cabinId, direction);
	}

	@Override
	public void go(Integer floor, Integer cabin) {
		Cabin currentCabin = this.cabins.get(cabin);

		if (currentCabin != null) {
			Integer currentFloor = currentCabin.getCurrentFloor();

			// Remove current floor request
			String direction = null;

			int compareTo = currentFloor.compareTo(floor);
			if (compareTo > 0) {
				direction = Direction.DOWN;
			} else if (compareTo < 0) {
				direction = Direction.UP;
			}

			this.removeRequest(cabin, direction);
			this.addRequest(cabin, floor, null);
		}
	}

	@Override
	public void call(Integer from, String direction) {
		this.addRequest(null, from, direction);
	}

	@Override
	public void userHasExited(Integer cabin) {
		super.userHasExited(cabin);
		this.removeRequest(cabin, null);
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabinCount);
		if (this.oldRequests != null) {
			this.oldRequests.clear();
		}

		if (this.youngRequests != null) {
			this.youngRequests.clear();
		}
	}

	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("ageLimit", this.ageLimit != null ? this.ageLimit.toString() : "");
		info.put("oldRequests", this.oldRequests != null ? new TreeMap<>(this.oldRequests).descendingMap().toString() : "");
		info.put("youngRequests", this.youngRequests != null ? new TreeMap<>(this.youngRequests).descendingMap().toString() : "");

		return info;
	}
}
