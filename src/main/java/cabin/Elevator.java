package cabin;

import cabin.util.Command;

public interface Elevator {
	public static final int DEFAULT_MIN_FLOOR = 0;
	public static final int DEFAULT_MAX_FLOOR = 5;
	public static final Integer DEFAULT_CABIN_SIZE = null;
	public static final Integer DEFAULT_CABIN_COUNT = 2;

	public Command[] nextCommands();

	public void call(Integer from, String direction);

	public void go(Integer floor, Integer cabin);

	public void userHasEntered(Integer cabin);

	public void userHasExited(Integer cabin);

	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount);

	public String status(boolean pretty);

	public boolean isDebug();

	public void setDebug(boolean debug);

	public void thresholds(Integer alertThreshold, Integer panicThreshold);
}
