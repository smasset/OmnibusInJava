package cabin;

import cabin.command.Command;

public class DefaultElevator implements Elevator {
	protected int maxFloor = Elevator.DEFAULT_MAX_FLOOR;

	public DefaultElevator() {
		this(Elevator.DEFAULT_MAX_FLOOR);
	}

	public DefaultElevator(int maxFloor) {
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
	}

	@Override
	public void userHasExited() {
	}

	@Override
	public void reset(String cause) {
	}

}
