package cabin;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cabin.util.Cabin;
import cabin.util.Command;
import cabin.util.DefaultCabin;

public class MultiCabinElevator implements Elevator {
	private static final Logger logger = Logger.getLogger(MultiCabinElevator.class);

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
		Command[] commands = new Command[cabins.size()];

		Integer cabinId = null;
		Cabin cabin = null;
		Integer nextFloor = null;
		Command command = null;
		for (Entry<Integer, Cabin> currentCabin : cabins.entrySet()) {
			cabinId = currentCabin.getKey();
			cabin = currentCabin.getValue();

			nextFloor = this.getNextFloor(cabinId);
			cabin.setNextFloor(nextFloor);
			command = cabin.nextCommand();

			if ((this.minFloor.equals(nextFloor)) && (Command.OPEN_DOWN.equals(command))) {
				command = Command.OPEN_UP;
			} else if ((this.maxFloor.equals(nextFloor)) && (Command.OPEN_UP.equals(command))) {
				command = Command.OPEN_DOWN;
			}
			commands[currentCabin.getKey()] = command;
		}
		this.currentTick++;

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

	protected void initCabins(Integer cabinCount) {
		for (int cabinIndex = 0; cabinIndex < cabinCount; ++cabinIndex) {
			this.cabins.put(cabinIndex, new DefaultCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR));
		}
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		logger.info("Reset : " + cause + " ; status : " + this.status(false));

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
		this.initCabins(cabinCount);
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