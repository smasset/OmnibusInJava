package cabin;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import cabin.util.Command;

public class MultiCabinElevator implements Elevator {
	private final SortedMap<Integer, Cabin> cabins = new TreeMap<>();

	public MultiCabinElevator(Integer cabinCount) {
		for (int cabinIndex = 0; cabinIndex < cabinCount; ++cabinIndex) {
			this.cabins.put(cabinIndex, new DefaultCabin(cabinIndex));
		}
	}

	@Override
	public Command[] nextCommands() {
		Command[] commands = new Command[cabins.size()];

		for(Entry<Integer, Cabin> currentCabin : cabins.entrySet()) {
			//TODO set destinations for each cabin
			commands[currentCabin.getKey()] = currentCabin.getValue().nextCommand();
		}

		return commands;
	}

	@Override
	public void call(Integer from, String direction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void go(Integer floor, Integer cabin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userHasEntered(Integer cabin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userHasExited(Integer cabin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public String status(boolean pretty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDebug() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDebug(boolean debug) {
		// TODO Auto-generated method stub

	}

	@Override
	public void thresholds(Integer alertThreshold, Integer panicThreshold) {
		// TODO Auto-generated method stub

	}

}
