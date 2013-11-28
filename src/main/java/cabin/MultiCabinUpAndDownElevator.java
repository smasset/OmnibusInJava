package cabin;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import cabin.comparator.ClosestOutComparator;
import cabin.util.Cabin;
import cabin.util.Direction;
import cabin.util.FloorRequest;

public class MultiCabinUpAndDownElevator extends MultiCabinElevator {
	protected NavigableMap<Integer, FloorRequest> requests = new ConcurrentSkipListMap<Integer, FloorRequest>();

	public MultiCabinUpAndDownElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE, Elevator.DEFAULT_CABIN_COUNT);
	}

	public MultiCabinUpAndDownElevator(int minFloor, int maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
	}

	private void removeRequest(Integer cabinId, String direction) {
		Cabin cabin = this.cabins.get(cabinId);

		if (cabin != null) {

			FloorRequest currentFloorRequest = requests.get(cabin.getCurrentFloor());

			if (currentFloorRequest != null) {
				if (currentFloorRequest.getCount() == 1) {
					requests.remove(currentFloorRequest.getFloor());
				} else {
					requests.put(currentFloorRequest.getFloor(), currentFloorRequest.decrementCount(cabinId, direction));
				}
			}
		}
	}

	private Integer getNextFloor(Integer cabinId, String direction) {
		Integer nextFloor = null;

		boolean sortRequests = false;
		boolean serveOnlyOutRequests = false;
		boolean serveOnlySameRequests = false;
		boolean returnDefaultFloor = false;

		Cabin cabin = this.cabins.get(cabinId);
		if (cabin != null) {
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
				SortedSet<FloorRequest> requestSet = new TreeSet<>(new ClosestOutComparator(cabin.getMode(), cabin.getCurrentFloor(), this.currentTick));
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

			FloorRequest currentRequest = null;
			while ((nextFloor == null) && (requestIterator.hasNext())) {
				currentRequest = requestIterator.next();

				if (serveOnlyOutRequests) {
					if (currentRequest.getOutCount(cabinId) > 0) {
						nextFloor = currentRequest.getFloor();
					}
				} else if (serveOnlySameRequests) {
					if (currentRequest.hasSameDirection(direction)) {
						nextFloor = currentRequest.getFloor();
					}
				} else {
					nextFloor = currentRequest.getFloor();
				}
			}

			if (returnDefaultFloor && (nextFloor == null)) {
				if (Direction.UP.equals(direction)) {
					nextFloor = this.requests.ceilingKey(cabin.getCurrentFloor());
				} else {
					nextFloor = this.requests.floorKey(cabin.getCurrentFloor());
				}
			}
		}

		return nextFloor;
	}

	@Override
	public Integer getNextFloor(Integer cabinId) {
		Integer nextFloor = null;

		Cabin cabin = this.cabins.get(cabinId);
		if (cabin != null) {

			switch (cabin.getLastDirection()) {

			case Direction.UP:
				nextFloor = this.getNextFloor(cabinId, Direction.UP);
				if (nextFloor == null) {
					nextFloor = this.getNextFloor(cabinId, Direction.DOWN);
				}
				break;

			case Direction.DOWN:
				nextFloor = this.getNextFloor(cabinId, Direction.DOWN);
				if (nextFloor == null) {
					nextFloor = this.getNextFloor(cabinId, Direction.UP);
				}
				break;

			default:
				break;
			}

		}

		return nextFloor;
	}

	@Override
	public void go(Integer floor, Integer cabinId) {
		// Remove current floor request
		String direction = null;

		Cabin cabin = this.cabins.get(cabinId);
		if (cabin != null) {
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
	public void userHasEntered(Integer cabin) {
		super.userHasEntered(cabin);
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabinCount);
		this.requests.clear();
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("requests", this.requests.descendingMap().toString());

		return info;
	}

}
