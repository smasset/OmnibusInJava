package cabin.comparator;

import cabin.util.FloorRequest;
import cabin.util.Mode;
import cabin.util.RequestType;

public class YoungAndRestlessComparator extends AbstractFloorRequestComparator {

	public YoungAndRestlessComparator(Mode mode, Integer currentFloor) {
		super(mode, currentFloor, null);
	}

	@Override
	protected Double getScore(FloorRequest request) {
		Double score = request.getAbsoluteDistance(this.currentFloor).doubleValue();
		Double penalty = 0d;

		switch (this.mode) {

		case NORMAL:
			if (!RequestType.OUT.equals(request.getType())) {
				penalty = 5d;
			}
			break;

		default:
			if (!RequestType.OUT.equals(request.getType())) {
				penalty = 100d;
			}
			break;
		}

		return score + penalty;
	}

}
