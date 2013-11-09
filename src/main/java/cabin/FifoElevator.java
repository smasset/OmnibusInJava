package cabin;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FifoElevator extends StateOfLoveAndTrustElevator {
	private ConcurrentLinkedQueue<Integer> requests = new ConcurrentLinkedQueue<>();

	public FifoElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public FifoElevator(int minFloor, int maxFloor) {
		super(minFloor, maxFloor);
	}

	@Override
	protected Integer getNextFloor() {
		return this.requests.peek();
	}

	@Override
	public void go(Integer floor) {
		if ((floor >= this.minFloor) && (floor <= this.maxFloor)) {
			if (!this.requests.contains(floor)) {
				this.requests.add(floor);
			}
		}
	}

	@Override
	public void call(Integer from, String direction) {
		if ((from >= this.minFloor) && (from <= this.maxFloor)) {
			if (!this.requests.contains(from)) {
				this.requests.add(from);
			}
		}
	}

	@Override
	public void userHasExited() {
		if (this.currentFloor.equals(this.requests.peek())) {
			this.requests.poll();
		}
	}

	@Override
	public void userHasEntered() {
		if (this.currentFloor.equals(this.requests.peek())) {
			this.requests.poll();
		}
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
