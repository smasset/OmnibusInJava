package cabin;

import cabin.command.Command;

public interface Elevator {
	public static final int DEFAULT_MIN_FLOOR = 0;
	public static final int DEFAULT_MAX_FLOOR = 5;
	public static final Integer DEFAULT_CABIN_SIZE = null;

	public Command nextCommand();

	public void call(Integer from, String direction);

    public void go(Integer floor);

    public void userHasEntered();

    public void userHasExited();

    public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause);

    public String status(boolean pretty);

    public boolean isDebug();

    public void setDebug(boolean debug);

    public void thresholds(Integer alertThreshold, Integer panicThreshold);
}
