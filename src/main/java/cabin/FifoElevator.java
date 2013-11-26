package cabin;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FifoElevator extends StateOfLoveAndTrustElevator {
	private ConcurrentLinkedQueue<Integer> requests = new ConcurrentLinkedQueue<>();

	public FifoElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE, Elevator.DEFAULT_CABIN_COUNT);
	}

	public FifoElevator(int minFloor, int maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
	}

	@Override
	protected Integer getNextFloor() {
		return this.requests.peek();
	}

	@Override
	public void go(Integer floor, Integer cabin) {
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
	public void userHasExited(Integer cabin) {
		super.userHasExited(cabin);

		if (this.currentFloor.equals(this.requests.peek())) {
			this.requests.poll();
		}
	}

	@Override
	public void userHasEntered(Integer cabin) {
		super.userHasEntered(cabin);

		if (this.currentFloor.equals(this.requests.peek())) {
			this.requests.poll();
		}
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabincount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabincount);
		this.requests.clear();
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("requests", this.requests.toString());

		return info;
	}
}
