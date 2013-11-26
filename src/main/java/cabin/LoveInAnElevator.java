package cabin;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class LoveInAnElevator extends StateOfLoveAndTrustElevator {
	protected ConcurrentSkipListMap<Integer, Integer> requests = new ConcurrentSkipListMap<Integer, Integer>();

	public LoveInAnElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE, Elevator.DEFAULT_CABIN_COUNT);
	}

	public LoveInAnElevator(int minFloor, int maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
	}

	@Override
	protected Integer getNextFloor() {
		Integer nextFloor = null;

		switch (this.lastDirection) {

		case Direction.UP:
			nextFloor = this.requests.ceilingKey(this.currentFloor);
			if (nextFloor == null) {
				nextFloor = this.requests.lowerKey(this.currentFloor);
			}
			break;

		case Direction.DOWN:
			nextFloor = this.requests.floorKey(this.currentFloor);
			if (nextFloor == null) {
				nextFloor = this.requests.higherKey(this.currentFloor);
			}
			break;

		default:
			break;
		}

		return nextFloor;
	}

	@Override
	public void go(Integer floor, Integer cabin) {
		if ((floor >= this.minFloor) && (floor <= this.maxFloor)) {
			Integer currentFloorRequests = this.requests.get(floor);

			if (currentFloorRequests == null) {
				currentFloorRequests = 0;
			}

			this.requests.put(floor, ++currentFloorRequests);
		}
	}

	@Override
	public void call(Integer from, String direction) {
		this.go(from, null);
	}

	private void removeRequest() {
		Integer currentFloorRequests = this.requests.get(this.currentFloor);

		if (currentFloorRequests != null) {
			if (currentFloorRequests == 1) {
				this.requests.remove(this.currentFloor);
			} else {
				this.requests.put(this.currentFloor, --currentFloorRequests);
			}
		}
	}

	@Override
	public void userHasExited(Integer cabin) {
		super.userHasExited(cabin);
		this.removeRequest();
	}

	@Override
	public void userHasEntered(Integer cabin) {
		super.userHasEntered(cabin);
		this.removeRequest();
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
