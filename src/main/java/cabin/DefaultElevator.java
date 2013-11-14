package cabin;

import cabin.command.Command;

public class DefaultElevator implements Elevator {
	protected int minFloor = Elevator.DEFAULT_MIN_FLOOR;
	protected int maxFloor = Elevator.DEFAULT_MAX_FLOOR;

	protected Integer cabinSize = null;
	protected int cabinCount = 0;

	public DefaultElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public DefaultElevator(int minFloor, int maxFloor) {
		this.minFloor = minFloor;
		this.maxFloor = maxFloor;
	}

	@Override
	public Command nextCommand() {
		return Command.NOTHING;
	}

	@Override
	public void call(Integer from, String direction) {
	}

	@Override
	public void go(Integer floor) {
	}

	@Override
	public void userHasEntered() {
		this.cabinCount++;
	}

	@Override
	public void userHasExited() {
		this.cabinCount--;
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause) {
		if (minFloor != null) {
			this.minFloor = minFloor;
		}

		if (maxFloor != null) {
			this.maxFloor = maxFloor;
		}

		if (cabinSize != null) {
			this.cabinSize = cabinSize;
		}

		this.cabinCount = 0;
	}

}
