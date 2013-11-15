package cabin;

public class FloorRequest implements Comparable<FloorRequest>{
	private Integer floor = null;
	private RequestType type = null;
	private Integer count = 0;
	private Integer relativeCount = 0;

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

	public Integer getRelativeCount() {
		return relativeCount;
	}

	public void setRelativeCount(Integer relativeCount) {
		this.relativeCount = relativeCount;
	}

	private void addCount(int increment) {
		this.count += increment;
	}

	private void addRelativeCount(int increment) {
		this.relativeCount += increment;
	}

	public FloorRequest incrementCount() {
		this.addCount(1);
		return this;
	}

	public FloorRequest incrementRelativeCount() {
		this.addRelativeCount(1);
		return this;
	}

	public FloorRequest decrementCount() {
		this.addCount(-1);
		return this;
	}

	public FloorRequest decrementRelativeCount() {
		this.addRelativeCount(-1);
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
		string.append("; type: ");
		string.append(this.type);
		string.append("; count: ");
		string.append(this.count);
		string.append("; relativeCount: ");
		string.append(this.relativeCount);

		return string.toString();
	}

	@Override
	public int compareTo(FloorRequest o) {
		return this.relativeCount.compareTo(o.getRelativeCount());
	}
}
