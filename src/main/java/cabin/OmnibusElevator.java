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
			int floorRange = this.maxFloor - this.minFloor;
			this.commands = new Command[floorRange == 0 ? 2 : 6 * floorRange];

			for (int currentFloor = 0; currentFloor < this.commands.length; currentFloor = currentFloor + 3) {
				this.commands[currentFloor] = Command.OPEN;
				this.commands[currentFloor + 1] = Command.CLOSE;
				if (currentFloor + 2 < this.commands.length) {
					if (currentFloor + 2 < 3 * floorRange) {
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
		if (minFloor != null) {
			this.minFloor = minFloor;
		}

		if (maxFloor != null) {
			this.maxFloor = maxFloor;
		}

		this.commands = null;
		this.count = 0;
	}
}
