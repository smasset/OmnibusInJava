package cabin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import cabin.command.Command;

public class DefaultElevator implements Elevator {
	private static final Logger statusLogger = Logger.getLogger("status");

	protected int minFloor = Elevator.DEFAULT_MIN_FLOOR;
	protected int maxFloor = Elevator.DEFAULT_MAX_FLOOR;

	protected Integer cabinSize = null;
	protected int cabinCount = 0;
	protected boolean debug = false;

	public DefaultElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR);
	}

	public DefaultElevator(int minFloor, int maxFloor) {
		this.minFloor = minFloor;
		this.maxFloor = maxFloor;
	}

	@Override
	public Command nextCommand() {
		return Command.NOTHING;
	}

	@Override
	public void call(Integer from, String direction) {
	}

	@Override
	public void go(Integer floor) {
	}

	@Override
	public void userHasEntered() {
		this.cabinCount++;
	}

	@Override
	public void userHasExited() {
		this.cabinCount--;
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause) {
		statusLogger.info("Reset : " + cause + " ; status : " + this.status(false));

		if (minFloor != null) {
			this.minFloor = minFloor;
		}

		if (maxFloor != null) {
			this.maxFloor = maxFloor;
		}

		if (cabinSize != null) {
			this.cabinSize = cabinSize;
		}

		this.cabinCount = 0;
	}

	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = new TreeMap<String, String>();

		info.put("minFloor", Integer.toString(this.minFloor));
		info.put("maxFloor", Integer.toString(this.maxFloor));
		info.put("cabinSize", this.cabinSize != null ? cabinSize.toString() : "");
		info.put("cabinCount", Integer.toString(this.cabinCount));
		info.put("debug", Boolean.toString(this.debug));

		return info;
	}

	@Override
	public String status(boolean pretty) {
		StringBuilder sb = new StringBuilder();

		for (Entry<String, String> currentInfo : this.getStatusInfo().entrySet()) {
  		    sb.append(currentInfo.getKey());
  		    sb.append(" : ");
  		    sb.append(currentInfo.getValue());
  		    if (pretty) {
  		    	sb.append("\n");
  		    } else {
  		    	sb.append(" ; ");
  		    }
		}

		return sb.toString();
	}

	@Override
	public boolean isDebug() {
		return this.debug;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
