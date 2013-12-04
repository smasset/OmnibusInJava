package cabin.util;

public class SelectiveCabin extends DefaultCabin {

	public SelectiveCabin(Integer id) {
		this(id, Cabin.DEFAULT_CABIN_SIZE, Cabin.DEFAULT_START_FLOOR);
	}

	public SelectiveCabin(Integer id, Integer size, Integer startFloor) {
		this(id, size, startFloor, null);
	}

	public SelectiveCabin(Integer id, Integer size, Integer startFloor, Integer initFloor) {
		super(id, size, startFloor, initFloor);
		this.selectOpenDirection = true;
	}
}
