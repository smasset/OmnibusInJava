package cabin.util;

import java.util.HashMap;
import java.util.Map;

public class FloorRequest {
	private Integer floor = null;

	private Map<Integer, Integer> outCounts = new HashMap<>();
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
		return this.getType(null);
	}

	public RequestType getType(Integer cabinId) {
		RequestType type = null;

		if (this.getOutCount(cabinId) > 0) {
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
		return this.getOutCount(null);
	}

	public Integer getOutCount(Integer cabinId) {
		Integer count = null;

		if (cabinId != null) {
			count = this.outCounts.get(cabinId);
		} else {
			count = 0;
			if (this.outCounts != null) {
				for (Integer currentCount : this.outCounts.values()) {

					if (currentCount != null) {
						count += currentCount;
					}
				}
			}
		}

		return count != null ? count : 0;
	}

	public Integer getCount() {
		return this.upCount + this.downCount + this.getOutCount();
	}

	public Integer getRelativeCount() {
		return this.upCount + this.downCount - this.getOutCount();
	}

	private void addCount(Integer cabinId, String direction, int increment) {
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
			if (cabinId != null) {
				Integer newCount = increment;

				Integer currentCount = this.outCounts.get(cabinId);
				if (currentCount != null) {
					newCount += currentCount; 
				}

				if (newCount > 0) {
					this.outCounts.put(cabinId, newCount);
				} else {
					this.outCounts.remove(cabinId);
				}
			}
		}
	}

	public FloorRequest incrementCount(String direction) {
		return this.incrementCount(0, direction);
	}

	public FloorRequest incrementCount(Integer cabinId, String direction) {
		this.addCount(cabinId, direction, 1);
		return this;
	}

	public FloorRequest decrementCount(String direction) {
		return this.decrementCount(0, direction);
	}

	public FloorRequest decrementCount(Integer cabinId, String direction) {
		this.addCount(cabinId, direction, -1);
		return this;
	}

	public boolean hasSameDirection(String direction) {
		return this.hasSameDirection(null, direction);
	}

	public boolean hasSameDirection(Integer cabinId, String direction) {
		boolean hasSameDirection = false;

		RequestType type = this.getType(cabinId);

		switch (direction) {
		case Direction.UP:
			hasSameDirection = !(type == null || type.equals(RequestType.DOWN));
			break;
		case Direction.DOWN:
			hasSameDirection = !(type == null || type.equals(RequestType.UP));
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
		string.append("; outCounts: ");
		string.append(this.outCounts);
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
