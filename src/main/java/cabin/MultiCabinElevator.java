package cabin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import cabin.util.Cabin;
import cabin.util.Command;
import cabin.util.DefaultCabin;

public class MultiCabinElevator implements Elevator {
	private static final Logger requestLogger = Logger.getLogger("requests");

	protected final SortedMap<Integer, Cabin> cabins = new TreeMap<>();
	protected Integer minFloor = null;
	protected Integer maxFloor = null;
	protected Integer cabinSize = null;

	protected boolean debug = false;
	protected Long currentTick = 0l;

	public MultiCabinElevator(Integer cabinCount) {
		this(null, null, null, cabinCount);
	}

	public MultiCabinElevator(Integer minFloor, Integer maxFloor, Integer cabinSize, Integer cabinCount) {
		this.reset(minFloor, maxFloor, cabinSize, null, cabinCount);
	}

	public Integer getNextFloor(Integer cabinId) {
		return null;
	}

	@Override
	public Command[] nextCommands() {
		this.currentTick++;
		Command[] commands = new Command[cabins.size()];

		for (Entry<Integer, Cabin> currentCabin : cabins.entrySet()) {
			currentCabin.getValue().setNextFloor(this.getNextFloor(currentCabin.getKey()));
			commands[currentCabin.getKey()] = currentCabin.getValue().nextCommand();
		}

		return commands;
	}

	@Override
	public void call(Integer from, String direction) {
	}

	@Override
	public void go(Integer floor, Integer cabin) {
	}

	@Override
	public void userHasEntered(Integer cabin) {
		Cabin inCabin = this.cabins.get(cabin);

		if (inCabin != null) {
			inCabin.getIn();
		}
	}

	@Override
	public void userHasExited(Integer cabin) {
		Cabin outCabin = this.cabins.get(cabin);

		if (outCabin != null) {
			outCabin.getOut();
		}
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		if (cause != null) {
			requestLogger.info("Reset : " + cause + " ; status : " + this.status(false));
		}

		if (minFloor != null) {
			this.minFloor = minFloor;
		}

		if (maxFloor != null) {
			this.maxFloor = maxFloor;
		}

		if (cabinSize != null) {
			this.cabinSize = cabinSize;
		}

		this.currentTick = 0l;

		if (this.cabins != null) {
			this.cabins.clear();
		}

		for (int cabinIndex = 0; cabinIndex < cabinCount; ++cabinIndex) {
			this.cabins.put(cabinIndex, new DefaultCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR));
		}
	}

	@Override
	public boolean isDebug() {
		return this.debug;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	protected String getCabinStatus(Cabin cabin) {
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		if (cabin != null) {
			sb.append(cabin);
		}
		sb.append("}");

		return sb.toString();
	}

	protected String getCabinStatus() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		if (this.cabins != null) {
			for (Entry<Integer, Cabin> currentCabin : this.cabins.entrySet()) {
				sb.append(currentCabin.getKey());
				sb.append(" : ");
				sb.append(this.getCabinStatus(currentCabin.getValue()));
				sb.append(", ");
			}
		}
		sb.append("}");

		return sb.toString();
	}

	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = new TreeMap<String, String>();

		info.put("minFloor", this.minFloor != null ? minFloor.toString() : "");
		info.put("maxFloor", this.maxFloor != null ? maxFloor.toString() : "");
		info.put("cabinCount", this.cabins != null ? Integer.toString(this.cabins.size()) : "");
		info.put("cabinSize", this.cabinSize != null ? cabinSize.toString() : "");
		info.put("debug", Boolean.toString(this.debug));
		info.put("currentTick", Long.toString(this.currentTick));
		info.put("cabins", this.getCabinStatus());

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
	public void thresholds(Integer alertThreshold, Integer panicThreshold) {
		if (this.cabins != null){
			for (Cabin currentCabin : this.cabins.values()) {
				currentCabin.thresholds(alertThreshold, panicThreshold);
			}
		}
	}

}
