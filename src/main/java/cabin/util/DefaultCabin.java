package cabin.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.ObjectUtils;

public class DefaultCabin implements Cabin {
	protected Integer id = null;
	protected Integer size = null;
	protected Integer startFloor = null;

	protected Integer currentFloor = null;
	protected Integer population = null;
	protected Integer nextFloor = null;
	protected CabinState state = null;
	protected Integer alertThreshold = null;
	protected Integer panicThreshold = null;
	protected String lastDirection = null;
	protected boolean selectOpenDirection = false;
	protected boolean openAllDoors = false;
	protected Integer sameFloorCount = null;

	public DefaultCabin(Integer id) {
		this(id, Cabin.DEFAULT_CABIN_SIZE, Cabin.DEFAULT_START_FLOOR);
	}

	public DefaultCabin(Integer id, Integer size, Integer startFloor) {
		this.id = id;
		this.startFloor = startFloor;
		this.reset(size);
	}

	private void addPopulsation(Integer increment) {
		this.population += increment;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public Command nextCommand() {
		Command nextCommand = Command.NOTHING;

		switch (this.state) {

		case STOPPED:
			if ((this.nextFloor != null) && (this.currentFloor != null)) {
				int comparison = Integer.compare(this.currentFloor, nextFloor);

				if (comparison == 0) {
					if (this.sameFloorCount > 2) {
						this.lastDirection = Direction.UP.equals(this.lastDirection) ? Direction.DOWN : Direction.UP;
						this.sameFloorCount = 0;
					}

					if (this.selectOpenDirection && !this.openAllDoors) {
						switch (this.lastDirection) {

						case Direction.UP:
							nextCommand = Command.OPEN_UP;
							break;

						case Direction.DOWN:
							nextCommand = Command.OPEN_DOWN;
							break;

						default:
							nextCommand = Command.OPEN;
							break;
						}
					} else {
						nextCommand = Command.OPEN;
					}

					this.state = CabinState.OPENED;
				} else if (comparison > 0) {
					this.currentFloor--;
					nextCommand = Command.DOWN;
					this.lastDirection = Direction.DOWN;
				} else {
					this.currentFloor++;
					nextCommand = Command.UP;
					this.lastDirection = Direction.UP;
				}
			}
			break;

		case OPENED:
			nextCommand = Command.CLOSE;
			this.state = CabinState.STOPPED;
			break;

		default:
			break;
		}

		return nextCommand;
	}

	@Override
	public Integer getStartFloor() {
		return this.startFloor;
	}

	@Override
	public void setNextFloor(Integer nextFloor) {
		if (ObjectUtils.equals(nextFloor, this.currentFloor)) {
			this.sameFloorCount++;
		} else {
			this.sameFloorCount=0;
		}

		this.nextFloor = nextFloor;
	}

	@Override
	public Integer getCurrentFloor() {
		return this.currentFloor;
	}

	@Override
	public Integer getPopulation() {
		return this.population;
	}

	@Override
	public void getIn() {
		this.addPopulsation(1);
	}

	@Override
	public void getOut() {
		this.addPopulsation(-1);
	}

	protected void setThresholds() {
		this.thresholds(null, null);
	}

	@Override
	public void thresholds(Integer alertThreshold, Integer panicThreshold) {
		if (alertThreshold != null) {
			this.alertThreshold = alertThreshold;
		} else if (this.size != null) {
			this.alertThreshold = Double.valueOf(Math.ceil(0.8d * this.size)).intValue();
		}

		if (panicThreshold != null) {
			this.panicThreshold = panicThreshold;
		} else if (this.size != null) {
			this.panicThreshold = new Integer(this.size);
		}
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
	public void setOpenAllDoors(boolean openAllDoors) {
		this.openAllDoors = openAllDoors;
	}

	@Override
	public String getLastDirection() {
		return this.lastDirection;
	}

	@Override
	public void setLastDirection(String lastDirection) {
		this.lastDirection = lastDirection;
	}

	@Override
	public void reset(Integer size) {
		if (size != null) {
			this.size = size;
		}

		this.setThresholds();
		this.currentFloor = startFloor;
		this.population = 0;
		this.nextFloor = null;
		this.state = CabinState.STOPPED;
		this.sameFloorCount = 0;
	}

	protected Map<String, String> getStatusInfo() {
		TreeMap<String, String> info = new TreeMap<>();

		info.put("id", this.id != null ? this.id.toString() : "");
		info.put("size", this.size != null ? this.size.toString() : "");
		info.put("startFloor", this.startFloor != null ? this.startFloor.toString() : "");
		info.put("currentFloor", this.currentFloor != null ? this.currentFloor.toString() : "");
		info.put("population", this.population != null ? this.population.toString() : "");
		info.put("nextFloor", this.nextFloor != null ? this.nextFloor.toString() : "");
		info.put("state", this.state != null ? this.state.toString() : "");
		info.put("alertThreshold", this.alertThreshold != null ? this.alertThreshold.toString() : "");
		info.put("panicThreshold", this.panicThreshold != null ? this.panicThreshold.toString() : "");
		info.put("mode", this.getMode() != null ? this.getMode().toString() : "");
		info.put("lastDirection", this.lastDirection != null ? this.lastDirection : "");
		info.put("selectOpenDirection", Boolean.toString(this.selectOpenDirection));
		info.put("openAllDoors", Boolean.toString(this.openAllDoors));
		info.put("sameFloorCount", this.sameFloorCount != null ? this.sameFloorCount.toString() : "");

		return info;
	}

	@Override
	public String toString(boolean pretty) {
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
	public String toString() {
		return this.toString(false);
	}
}
