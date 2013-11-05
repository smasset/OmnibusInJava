package cabin;

import cabin.command.Command;

public class OmnibusElevator extends DefaultElevator {

    private Command commands[] = {
        Command.OPEN, Command.CLOSE, Command.UP,
        Command.OPEN, Command.CLOSE, Command.UP,
        Command.OPEN, Command.CLOSE, Command.UP,
        Command.OPEN, Command.CLOSE, Command.UP,
        Command.OPEN, Command.CLOSE, Command.UP,
        Command.OPEN, Command.CLOSE, Command.DOWN,
        Command.OPEN, Command.CLOSE, Command.DOWN,
        Command.OPEN, Command.CLOSE, Command.DOWN,
        Command.OPEN, Command.CLOSE, Command.DOWN,
        Command.OPEN, Command.CLOSE, Command.DOWN
    };

    private int count = 0;

    @Override
    public Command nextCommand() {
        return commands[(count++)%commands.length];
    }

	@Override
	public void reset(String cause) {
		this.count = 0;
	}
}
