package cabin;

import cabin.command.Command;

public class OmnibusElevator extends DefaultElevator {

	private Command[] commands = null;

	private int count = 0;

	public OmnibusElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public OmnibusElevator(int minFloor, int maxFloor) {
		super(minFloor, maxFloor);
	}

	@Override
	public Command nextCommand() {
		if (this.commands == null) {
			this.commands = new Command[this.maxFloor == 0 ? 2 : 6 * this.maxFloor];

			for (int currentFloor = 0; currentFloor < this.commands.length; currentFloor = currentFloor + 3) {
				this.commands[currentFloor] = Command.OPEN;
				this.commands[currentFloor + 1] = Command.CLOSE;
				if (currentFloor + 2 < this.commands.length) {
					if (currentFloor + 2 < 3 * this.maxFloor) {
						this.commands[currentFloor + 2] = Command.UP;
					} else {
						this.commands[currentFloor + 2] = Command.DOWN;
					}
				}
			}
		}

		return commands[(count++) % commands.length];
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, String cause) {
		this.count = 0;
	}
}
