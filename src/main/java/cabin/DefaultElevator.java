package cabin;

import cabin.command.Command;

public class DefaultElevator implements Elevator {

	@Override
	public Command nextCommand() {
		return Command.NOTHING;
	}

	@Override
	public void call(Integer from, Integer to) {
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
