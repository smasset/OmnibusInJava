package cabin;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import cabin.util.Command;

public class MultiCabinElevator implements Elevator {
	protected final SortedMap<Integer, Cabin> cabins = new TreeMap<>();
	protected Integer minFloor = null;
	protected Integer maxFloor = null;
	protected Integer cabinSize = null;

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

		for(Entry<Integer, Cabin> currentCabin : cabins.entrySet()) {
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
		if (minFloor != null) {
			this.minFloor = minFloor;
		}

		if (maxFloor != null) {
			this.maxFloor = maxFloor;
		}

		if (cabinSize != null) {
			this.cabinSize = cabinSize;
		}

		this.cabins.clear();
		for (int cabinIndex = 0; cabinIndex < cabinCount; ++cabinIndex) {
			this.cabins.put(cabinIndex, new DefaultCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR));
		}
	}

	@Override
	public String status(boolean pretty) {
		return "";
	}

	@Override
	public boolean isDebug() {
		return false;
	}

	@Override
	public void setDebug(boolean debug) {
	}

	@Override
	public void thresholds(Integer alertThreshold, Integer panicThreshold) {
	}

}
