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

		NavigableMap<Integer, FloorRequest> nextRequests = null;
		if (Direction.UP.equals(direction)) {
			nextRequests = this.requests.tailMap(this.currentFloor, true);
		} else {
			nextRequests = this.requests.headMap(this.currentFloor, true).descendingMap();
		}

		Iterator<FloorRequest> requestIterator = null;
		if (sortRequests) {
			requestIterator = new TreeSet<>(nextRequests.values()).iterator();
		} else {
			requestIterator = nextRequests.values().iterator();
		}

		FloorRequest currentRequest = null;
		while((nextFloor == null) && (requestIterator.hasNext())) {
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
		this.call(floor, null);
	}

	@Override
	public void call(Integer from, String direction) {
		if ((from >= this.minFloor) && (from <= this.maxFloor)) {
			RequestType newType = direction == null ? RequestType.OUT : null;

			FloorRequest newRequest = this.requests.get(from);
			if (newRequest == null) {
				if (direction != null) {
					newType = RequestType.valueOf(direction);
				}

				newRequest = new FloorRequest(from, newType);
			} else {
				if (direction != null) {
					newType = newRequest.getType();

					switch (newRequest.getType()) {
					case UP:
						if (Direction.DOWN.equals(direction)) {
							newType = RequestType.UP_DOWN;
						}
					case DOWN:
						if (Direction.UP.equals(direction)) {
							newType = RequestType.UP_DOWN;
						}
					default:
						break;
					}
				}

				newRequest.setType(newType);
			}

			if (direction != null) {
				newRequest.incrementRelativeCount();
			} else {
				newRequest.decrementRelativeCount();
			}

			this.requests.put(from, newRequest.incrementCount());
		}
	}

	private void removeRequest(RequestType type) {
		FloorRequest currentFloorRequest = this.requests.get(this.currentFloor);

		if (currentFloorRequest != null) {
			if (currentFloorRequest.getCount() == 1) {
				this.requests.remove(this.currentFloor);
			} else {
				if (type != null) {
					currentFloorRequest.setType(RequestType.UP_DOWN);
					currentFloorRequest.incrementRelativeCount();
				} else {
					currentFloorRequest.decrementRelativeCount();
				}

				this.requests.put(this.currentFloor, currentFloorRequest.decrementCount());
			}
		}
	}

	@Override
	public void userHasExited() {
		super.userHasExited();
		this.removeRequest(RequestType.OUT);
	}

	@Override
	public void userHasEntered() {
		super.userHasEntered();
		this.removeRequest(null);
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
