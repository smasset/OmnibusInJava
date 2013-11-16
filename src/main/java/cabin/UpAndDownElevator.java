package cabin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

public class UpAndDownElevator extends StateOfLoveAndTrustElevator {
	protected NavigableMap<Integer, FloorRequest> requests = new ConcurrentSkipListMap<Integer, FloorRequest>();

	public UpAndDownElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public UpAndDownElevator(int minFloor, int maxFloor) {
		super(minFloor, maxFloor);
	}

	private Integer getNextUpperFloor() {
		Integer nextFloor = null;
		boolean floorFound = false;
		Mode mode = this.getMode();

		NavigableMap<Integer, FloorRequest> nextRequests = this.requests.tailMap(this.currentFloor, true);

		switch (mode) {

		case ALERT:
			Set<FloorRequest> values = new TreeSet<>(nextRequests.values());
			for(FloorRequest currentRequest : values) {
				if (RequestType.OUT.equals(currentRequest.getType())) {
					floorFound = true;
				}

				if (floorFound) {
					nextFloor = currentRequest.getFloor();
					break;
				}
			}
			break;

		default:
			for (Entry<Integer, FloorRequest> currentEntry : nextRequests.entrySet()) {
				switch (mode) {

				case PANIC:
					if (RequestType.OUT.equals(currentEntry.getValue().getType())) {
						floorFound = true;
					}
					break;

				default:
					if (currentEntry.getValue().hasSameDirection(this.lastDirection)) {
						floorFound = true;
					}
					break;
				}

				if (floorFound) {
					nextFloor = currentEntry.getKey();
					break;
				}
			}
			break;
		}

		if (nextFloor == null) {

			switch (mode) {

			case ALERT:
			case PANIC:
				break;

			default:
				nextFloor = this.requests.ceilingKey(this.currentFloor);
				break;
			}
		}

		return nextFloor;
	}

	private Integer getNextLowerFloor() {
		Integer nextFloor = null;
		boolean floorFound = false;
		Mode mode = this.getMode();

		NavigableMap<Integer, FloorRequest> nextRequests = this.requests.headMap(this.currentFloor, true).descendingMap();

		switch (mode) {

		case ALERT:
			Set<FloorRequest> values = new TreeSet<>(nextRequests.values());
			for(FloorRequest currentRequest : values) {
				if (RequestType.OUT.equals(currentRequest.getType())) {
					floorFound = true;
				}

				if (floorFound) {
					nextFloor = currentRequest.getFloor();
					break;
				}
			}
			break;

		default:
			for (Entry<Integer, FloorRequest> currentEntry : nextRequests.entrySet()) {
				switch (mode) {

				case PANIC:
					if (RequestType.OUT.equals(currentEntry.getValue().getType())) {
						floorFound = true;
					}
					break;

				default:
					if (currentEntry.getValue().hasSameDirection(this.lastDirection)) {
						floorFound = true;
					}
					break;
				}

				if (floorFound) {
					nextFloor = currentEntry.getKey();
					break;
				}
			}
			break;
		}

		if (nextFloor == null) {

			switch (mode) {

			case ALERT:
			case PANIC:
				break;

			default:
				nextFloor = this.requests.floorKey(this.currentFloor);
				break;
			}
		}

		return nextFloor;
	}

	@Override
	protected Integer getNextFloor() {
		Integer nextFloor = null;

		switch (this.lastDirection) {

		case Direction.UP:
			nextFloor = this.getNextUpperFloor();
			if (nextFloor == null) {
				nextFloor = this.getNextLowerFloor();
			}
			break;

		case Direction.DOWN:
			nextFloor = this.getNextLowerFloor();
			if (nextFloor == null) {
				nextFloor = this.getNextUpperFloor();
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
