package cabin;

public class FloorRequest {
	private Integer floor = null;
	private String direction = null;
	private Integer count = 0;

	public FloorRequest(Integer floor) {
		this(floor, null);
	}

	public FloorRequest(Integer floor, String direction) {
		this.floor = floor;
		this.direction = direction;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	private void addCount(int increment) {
		this.count += increment;
	}

	public FloorRequest incrementCount() {
		this.addCount(1);
		return this;
	}

	public FloorRequest decrementCount() {
		this.addCount(-1);
		return this;
	}

	public boolean hasSameDirection(String direction) {
		return this.direction == null|| direction.equals(this.direction);
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();

		string.append("floor: ");
		string.append(this.floor);
		string.append("direction: ");
		string.append(this.direction);
		string.append(", count: ");
		string.append(this.count);

		return string.toString();
	}
}
