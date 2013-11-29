package cabin;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import cabin.comparator.ClosestOutComparator;
import cabin.util.Cabin;
import cabin.util.Direction;
import cabin.util.FloorRequest;
import cabin.util.RequestType;
import cabin.util.SelectiveCabin;

public class MultiCabinUpAndDownElevator extends MultiCabinElevator {
	protected NavigableMap<Integer, FloorRequest> requests = new ConcurrentSkipListMap<Integer, FloorRequest>();

	public MultiCabinUpAndDownElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE, Elevator.DEFAULT_CABIN_COUNT);
	}

	public MultiCabinUpAndDownElevator(int minFloor, int maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
	}

	protected void initCabins(Integer cabinCount) {
		for (int cabinIndex = 0; cabinIndex < cabinCount; ++cabinIndex) {
			this.cabins.put(cabinIndex, new SelectiveCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR));
		}
	}

	private void removeRequest(Integer cabinId, String direction) {
		Cabin cabin = this.cabins.get(cabinId);

		if (cabin != null) {
			Integer currentFloor = cabin.getCurrentFloor();
			FloorRequest currentFloorRequest = this.requests.get(currentFloor);

			if (currentFloorRequest != null) {
				if (currentFloorRequest.getCount() == 1) {
					this.requests.remove(currentFloor);
				} else {
					this.requests.put(currentFloor, currentFloorRequest.decrementCount(cabinId, direction));
				}
			}
		}
	}

	private Integer getNextFloor(Integer cabinId, String direction) {
		Integer nextFloor = null;

		Cabin cabin = this.cabins.get(cabinId);

		if (cabin != null) {
			boolean sortRequests = false;
			boolean serveOnlyOutRequests = false;
			boolean serveOnlySameRequests = false;
			boolean returnDefaultFloor = false;

			switch (cabin.getMode()) {
			case PANIC:
				sortRequests = true;

			case ALERT:
				serveOnlyOutRequests = true;
				break;

			case NORMAL:
			default:
				serveOnlySameRequests = true;
				returnDefaultFloor = true;
				break;
			}

			Iterator<FloorRequest> requestIterator = null;
			if (sortRequests) {
				SortedSet<FloorRequest> requestSet = new TreeSet<>(new ClosestOutComparator(cabin, this.currentTick));
				requestSet.addAll(this.requests.values());
				requestIterator = requestSet.iterator();
			} else {
				NavigableMap<Integer, FloorRequest> nextRequests = null;
				if (Direction.UP.equals(direction)) {
					nextRequests = this.requests.tailMap(cabin.getCurrentFloor(), true);
				} else {
					nextRequests = this.requests.headMap(cabin.getCurrentFloor(), true).descendingMap();
				}

				requestIterator = nextRequests.values().iterator();
			}

			Integer defaultFloor = null;
			FloorRequest currentRequest = null;
			while ((nextFloor == null) && (requestIterator.hasNext())) {
				currentRequest = requestIterator.next();

				if (serveOnlyOutRequests) {
					if (RequestType.OUT.equals(currentRequest.getType(cabinId))) {
						nextFloor = currentRequest.getFloor();
					}
				} else if (serveOnlySameRequests) {
					if (currentRequest.hasSameDirection(cabinId, direction)) {
						nextFloor = currentRequest.getFloor();
					}
				} else {
					nextFloor = currentRequest.getFloor();
				}

				if ((defaultFloor == null) && (currentRequest.getType() != null)) {
					defaultFloor = currentRequest.getFloor();
				}
			}

			if (returnDefaultFloor && (nextFloor == null)) {
				nextFloor = defaultFloor;

//				if (Direction.UP.equals(direction)) {
//					nextFloor = this.requests.higherKey(cabin.getCurrentFloor());
//				} else {
//					nextFloor = this.requests.lowerKey(cabin.getCurrentFloor());
//				}
			}
		}

		return nextFloor;
	}

	@Override
	public Integer getNextFloor(Integer cabinId) {
		Integer nextFloor = null;

		Cabin cabin = this.cabins.get(cabinId);
		if (cabin != null) {

			Integer halfwayFloor = Double.valueOf(Math.ceil((this.maxFloor + this.minFloor) / 2d)).intValue();
			Long halfwayTick = Long.valueOf(halfwayFloor - cabin.getStartFloor());
			
			if (this.currentTick < halfwayTick) {
				nextFloor = halfwayFloor;
			} else if ((cabinId == 0) && (this.currentTick == halfwayTick)) {
				nextFloor = halfwayFloor - 1; 
			} else {
				switch (cabin.getLastDirection()) {

				case Direction.UP:
					nextFloor = this.getNextFloor(cabinId, Direction.UP);
					if (nextFloor == null) {
						nextFloor = this.getNextFloor(cabinId, Direction.DOWN);
						if (nextFloor !=null) {
							cabin.setLastDirection(Direction.DOWN);
						}
					}
					break;

				case Direction.DOWN:
					nextFloor = this.getNextFloor(cabinId, Direction.DOWN);
					if (nextFloor == null) {
						nextFloor = this.getNextFloor(cabinId, Direction.UP);
						if (nextFloor !=null) {
							cabin.setLastDirection(Direction.UP);
						}
					}
					break;

				default:
					break;
				}
			}
		}

		return nextFloor;
	}

	@Override
	public void go(Integer floor, Integer cabinId) {
		Cabin cabin = this.cabins.get(cabinId);

		if (cabin != null) {
			// Remove current floor request
			String direction = null;

			int compareTo = cabin.getCurrentFloor().compareTo(floor);
			if (compareTo > 0) {
				direction = Direction.DOWN;
			} else if (compareTo < 0) {
				direction = Direction.UP;
			}

			this.removeRequest(cabinId, direction);

			// Add new request
			if ((floor >= this.minFloor) && (floor <= this.maxFloor)) {

				FloorRequest newRequest = this.requests.get(floor);
				if (newRequest == null) {
					newRequest = new FloorRequest(floor);
				}

				newRequest.setLatestBirthDate(this.currentTick);
				this.requests.put(floor, newRequest.incrementCount(cabinId, null));
			}
		}
	}

	@Override
	public void call(Integer from, String direction) {
		if ((from >= this.minFloor) && (from <= this.maxFloor)) {

			FloorRequest newRequest = this.requests.get(from);
			if (newRequest == null) {
				newRequest = new FloorRequest(from);
			}

			newRequest.setLatestBirthDate(this.currentTick);
			this.requests.put(from, newRequest.incrementCount(direction));
		}
	}

	@Override
	public void userHasExited(Integer cabinId) {
		super.userHasExited(cabinId);
		this.removeRequest(cabinId, null);
	}

	@Override
	public void userHasEntered(Integer cabinId) {
		super.userHasEntered(cabinId);
	}

	@Override
	public void reset(Integer minFloor, Integer maxFloor, Integer cabinSize, String cause, Integer cabinCount) {
		super.reset(minFloor, maxFloor, cabinSize, cause, cabinCount);
		if (this.requests != null) {
			this.requests.clear();
		}
	}

	@Override
	protected Map<String, String> getStatusInfo() {
		Map<String, String> info = super.getStatusInfo();

		info.put("requests", this.requests.descendingMap().toString());

		return info;
	}

}
