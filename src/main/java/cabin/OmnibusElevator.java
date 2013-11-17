package cabin;

import java.util.Map;

import cabin.command.Command;

public class OmnibusElevator extends DefaultElevator {

	private Command[] commands = null;

	private int count = 0;

	public OmnibusElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE);
	}

	public OmnibusElevator(int minFloor, int maxFloor, Integer cabinSize) {
		super(minFloor, maxFloor, cabinSize);
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
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause) {
		super.reset(minFloor, maxFloor, cabinSize, cause);

		this.commands = null;
		this.count = 0;
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("count", Integer.toString(this.count));

		return info;
	}
}
