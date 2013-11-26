package cabin.util;


public class FloorRequest {
	private Integer floor = null;

	private Integer outCount = 0;
	private Integer upCount = 0;
	private Integer downCount = 0;

	private Long latestBirthDate = null;

	public FloorRequest(Integer floor) {
		this.floor = floor;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public void setLatestBirthDate(Long latestBirthDate) {
		this.latestBirthDate = latestBirthDate;
	}

	public RequestType getType() {
		RequestType type = null;

		if (this.outCount > 0) {
			type = RequestType.OUT;
		} else if (this.upCount > 0) {
			type = this.downCount > 0 ? RequestType.UP_DOWN : RequestType.UP;
		} else if (this.downCount > 0) {
			type = this.upCount > 0 ? RequestType.UP_DOWN : RequestType.DOWN;
		}

		return type;
	}

	public Long getAge(Long currentTick) {
		Long age = this.latestBirthDate;

		if (currentTick != null) {
			age = currentTick - this.latestBirthDate;
		}

		return age;
	}

	public Integer getOutCount() {
		return this.outCount;
	}

	public Integer getCount() {
		return this.upCount + this.downCount + this.outCount;
	}

	public Integer getRelativeCount() {
		return this.upCount + this.downCount - this.outCount;
	}

	private void addCount(String direction, int increment) {
		if (direction != null) {

			switch (direction) {
			case Direction.UP:
				this.upCount += increment;
				break;

			case Direction.DOWN:
				this.downCount += increment;
				break;

			default:
				break;
			}

		} else {
			this.outCount += increment;
		}
	}

	public FloorRequest incrementCount(String direction) {
		this.addCount(direction, 1);
		return this;
	}

	public FloorRequest decrementCount(String direction) {
		this.addCount(direction, -1);
		return this;
	}

	public boolean hasSameDirection(String direction) {
		boolean hasSameDirection = true;

		switch (direction) {
		case Direction.UP:
			hasSameDirection = !(this.getType().equals(RequestType.DOWN));
			break;
		case Direction.DOWN:
			hasSameDirection = !(this.getType().equals(RequestType.UP));
			break;
		default:
			break;
		}

		return hasSameDirection;
	}

	public Integer getRelativeDistance(Integer currentFloor) {
		Integer distance = this.floor;

		if (currentFloor != null) {
			distance = this.floor - currentFloor;
		}

		return distance;
	}

	public Integer getAbsoluteDistance(Integer currentFloor) {
		return Math.abs(this.getRelativeDistance(currentFloor));
	}

	@Override
	public String toString() {
		return this.toString(null, null);
	}

	public String toString(Long currentTick, Integer currentFloor) {
		StringBuilder string = new StringBuilder();

		string.append("floor: ");
		string.append(this.floor);
		string.append("; type: ");
		string.append(this.getType());
		string.append("; count: ");
		string.append(this.getCount());
		string.append("; outCount: ");
		string.append(this.outCount);
		string.append("; upCount: ");
		string.append(this.upCount);
		string.append("; downCount: ");
		string.append(this.downCount);
		string.append("; relativeCount: ");
		string.append(this.getRelativeCount());
		string.append("; age: ");
		string.append(this.getAge(currentTick));
		string.append("; distance: ");
		string.append(this.getAbsoluteDistance(currentFloor));

		return string.toString();
	}
}
