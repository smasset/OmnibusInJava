package cabin;

import java.util.Map;

import cabin.command.Command;

public abstract class StateOfLoveAndTrustElevator extends DefaultElevator {
	protected CabinState currentState = CabinState.STOPPED;

	protected String lastDirection = Direction.UP;

	protected Integer currentFloor = 0;

	protected abstract Integer getNextFloor();

	public StateOfLoveAndTrustElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public StateOfLoveAndTrustElevator(int minFloor, int maxFloor) {
		super(minFloor, maxFloor);
	}

	protected Mode getMode() {
		Mode mode = Mode.NORMAL;

		if ((this.panicThreshold != null) && (this.cabinCount >= this.panicThreshold)) {
			mode = Mode.PANIC;
		} else if ((this.alertThreshold != null) && (this.cabinCount >= this.alertThreshold)) {
			mode = Mode.ALERT;
		}

		return mode;
	}

	protected synchronized Command getNextCommand() {
		Command result = Command.NOTHING;

		switch (this.currentState) {

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
					this.lastDirection = Direction.DOWN;
				} else {
					this.currentFloor++;
					result = Command.UP;
					this.lastDirection = Direction.UP;
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

	@Override
	public Command nextCommand() {
		return this.getNextCommand();
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause) {
		super.reset(minFloor, maxFloor, cabinSize, cause);

		this.currentState = CabinState.STOPPED;
		this.lastDirection = Direction.UP;
		this.currentFloor = 0;

		if (cabinSize != null) {
		    this.panicThreshold = new Integer(cabinSize + 1);
		    this.alertThreshold = Double.valueOf(Math.ceil(0.8d * cabinSize)).intValue();
		}
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("currentState", this.currentState.toString());
		info.put("lastDirection", this.lastDirection);
		info.put("currentFloor", this.currentFloor != null ? currentFloor.toString() : "");
		info.put("mode", this.getMode().toString());

		return info;
	}
}
