package cabin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import cabin.command.Command;

public class DefaultElevator implements Elevator {
	private static final Logger requestLogger = Logger.getLogger("requests");

	protected int minFloor = Elevator.DEFAULT_MIN_FLOOR;
	protected int maxFloor = Elevator.DEFAULT_MAX_FLOOR;

	protected Integer cabinSize = null;
	protected int cabinCount = 0;
	protected boolean debug = false;
	protected Integer alertThreshold = null;
	protected Integer panicThreshold = null;
	protected Long currentTick = Long.MIN_VALUE;

	public DefaultElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE);
	}

	public DefaultElevator(int minFloor, int maxFloor, Integer cabinSize) {
		this.minFloor = minFloor;
		this.maxFloor = maxFloor;
		this.cabinSize = cabinSize;
	}

	@Override
	public Command nextCommand() {
		this.currentTick++;
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
		requestLogger.info("Reset : " + cause + " ; status : " + this.status(false));

		if (minFloor != null) {
			this.minFloor = minFloor;
		}

		if (maxFloor != null) {
			this.maxFloor = maxFloor;
		}

		if (cabinSize != null) {
			this.cabinSize = cabinSize;
		}

		this.currentTick = Long.MIN_VALUE;
		this.cabinCount = 0;
	}

	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = new TreeMap<String, String>();

		info.put("minFloor", Integer.toString(this.minFloor));
		info.put("maxFloor", Integer.toString(this.maxFloor));
		info.put("cabinSize", this.cabinSize != null ? cabinSize.toString() : "");
		info.put("cabinCount", Integer.toString(this.cabinCount));
		info.put("debug", Boolean.toString(this.debug));
		info.put("panicThreshold", this.panicThreshold != null ? panicThreshold.toString() : "");
		info.put("alertThreshold", this.alertThreshold != null ? alertThreshold.toString() : "");
		info.put("currentTick", Long.toString(this.currentTick));

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

	@Override
	public void thresholds(Integer alertThreshold, Integer panicThreshold) {
		if (alertThreshold != null) {
			this.alertThreshold = alertThreshold;
		}

		if (panicThreshold != null) {
			this.panicThreshold = panicThreshold;
		}
	}

}
