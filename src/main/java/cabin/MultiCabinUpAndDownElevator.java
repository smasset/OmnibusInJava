package cabin;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import cabin.comparator.ClosestOutComparator;
import cabin.util.Cabin;
import cabin.util.Direction;
import cabin.util.FloorRequest;
import cabin.util.RequestType;
import cabin.util.SelectiveCabin;

public class MultiCabinUpAndDownElevator extends MultiCabinElevator {
	protected NavigableMap<Integer, FloorRequest> requests = new ConcurrentSkipListMap<Integer, FloorRequest>();

	public MultiCabinUpAndDownElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE, Elevator.DEFAULT_CABIN_COUNT);
	}

	public MultiCabinUpAndDownElevator(int minFloor, int maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
	}

	protected void initCabins(Integer cabinCount) {
		Integer initFloor = 0;
		Cabin currentCabin = null;
		Integer floorRange = (this.maxFloor - this.minFloor + 1) / (cabinCount - 2);

		for (int cabinIndex = 0; cabinIndex < cabinCount; ++cabinIndex) {
			if (cabinIndex == 0) {
				currentCabin = new SelectiveCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR, this.minFloor);
			} else if (cabinIndex == cabinCount - 1) {
				currentCabin = new SelectiveCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR, this.maxFloor);
			} else {
				initFloor = this.minFloor + (cabinIndex -1) * floorRange + Double.valueOf(Math.floor(floorRange / 2)).intValue();

				currentCabin = new SelectiveCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR, initFloor);
			}

			this.cabins.put(cabinIndex, currentCabin);
		}
	}

	private void removeRequest(Integer cabinId, String direction) {
		Cabin cabin = (Cabin) this.cabins.get(cabinId);

		if (cabin != null) {
			Integer currentFloor = cabin.getCurrentFloor();
			FloorRequest currentFloorRequest = this.requests.get(currentFloor);

			if (currentFloorRequest != null) {
				if (currentFloorRequest.getCount() == 1) {
					this.requests.remove(currentFloor);
				} else {
					this.requests.put(currentFloor, currentFloorRequest.decrementCount(cabinId, direction));
				}
			}
		}
	}

	protected Queue<FloorRequest> getNextFloors(Cabin cabin, String direction) {
		Queue<FloorRequest> nextFloors = new LinkedList<>();

		if (cabin != null) {
			boolean sortRequests = false;
			boolean serveOnlyOutRequests = false;
			boolean serveOnlySameRequests = false;
			boolean returnDefaultFloor = false;

			switch (cabin.getMode()) {
			case PANIC:
				sortRequests = true;

			case ALERT:
				serveOnlyOutRequests = true;
				break;

			case NORMAL:
			default:
				serveOnlySameRequests = true;
				returnDefaultFloor = true;
				break;
			}

			Iterator<FloorRequest> requestIterator = null;
			if (sortRequests) {
				SortedSet<FloorRequest> requestSet = new TreeSet<>(new ClosestOutComparator(cabin, this.currentTick));
				requestSet.addAll(this.requests.values());

				requestIterator = requestSet.iterator();
			} else {
				NavigableMap<Integer, FloorRequest> nextRequests = null;
				if (Direction.UP.equals(direction)) {
					nextRequests = this.requests.tailMap(cabin.getCurrentFloor(), true);
				} else {
					nextRequests = this.requests.headMap(cabin.getCurrentFloor(), true).descendingMap();
				}

				requestIterator = nextRequests.values().iterator();
			}

			FloorRequest defaultFloor = null;
			FloorRequest currentRequest = null;
			while (requestIterator.hasNext()) {
				currentRequest = requestIterator.next();

				if (serveOnlyOutRequests) {
					if (RequestType.OUT.equals(currentRequest.getType(cabin.getId()))) {
						nextFloors.add(currentRequest);
					}
				} else if (serveOnlySameRequests) {
					if (currentRequest.hasSameDirection(cabin.getId(), direction)) {
						if (RequestType.OUT.equals(currentRequest.getType(cabin.getId())) || currentRequest.getAbsoluteDistance(cabin.getCurrentFloor()) <= 10) {
							nextFloors.add(currentRequest);
						}
					}
				} else {
					nextFloors.add(currentRequest);
				}

				if (returnDefaultFloor && (currentRequest.getCount(cabin.getId()) > 0)) {
					if (currentRequest.getAbsoluteDistance(cabin.getCurrentFloor()) <= 10) {
						defaultFloor = currentRequest;
					}
				}
			}

			if (returnDefaultFloor && !nextFloors.contains(defaultFloor)) {
				nextFloors.add(defaultFloor);
			}
		}

		return nextFloors;
	}

	@Override
	public Queue<FloorRequest> getNextFloors(Cabin cabin) {
		Queue<FloorRequest> nextFloors = new LinkedList<>();

		if (cabin != null) {

			switch (cabin.getLastDirection()) {

			case Direction.UP:
				nextFloors.addAll(this.getNextFloors(cabin, Direction.UP));
				cabin.setOpenAllDoors(nextFloors.size() <= 3);
				nextFloors.addAll(this.getNextFloors(cabin, Direction.DOWN));
				break;

			case Direction.DOWN:
				nextFloors.addAll(this.getNextFloors(cabin, Direction.DOWN));
				cabin.setOpenAllDoors(nextFloors.size() <= 3);
				nextFloors.addAll(this.getNextFloors(cabin, Direction.UP));
				break;

			default:
				break;
			}
		}

		return nextFloors;
	}

	@Override
	public void go(Integer floor, Integer cabinId) {
		Cabin cabin = (Cabin) this.cabins.get(cabinId);

		if (cabin != null) {
			// Remove current floor request
			String direction = null;

			int compareTo = cabin.getCurrentFloor().compareTo(floor);
			if (compareTo > 0) {
				direction = Direction.DOWN;
			} else if (compareTo < 0) {
				direction = Direction.UP;
			}

			this.removeRequest(cabinId, direction);

			// Add new request
			if ((floor >= this.minFloor) && (floor <= this.maxFloor)) {

				FloorRequest newRequest = this.requests.get(floor);
				if (newRequest == null) {
					newRequest = new FloorRequest(floor);
				}

				newRequest.setLatestBirthDate(this.currentTick);
				this.requests.put(floor, newRequest.incrementCount(cabinId, null));
			}
		}
	}

	@Override
	public void call(Integer from, String direction) {
		if ((from >= this.minFloor) && (from <= this.maxFloor)) {

			FloorRequest newRequest = this.requests.get(from);
			if (newRequest == null) {
				newRequest = new FloorRequest(from);
			}

			newRequest.setLatestBirthDate(this.currentTick);
			this.requests.put(from, newRequest.incrementCount(direction));
		}
	}

	@Override
	public void userHasExited(Integer cabinId) {
		super.userHasExited(cabinId);
		this.removeRequest(cabinId, null);
	}

	@Override
	public void userHasEntered(Integer cabinId) {
		super.userHasEntered(cabinId);
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabinCount);
		if (this.requests != null) {
			this.requests.clear();
		}
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("requests", this.requests != null ? this.requests.descendingMap().toString() : "");

		return info;
	}

}
