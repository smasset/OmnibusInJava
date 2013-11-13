package cabin;

import java.util.Map.Entry;
import java.util.NavigableMap;
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

		NavigableMap<Integer, FloorRequest> nextRequests = this.requests.tailMap(this.currentFloor, true);
		for (Entry<Integer, FloorRequest> currentEntry : nextRequests.entrySet()) {
			if (currentEntry.getValue().hasSameDirection(this.lastDirection.toString())) {
				nextFloor = currentEntry.getKey();
				break;
			}
		}

		if (nextFloor == null) {
			nextFloor = this.requests.ceilingKey(this.currentFloor);
		}

		return nextFloor;
	}

	private Integer getNextLowerFloor() {
		Integer nextFloor = null;

		NavigableMap<Integer, FloorRequest> nextRequests = this.requests.headMap(this.currentFloor, true).descendingMap();
		for (Entry<Integer, FloorRequest> currentEntry : nextRequests.entrySet()) {
			if (currentEntry.getValue().hasSameDirection(this.lastDirection.toString())) {
				nextFloor = currentEntry.getKey();
				break;
			}
		}

		if (nextFloor == null) {
			nextFloor = this.requests.floorKey(this.currentFloor);
		}

		return nextFloor;
	}

	@Override
	protected Integer getNextFloor() {
		Integer nextFloor = null;

		switch (this.lastDirection) {

		case UP:
			nextFloor = this.getNextUpperFloor();
			if (nextFloor == null) {
				nextFloor = this.getNextLowerFloor();
			}
			break;

		case DOWN:
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
		FloorRequest newRequest = this.requests.get(from);
		if (newRequest == null) {
			newRequest = new FloorRequest(from, direction);
		}

		this.requests.put(from, newRequest.incrementCount());
	}

	@Override
	public void userHasExited() {
		FloorRequest currentFloorRequest = this.requests.get(this.currentFloor);

		if (currentFloorRequest != null) {
			if (currentFloorRequest.getCount() == 1) {
				this.requests.remove(this.currentFloor);
			} else {
				this.requests.put(this.currentFloor, currentFloorRequest.decrementCount());
			}
		}
	}

	@Override
	public void userHasEntered() {
		this.userHasExited();
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, String cause) {
		super.reset(minFloor, maxFloor, cause);
		this.requests.clear();
	}

}
