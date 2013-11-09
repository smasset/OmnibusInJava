package cabin;

import java.util.concurrent.ConcurrentSkipListMap;

public class LoveInAnElevator extends StateOfLoveAndTrustElevator {
	protected ConcurrentSkipListMap<Integer, Integer> requests = new ConcurrentSkipListMap<Integer, Integer>();

	public LoveInAnElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public LoveInAnElevator(int minFloor, int maxFloor) {
		super(minFloor, maxFloor);
	}

	@Override
	protected Integer getNextFloor() {
		Integer nextFloor = null;

		switch (this.lastDirection) {

		case UP:
			nextFloor = this.requests.ceilingKey(this.currentFloor);
			if (nextFloor == null) {
				nextFloor = this.requests.lowerKey(this.currentFloor);
			}
			break;

		case DOWN:
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
	public void go(Integer floor) {
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
		this.go(from);
	}

	@Override
	public void userHasExited() {
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
	public void userHasEntered() {
		this.userHasExited();
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, String cause) {
		super.reset(minFloor, maxFloor, cause);
		this.requests.clear();
	}

	protected void print() {
		super.print();
		System.out.println("requests     : " + this.requests);
	}
}
