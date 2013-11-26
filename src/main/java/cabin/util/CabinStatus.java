package cabin.util;


public class CabinStatus {
	protected Integer id = null;
	protected Integer size = null;
	protected Integer population = null;
	protected CabinState state = null;
	protected String lastDirection = null;
	protected Integer startFloor = null;
	protected Integer currentFloor = null;

	public CabinStatus(Integer id) {
		this(id, null, 0, Direction.UP);
	}

	public CabinStatus(Integer id, Integer size, Integer startFloor, String lastDirection) {
		this.id = id;
		this.reset(size, startFloor, lastDirection);
	}

	public void reset(Integer size, Integer startFloor, String lastDirection) {
		if (size != null) {
			this.size = size;
		}

		if (startFloor != null) {
			this.startFloor = startFloor;
		}

		if (lastDirection != null) {
			this.lastDirection = lastDirection;
		}
	}

	protected Mode getMode(Integer alertThreshold, Integer panicThreshold) {
		Mode mode = Mode.NORMAL;

		if ((panicThreshold != null) && (this.population >= panicThreshold)) {
			mode = Mode.PANIC;
		} else if ((alertThreshold != null) && (this.population >= alertThreshold)) {
			mode = Mode.ALERT;
		}

		return mode;
	}
}
