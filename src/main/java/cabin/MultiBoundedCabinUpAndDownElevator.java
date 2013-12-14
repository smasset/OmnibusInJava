package cabin;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
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
	protected Deque<FloorRequest> getNextFloors(Cabin cabin, String direction) {
		return this.getNextFloors((BoundedCabin) cabin);
	}

	protected Deque<FloorRequest> getNextFloors(BoundedCabin cabin, String direction) {
		Deque<FloorRequest> nextFloors = new LinkedList<>();

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

			FloorRequest defaultFloor = null;
			FloorRequest currentRequest = null;
			while (requestIterator.hasNext()) {
				currentRequest = requestIterator.next();

				if (serveOnlyOutRequests) {
					if (RequestType.OUT.equals(currentRequest.getType(cabin.getId()))) {
						nextFloors.add(currentRequest);
					}
				} else if (serveOnlySameRequests) {
					if (currentRequest.hasSameDirection(cabin.getId(), direction)) {
						if (RequestType.OUT.equals(currentRequest.getType(cabin.getId())) || cabin.isWithinLimits(currentRequest.getFloor())) {
							nextFloors.add(currentRequest);
						}
					}
				} else {
					nextFloors.add(currentRequest);
				}

				if (returnDefaultFloor && (currentRequest.getCount(cabin.getId()) > 0)) {
					if (cabin.isWithinLimits(currentRequest.getFloor())) {
						defaultFloor = currentRequest;
					}
				}
			}

			if (returnDefaultFloor && !nextFloors.contains(defaultFloor)) {
				nextFloors.add(defaultFloor);
			}
		}

		return nextFloors;
	}
}
