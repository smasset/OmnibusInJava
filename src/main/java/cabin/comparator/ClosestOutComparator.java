package cabin.comparator;

import cabin.util.Cabin;
import cabin.util.FloorRequest;
import cabin.util.Mode;
import cabin.util.RequestType;

public class ClosestOutComparator extends AbstractFloorRequestComparator {
	private final Integer cabinId;

	public ClosestOutComparator(Mode mode, Integer currentFloor, Long currentTick) {
		super(mode, currentFloor, currentTick);
		this.cabinId = null;
	}

	public ClosestOutComparator(Cabin cabin, Long currentTick) {
		super(cabin.getMode(), cabin.getCurrentFloor(), currentTick);
		this.cabinId = cabin != null ? cabin.getId() : null;
	}

	@Override
	protected Double getScore(FloorRequest request) {
		Double score = request.getAbsoluteDistance(this.currentFloor).doubleValue();

		if (!RequestType.OUT.equals(request.getType(cabinId))) {
			score += 100d;
		}

		return score;
	}

}
