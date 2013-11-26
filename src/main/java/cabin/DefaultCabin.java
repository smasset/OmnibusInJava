package cabin;

import java.util.Map;

import cabin.util.CabinState;
import cabin.util.Command;
import cabin.util.Direction;
import cabin.util.FloorRequest;
import cabin.util.Mode;

public class DefaultCabin implements Cabin {
	protected Integer id = null;
	protected Integer size = null;
	protected Integer population = null;
	protected CabinState state = null;
	protected String lastDirection = null;
	protected Integer startFloor = null;
	protected Integer currentFloor = null;
	protected Integer alertThreshold = null;
	protected Integer panicThreshold = null;
	protected Map<Integer, FloorRequest> requests = null;

	public DefaultCabin(Integer id) {
		this(id, null, 0, Direction.UP);
	}

	public DefaultCabin(Integer id, Integer size, Integer startFloor, String lastDirection) {
		this.id = id;
		this.reset(size, startFloor, lastDirection);
	}

	public void reset(Integer size, Integer startFloor, String lastDirection) {
	}

	public void setRequests(Map<Integer, FloorRequest> requests) {
		this.requests = requests;
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
	}

	@Override
	public void userHasExited() {
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause) {
		if (cabinSize != null) {
			this.size = cabinSize;
		}

		this.startFloor = 0;
		this.lastDirection = Direction.UP;
	}

	@Override
	public Mode getMode() {
		Mode mode = Mode.NORMAL;

		if ((this.panicThreshold != null) && (this.population >= this.panicThreshold)) {
			mode = Mode.PANIC;
		} else if ((this.alertThreshold != null) && (this.population >= this.alertThreshold)) {
			mode = Mode.ALERT;
		}

		return mode;
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
		if (alertThreshold != null) {
			this.alertThreshold = alertThreshold;
		}

		if (panicThreshold != null) {
			this.panicThreshold = panicThreshold;
		}
	}
}
