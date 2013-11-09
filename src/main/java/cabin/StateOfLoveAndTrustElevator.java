package cabin;

import cabin.command.Command;

public abstract class StateOfLoveAndTrustElevator extends DefaultElevator {
	protected CabinState currentState = CabinState.STOPPED;

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
		Integer nextFloor = this.getNextFloor();

		switch (this.currentState) {

		case UP:
		case DOWN:
		case STOPPED:
			if (nextFloor != null) {
				int comparison = Integer.compare(this.currentFloor, nextFloor);

				if (comparison == 0) {
					result = Command.OPEN;
					this.currentState = CabinState.OPENED;
				} else if (comparison > 0) {
					this.currentFloor--;
					result = Command.DOWN;
					this.currentState = CabinState.DOWN;
				} else {
					this.currentFloor++;
					result = Command.UP;
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
}
