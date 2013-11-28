package cabin.comparator;

import cabin.Cabin;
import cabin.util.FloorRequest;
import cabin.util.RequestType;

public class MultiCabinYoungAndRestlessComparator extends AbstractFloorRequestComparator {
	private final Integer cabinId;

	public MultiCabinYoungAndRestlessComparator(Cabin cabin) {
		super(cabin.getMode(), cabin.getCurrentFloor(), null);
		this.cabinId = cabin.getId();
	}

	@Override
	protected Double getScore(FloorRequest request) {
		Double score = request.getAbsoluteDistance(this.currentFloor).doubleValue();
		Double penalty = 0d;

		switch (this.mode) {

		case NORMAL:
			if (!RequestType.OUT.equals(request.getType())) {
				penalty = 5d;
			} else if (request.getOutCount(this.cabinId) == 0){
				penalty = 100d;
			}
			break;

		default:
			if (!RequestType.OUT.equals(request.getType())) {
				penalty = 100d;
			} else if (request.getOutCount(this.cabinId) == 0){
				penalty = 100d;
			}
			break;
		}

		return score + penalty;
	}

}
