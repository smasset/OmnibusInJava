package cabin;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeSet;

import cabin.comparator.ClosestOutComparator;
import cabin.util.BoundedCabin;
import cabin.util.Cabin;
import cabin.util.Direction;
import cabin.util.FloorRequest;
import cabin.util.RequestType;
import cabin.util.SelectiveBoundedCabin;

public class MultiBoundedCabinUpAndDownElevator extends MultiCabinUpAndDownElevator {
	public MultiBoundedCabinUpAndDownElevator() {
		this(Elevator.DEFAULT_MIN_FLOOR, Elevator.DEFAULT_MAX_FLOOR, Elevator.DEFAULT_CABIN_SIZE, Elevator.DEFAULT_CABIN_COUNT);
	}

	public MultiBoundedCabinUpAndDownElevator(int minFloor, int maxFloor, Integer cabinSize, Integer cabinCount) {
		super(minFloor, maxFloor, cabinSize, cabinCount);
	}

	@Override
	protected void initCabins(Integer cabinCount) {
		Integer floorRange = (this.maxFloor - this.minFloor + 1) / (cabinCount - 2);

		Integer initFloor = 0;
		Integer lowerLimit = 0;
		Integer upperLimit = 0;

		BoundedCabin currentCabin = null;
		for (int cabinIndex = 0; cabinIndex < cabinCount; ++cabinIndex) {
			if (cabinIndex == 0) {
				currentCabin = new SelectiveBoundedCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR, this.minFloor);
			} else if (cabinIndex == cabinCount - 1) {
				currentCabin = new SelectiveBoundedCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR, this.maxFloor);
			} else {
				initFloor = this.minFloor + (cabinIndex -1) * floorRange;
				lowerLimit = initFloor;
				upperLimit = initFloor + floorRange - 1;

				currentCabin = new SelectiveBoundedCabin(cabinIndex, this.cabinSize, Cabin.DEFAULT_START_FLOOR, initFloor, lowerLimit, upperLimit);
			}

			this.cabins.put(cabinIndex, currentCabin);
		}
	}

	@Override
	protected Integer getNextFloor(Integer cabinId, String direction) {
		Integer nextFloor = null;

		BoundedCabin cabin = (BoundedCabin) this.cabins.get(cabinId);

		if (cabin != null) {
			boolean sortRequests = false;
			boolean serveOnlyOutRequests = false;
			boolean serveOnlySameRequests = false;
			boolean returnDefaultFloor = false;
			int requestDepth = 0;

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

				requestDepth = requestSet.size();
				requestIterator = requestSet.iterator();
			} else {
				NavigableMap<Integer, FloorRequest> nextRequests = null;
				if (Direction.UP.equals(direction)) {
					nextRequests = this.requests.tailMap(cabin.getCurrentFloor(), true);
				} else {
					nextRequests = this.requests.headMap(cabin.getCurrentFloor(), true).descendingMap();
				}

				requestDepth = nextRequests != null ? nextRequests.size() : 0;
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
						if (RequestType.OUT.equals(currentRequest.getType(cabinId)) || cabin.isWithinLimits(currentRequest.getFloor())) {
							nextFloor = currentRequest.getFloor();
						}
					}
				} else {
					nextFloor = currentRequest.getFloor();
				}

				if (returnDefaultFloor && (currentRequest.getCount(cabinId) > 0)) {
					if (cabin.isWithinLimits(currentRequest.getFloor())) {
						defaultFloor = currentRequest.getFloor();
					}
				}
			}

			if (returnDefaultFloor && (nextFloor == null)) {
				nextFloor = defaultFloor;
			}

			if (cabin.isWithinLimits(nextFloor)) {
				cabin.setOpenAllDoors(requestDepth <= 3);
			}
		}

		return nextFloor;
	}
}
