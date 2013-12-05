package cabin.util;

public class SelectiveBoundedCabin extends DefaultBoundedCabin {
	public SelectiveBoundedCabin(Integer id, Integer size, Integer startFloor, Integer initFloor) {
		this(id, size, startFloor, initFloor, null, null);
	}

	public SelectiveBoundedCabin(Integer id, Integer size, Integer startFloor, Integer initFloor, Integer lowerLimit, Integer upperLimit) {
		super(id, size, startFloor, initFloor, lowerLimit, upperLimit);
		this.selectOpenDirection = true;
	}
}
