package cabin;

public class FloorRequest {
	private Integer floor = null;
	private RequestType type = null;
	private Integer count = 0;

	public FloorRequest(Integer floor) {
		this(floor, RequestType.OUT);
	}

	public FloorRequest(Integer floor, RequestType type) {
		this.floor = floor;
		this.type = type;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
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
		boolean hasSameDirection = true;

		switch (direction) {
		case Direction.UP:
			hasSameDirection = !(this.type.equals(RequestType.DOWN));
			break;
		case Direction.DOWN:
			hasSameDirection = !(this.type.equals(RequestType.UP));
			break;
		default:
			break;
		}

		return hasSameDirection;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();

		string.append("floor: ");
		string.append(this.floor);
		string.append("type: ");
		string.append(this.type);
		string.append(", count: ");
		string.append(this.count);

		return string.toString();
	}
}
