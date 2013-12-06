package cabin.util;

import java.util.Map;

public class DefaultBoundedCabin extends DefaultCabin implements BoundedCabin {
	protected Integer lowerLimit = null;
	protected Integer upperLimit = null;

	public DefaultBoundedCabin(Integer id, Integer size, Integer startFloor, Integer initFloor) {
		this(id, size, startFloor, initFloor, null, null);
	}

	public DefaultBoundedCabin(Integer id, Integer size, Integer startFloor, Integer initFloor, Integer lowerLimit, Integer upperLimit) {
		super(id, size, startFloor, initFloor);
		this.reset(size, lowerLimit, upperLimit);
	}

	@Override
	public Integer getUpperLimit() {
		return this.upperLimit;
	}

	@Override
	public void setUpperLimit(Integer upperLimit) {
		this.upperLimit = upperLimit;
	}

	@Override
	public Integer getLowerLimit() {
		return this.lowerLimit;
	}

	@Override
	public void setLowerLimit(Integer lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	@Override
	public void reset(Integer size) {
		this.reset(size, null, null);
	}

	@Override
	public void reset(Integer size, Integer lowerLimit, Integer upperLimit) {
		super.reset(size);
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("lowerLimit", this.lowerLimit != null ? this.lowerLimit.toString() : "");
		info.put("upperLimit", this.upperLimit != null ? this.upperLimit.toString() : "");

		return info;
	}

	@Override
	public boolean isWithinLimits(Integer floor) {
		boolean isWithinLimits = false;

		if (floor != null) {
			isWithinLimits = true;

			isWithinLimits &= ((this.lowerLimit == null) || (floor >= this.lowerLimit));
			isWithinLimits &= ((this.upperLimit == null) || (floor <= this.upperLimit));
		}

		return isWithinLimits;
	}

	@Override
	public Command nextCommand() {
		Command command = super.nextCommand();

		switch(command) {
		case OPEN:
		case OPEN_DOWN:
		case OPEN_UP:
			if (!this.isWithinLimits(this.currentFloor)) {
				if (this.currentFloor < this.lowerLimit) {
					command = Command.OPEN_UP;
				} else {
					command = Command.OPEN_DOWN;
				}
			}
			break;
		default:
			break;
		}

		return command;
	}
}
