package cabin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cabin.util.Direction;
import cabin.util.FloorRequest;

public class MultiCabinYoungAndRestlessElevator extends MultiCabinElevator {

	protected Integer ageLimit = null;
	protected Map<Integer, FloorRequest> oldRequests = new ConcurrentHashMap<Integer, FloorRequest>();
	protected Map<Integer, FloorRequest> youngRequests = new ConcurrentHashMap<Integer, FloorRequest>();

	public MultiCabinYoungAndRestlessElevator(Integer cabinCount) {
		this(null, null, null, cabinCount);
	}

	public MultiCabinYoungAndRestlessElevator(Integer minFloor, Integer maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
		this.ageLimit = 10;
	}

	@Override
	public Integer getNextFloor(Integer cabinId) {
		Integer nextFloor = null;

		Cabin currentCabin = this.cabins.get(cabinId);
		if (currentCabin != null) {
			Integer currentFloor = currentCabin.getCurrentFloor();
		}

		return nextFloor;
	}

	private boolean removeRequest(Integer floor, Map<Integer, FloorRequest> requests, String direction) {
		boolean removed = false;
		FloorRequest currentFloorRequest = requests.get(floor);

		if (currentFloorRequest != null) {
			removed = true;

			if (currentFloorRequest.getCount() == 1) {
				requests.remove(floor);
			} else {
				requests.put(floor, currentFloorRequest.decrementCount(direction));
			}
		}

		return removed;
	}

	private void removeRequest(Integer floor, String direction) {
		this.removeRequest(floor, this.youngRequests, direction);
		this.removeRequest(floor, this.oldRequests, direction);
	}

	private boolean addRequest(Map<Integer, FloorRequest> requests, FloorRequest defaultRequest, Integer floor, String direction) {
		boolean added = false;

		if ((floor >= this.minFloor) && (floor <= this.maxFloor)) {
			added = true;

			FloorRequest newRequest = requests.get(floor);
			if (newRequest == null) {
				if (defaultRequest != null) {
					newRequest = defaultRequest;
				} else {
					newRequest = new FloorRequest(floor);
				}
			}

			newRequest.setLatestBirthDate(this.currentTick);
			requests.put(floor, newRequest.incrementCount(direction));
		}

		return added;
	}

	private void addRequest(Integer currentFloor, Integer floor, String direction) {
		if (Math.abs(floor - currentFloor) <= this.ageLimit) {
			this.addRequest(this.youngRequests, this.oldRequests.get(floor), floor, direction);
		}
		this.addRequest(this.oldRequests, null, floor, direction);
	}

	@Override
	public void go(Integer floor, Integer cabin) {
		Cabin currentCabin = this.cabins.get(cabin);

		if (currentCabin != null) {
			Integer currentFloor = currentCabin.getCurrentFloor();

			// Remove current floor request
			String direction = null;

			int compareTo = currentFloor.compareTo(floor);
			if (compareTo > 0) {
				direction = Direction.DOWN;
			} else if (compareTo < 0) {
				direction = Direction.UP;
			}

			this.removeRequest(currentFloor, direction);
			this.addRequest(currentFloor, floor, null);
		}
	}

	@Override
	public void call(Integer from, String direction) {
		// TODO handle null cabin
		this.addRequest(null, from, direction);
	}

	@Override
	public void userHasExited(Integer cabin) {
		super.userHasExited(cabin);
		this.removeRequest(cabin, null);
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabinCount);
		this.oldRequests.clear();
		this.youngRequests.clear();
	}
}
