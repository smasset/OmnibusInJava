package cabin;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

public class UpAndDownElevator extends StateOfLoveAndTrustElevator {
	protected NavigableMap<Integer, FloorRequest> requests = new ConcurrentSkipListMap<Integer, FloorRequest>();

	public UpAndDownElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE);
	}

	public UpAndDownElevator(int minFloor, int maxFloor, Integer cabinSize) {
		super(minFloor, maxFloor, cabinSize);
	}

	private Integer getNextFloor(String direction) {
		Integer nextFloor = null;

		boolean sortRequests = false;
		boolean serveOnlyOutRequests = false;
		boolean serveOnlySameRequests = false;
		boolean returnDefaultFloor = false;

		switch (this.getMode()) {
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
			requestIterator = new TreeSet<>(this.requests.values()).iterator();
		} else {
			NavigableMap<Integer, FloorRequest> nextRequests = null;
			if (Direction.UP.equals(direction)) {
				nextRequests = this.requests.tailMap(this.currentFloor, true);
			} else {
				nextRequests = this.requests.headMap(this.currentFloor, true).descendingMap();
			}

			requestIterator = nextRequests.values().iterator();
		}

		FloorRequest currentRequest = null;
		while ((nextFloor == null) && (requestIterator.hasNext())) {
			currentRequest = requestIterator.next();

			if (serveOnlyOutRequests) {
				if (RequestType.OUT.equals(currentRequest.getType())) {
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
				nextFloor = this.requests.ceilingKey(this.currentFloor);
			} else {
				nextFloor = this.requests.floorKey(this.currentFloor);
			}
		}

		return nextFloor;
	}

	@Override
	protected Integer getNextFloor() {
		Integer nextFloor = null;

		switch (this.lastDirection) {

		case Direction.UP:
			nextFloor = this.getNextFloor(Direction.UP);
			if (nextFloor == null) {
				nextFloor = this.getNextFloor(Direction.DOWN);
			}
			break;

		case Direction.DOWN:
			nextFloor = this.getNextFloor(Direction.DOWN);
			if (nextFloor == null) {
				nextFloor = this.getNextFloor(Direction.UP);
			}
			break;

		default:
			break;
		}

		return nextFloor;
	}

	@Override
	public void go(Integer floor) {
		// Remove current floor request
		String direction = null;

		int compareTo = this.currentFloor.compareTo(floor);
		if (compareTo > 0) {
			direction = Direction.DOWN;
		} else if (compareTo < 0) {
			direction = Direction.UP;
		}

		this.removeRequest(direction);

		// Add new request
		if ((floor >= this.minFloor) && (floor <= this.maxFloor)) {

			FloorRequest newRequest = this.requests.get(floor);
			if (newRequest == null) {
				newRequest = new FloorRequest(floor);
			}

			this.requests.put(floor, newRequest.incrementCount(null));
		}
	}

	@Override
	public void call(Integer from, String direction) {
		if ((from >= this.minFloor) && (from <= this.maxFloor)) {

			FloorRequest newRequest = this.requests.get(from);
			if (newRequest == null) {
				newRequest = new FloorRequest(from);
			}

			this.requests.put(from, newRequest.incrementCount(direction));
		}
	}

	private void removeRequest(String direction) {
		FloorRequest currentFloorRequest = this.requests.get(this.currentFloor);

		if (currentFloorRequest != null) {
			if (currentFloorRequest.getCount() == 1) {
				this.requests.remove(this.currentFloor);
			} else {
				this.requests.put(this.currentFloor, currentFloorRequest.decrementCount(direction));
			}
		}
	}

	@Override
	public void userHasExited() {
		super.userHasExited();
		this.removeRequest(null);
	}

	@Override
	public void userHasEntered() {
		super.userHasEntered();
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause) {
		super.reset(minFloor, maxFloor, cabinSize, cause);
		this.requests.clear();
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("requests", this.requests.toString());

		return info;
	}

}
