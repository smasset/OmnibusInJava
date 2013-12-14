package cabin;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
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

	protected Deque<FloorRequest> getNextFloors(Cabin cabin, String direction) {
		Deque<FloorRequest> nextFloors = new LinkedList<>();

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

			if (returnDefaultFloor && (defaultFloor != null) && !nextFloors.contains(defaultFloor)) {
				nextFloors.add(defaultFloor);
			}
		}

		return nextFloors;
	}

	@Override
	public Deque<FloorRequest> getNextFloors(Cabin cabin) {
		Deque<FloorRequest> nextFloors = new LinkedList<>();
		FloorRequest lastRequest = null;

		if (cabin != null) {

			switch (cabin.getLastDirection()) {

			case Direction.UP:
				nextFloors.addAll(this.getNextFloors(cabin, Direction.UP));
				lastRequest = nextFloors.peekLast();
				nextFloors.addAll(this.getNextFloors(cabin, Direction.DOWN));
				break;

			case Direction.DOWN:
				nextFloors.addAll(this.getNextFloors(cabin, Direction.DOWN));
				lastRequest = nextFloors.peekLast();
				nextFloors.addAll(this.getNextFloors(cabin, Direction.UP));
				break;

			default:
				break;
			}

			cabin.setOpenAllDoors(lastRequest != null && lastRequest.getAbsoluteDistance(cabin.getCurrentFloor()) <= 3);
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

	public static void main(String[] args) {
		MultiCabinUpAndDownElevator elevator = new MultiCabinUpAndDownElevator(-5, 48, 30, 8);
		Map<Cabin, Deque<FloorRequest>> destinations = new HashMap<>(3);

		elevator.cabins.put(0, new SelectiveCabin(0, null, 0, 0));
		elevator.cabins.put(1, new SelectiveCabin(1, null, 0, 0));
		elevator.cabins.put(2, new SelectiveCabin(2, null, 3, 3));
		elevator.cabins.put(3, new SelectiveCabin(3, null, 3, 3));

		FloorRequest request0 = new FloorRequest(1);
		request0.incrementCount(Direction.UP);
		FloorRequest request1 = new FloorRequest(2);
		request1.incrementCount(1, null);
		FloorRequest request2 = new FloorRequest(3);
		request2.incrementCount(0, null);

		Deque<FloorRequest> cabin0Destinations = new LinkedList<FloorRequest>();
		cabin0Destinations.add(request0);
		cabin0Destinations.add(request2);
		destinations.put((Cabin) elevator.cabins.get(0), cabin0Destinations);

		Deque<FloorRequest> cabin1Destinations = new LinkedList<FloorRequest>();
		cabin1Destinations.add(request0);
		cabin1Destinations.add(request1);
		destinations.put((Cabin) elevator.cabins.get(1), cabin1Destinations);

		Deque<FloorRequest> cabin2Destinations = new LinkedList<FloorRequest>();
		destinations.put((Cabin) elevator.cabins.get(2), cabin2Destinations);

		Deque<FloorRequest> cabin3Destinations = new LinkedList<FloorRequest>();
		destinations.put((Cabin) elevator.cabins.get(3), cabin3Destinations);

		System.out.println("INPUT  : " + destinations);
		System.out.println("OUTPUT : " + elevator.getDestinations(destinations));
	}
}
