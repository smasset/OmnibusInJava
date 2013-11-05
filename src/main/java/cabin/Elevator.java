package cabin;

import cabin.command.Command;

public interface Elevator {
	public Command nextCommand();

	public void call(Integer from, String direction);

    public void go(Integer floor);

    public void userHasEntered();

    public void userHasExited();

    public void reset(String cause);

}
