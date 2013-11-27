package cabin;

public class MultiCabinOmnibusElevator extends MultiCabinElevator {
	private int count = 0;
	private boolean up = true;

	public MultiCabinOmnibusElevator(Integer cabinCount) {
		this(null, null, null, cabinCount);
	}

	public MultiCabinOmnibusElevator(Integer minFloor, Integer maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
	}

	@Override
	public Integer getNextFloor(Integer cabinId) {
		Integer nextFloor = null;

		Cabin currentCabin = this.cabins.get(cabinId);
		if (currentCabin != null) {
			Integer currentFloor = currentCabin.getCurrentFloor();
			switch (count % 3) {
			case 0:
				nextFloor = currentFloor;
				break;
			default:
				if (up) {
					if (currentFloor < this.maxFloor) {
						nextFloor = currentFloor + 1;
					} else {
						nextFloor = currentFloor - 1;
						up = false;
					}
				} else {
					if (currentFloor > this.minFloor) {
						nextFloor = currentFloor - 1;
					} else {
						nextFloor = currentFloor + 1;
						up = true;
					}
				}
				break;
			}
			this.count++;
		}

		return nextFloor;
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabinCount);
		this.up = true;
	}
}
