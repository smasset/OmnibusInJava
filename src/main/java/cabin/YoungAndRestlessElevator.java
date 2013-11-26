package cabin;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import cabin.comparator.YoungAndRestlessComparator;
import cabin.util.Direction;
import cabin.util.FloorRequest;
import cabin.util.RequestType;

public class YoungAndRestlessElevator extends StateOfLoveAndTrustElevator {

	protected Integer ageLimit = null;
	protected Map<Integer, FloorRequest> oldRequests = new ConcurrentHashMap<Integer, FloorRequest>();
	protected Map<Integer, FloorRequest> youngRequests = new ConcurrentHashMap<Integer, FloorRequest>();

	public YoungAndRestlessElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE, Elevator.DEFAULT_CABIN_COUNT);
	}

	public YoungAndRestlessElevator(int minFloor, int maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
		this.ageLimit = 10;
	}

	private Integer getNextFloor(Map<Integer, FloorRequest> requests) {
		Integer nextFloor = null;

		SortedSet<FloorRequest> requestSet = new TreeSet<>(new YoungAndRestlessComparator(this.getMode(), this.currentFloor));
		requestSet.addAll(requests.values());
		boolean serveOnlyOutRequests = this.cabinSize <= this.cabinPopulation;

		FloorRequest currentRequest = null;
		Iterator<FloorRequest> requestIterator = requestSet.iterator();
		while ((nextFloor == null) && (requestIterator.hasNext())) {
			currentRequest = requestIterator.next();

			if (serveOnlyOutRequests) {
				if (RequestType.OUT.equals(currentRequest.getType())) {
					nextFloor = currentRequest.getFloor();
				}
			} else {
				nextFloor = currentRequest.getFloor();
			}
		}

		return nextFloor;
	}

	@Override
	protected Integer getNextFloor() {
		Integer nextFloor = this.getNextFloor(this.youngRequests);

		if (nextFloor == null) {
			nextFloor = this.getNextFloor(this.oldRequests);
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

		return nextFloor;
	}

	private boolean removeRequest(Map<Integer, FloorRequest> requests, String direction) {
		boolean removed = false;
		FloorRequest currentFloorRequest = requests.get(this.currentFloor);

		if (currentFloorRequest != null) {
			removed = true;

			if (currentFloorRequest.getCount() == 1) {
				requests.remove(this.currentFloor);
			} else {
				requests.put(this.currentFloor, currentFloorRequest.decrementCount(direction));
			}
		}

		return removed;
	}

	private void removeRequest(String direction) {
		this.removeRequest(this.youngRequests, direction);
		this.removeRequest(this.oldRequests, direction);
	}

	private boolean addRequest(Map<Integer, FloorRequest> requests, FloorRequest defaultRequest, Integer floor, String direction) {
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
			requests.put(floor, newRequest.incrementCount(direction));
		}

		return added;
	}

	private void addRequest(Integer floor, String direction) {
		if (Math.abs(floor - this.currentFloor) <= this.ageLimit) {
			this.addRequest(this.youngRequests, this.oldRequests.get(floor), floor, direction);
		}
		this.addRequest(this.oldRequests, null, floor, direction);
	}

	@Override
	public void go(Integer floor, Integer cabin) {
		// Remove current floor request
		String direction = null;

		int compareTo = this.currentFloor.compareTo(floor);
		if (compareTo > 0) {
			direction = Direction.DOWN;
		} else if (compareTo < 0) {
			direction = Direction.UP;
		}

		this.removeRequest(direction);
		this.addRequest(floor, null);
	}

	@Override
	public void call(Integer from, String direction) {
		this.addRequest(from, direction);
	}

	@Override
	public void userHasExited(Integer cabin) {
		super.userHasExited(cabin);
		this.removeRequest(null);
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabinCount);
		this.oldRequests.clear();
		this.youngRequests.clear();
	}

	private String requestToString(SortedSet<FloorRequest> requests, Long currentTick, Integer currentFloor) {
		StringBuilder sb = new StringBuilder();

		for (FloorRequest currentRequest : requests) {
			sb.append(currentRequest.toString(currentTick, currentFloor));
			sb.append(", ");
		}

		return sb.toString();
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		YoungAndRestlessComparator requestComparator = new YoungAndRestlessComparator(getMode(), currentFloor);

		SortedSet<FloorRequest> oldRequestSet = new TreeSet<>(requestComparator);
		oldRequestSet.addAll(this.oldRequests.values());
		info.put("oldRequests", this.requestToString(oldRequestSet, currentTick, currentFloor));

		SortedSet<FloorRequest> youndRequestSet = new TreeSet<>(requestComparator);
		youndRequestSet.addAll(this.youngRequests.values());
		info.put("youngRequests", this.requestToString(youndRequestSet, currentTick, currentFloor));

		return info;
	}

}
