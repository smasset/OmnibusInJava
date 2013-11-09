package cabin;

import cabin.command.Command;

public interface Elevator {
	public static final int DEFAULT_MIN_FLOOR = 0;
	public static final int DEFAULT_MAX_FLOOR = 5;

	public Command nextCommand();

	public void call(Integer from, String direction);

    public void go(Integer floor);

    public void userHasEntered();

    public void userHasExited();

    public void reset(String cause);

}
