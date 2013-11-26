package cabin;

import cabin.util.Command;
import cabin.util.Mode;

public interface Cabin {

	public Command nextCommand();

	public void call(Integer from, String direction);

	public void go(Integer floor);

	public void userHasEntered();

	public void userHasExited();

	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause);

	public Mode getMode();
	
	public String status(boolean pretty);

	public boolean isDebug();

	public void setDebug(boolean debug);

	public void thresholds(Integer alertThreshold, Integer panicThreshold);

}
