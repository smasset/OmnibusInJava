package cabin;

import cabin.util.CabinState;
import cabin.util.Command;

public class DefaultCabin implements Cabin {
	protected Integer id = null;
	protected Integer size = null;
	protected Integer startFloor = null;

	protected Integer currentFloor = null;
	protected Integer population = null;
	protected Integer nextFloor = null;
	protected CabinState state = null;

	public DefaultCabin(Integer id) {
		this(id, null, 0);
	}

	public DefaultCabin(Integer id, Integer size, Integer startFloor) {
		this.id = id;
		this.size = size;
		this.startFloor = startFloor;
		this.reset();
	}

	private void reset() {
		this.currentFloor = startFloor;
		this.population = 0;
		this.nextFloor = null;
		this.state = CabinState.STOPPED;
	}

	@Override
	public Command nextCommand() {
		Command nextCommand = Command.NOTHING;

		switch (this.state) {

		case STOPPED:
			if ((this.nextFloor != null) && (this.currentFloor != null)) {
				int comparison = Integer.compare(this.currentFloor, nextFloor);

				if (comparison == 0) {
					nextCommand = Command.OPEN;
					this.state = CabinState.OPENED;
				} else if (comparison > 0) {
					this.currentFloor--;
					nextCommand = Command.DOWN;
				} else {
					this.currentFloor++;
					nextCommand = Command.UP;
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
	public void setNextFloor(Integer nextFloor) {
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
}
