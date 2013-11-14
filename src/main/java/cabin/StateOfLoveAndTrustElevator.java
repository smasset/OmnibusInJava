package cabin;

import cabin.command.Command;

public abstract class StateOfLoveAndTrustElevator extends DefaultElevator {
	protected CabinState currentState = CabinState.STOPPED;

	protected CabinState lastDirection = CabinState.UP;

	protected Integer currentFloor = super.minFloor;

	protected abstract Integer getNextFloor();

	public StateOfLoveAndTrustElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public StateOfLoveAndTrustElevator(int minFloor, int maxFloor) {
		super(minFloor, maxFloor);
	}

	protected synchronized Command getNextCommand() {
		Command result = Command.NOTHING;

		// this.print();

		switch (this.currentState) {

		case UP:
		case DOWN:
		case STOPPED:
			Integer nextFloor = this.getNextFloor();
			if (nextFloor != null) {
				int comparison = Integer.compare(this.currentFloor, nextFloor);

				if (comparison == 0) {
					result = Command.OPEN;
					this.currentState = CabinState.OPENED;
				} else if (comparison > 0) {
					this.currentFloor--;
					result = Command.DOWN;
					this.lastDirection = CabinState.DOWN;
					this.currentState = CabinState.DOWN;
				} else {
					this.currentFloor++;
					result = Command.UP;
					this.lastDirection = CabinState.UP;
					this.currentState = CabinState.UP;
				}
			}
			break;

		case OPENED:
			result = Command.CLOSE;
			this.currentState = CabinState.STOPPED;
			break;

		default:
			break;

		}

		return result;
	}

	protected void print() {
		System.out.println("currentState : " + this.currentState);
		System.out.println("currentFloor : " + this.currentFloor);
	}

	@Override
	public Command nextCommand() {
		return this.getNextCommand();
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause) {
		super.reset(minFloor, maxFloor, cabinSize, cause);

		this.currentState = CabinState.STOPPED;
		this.lastDirection = CabinState.UP;
		this.currentFloor = super.minFloor;
	}
}
