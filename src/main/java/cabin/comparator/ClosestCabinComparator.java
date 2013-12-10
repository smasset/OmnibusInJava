package cabin.comparator;

import cabin.util.Cabin;
import cabin.util.FloorRequest;
import cabin.util.RequestType;

public class ClosestCabinComparator extends AbstractCabinComparator {

	public ClosestCabinComparator(FloorRequest request) {
		super(request);
	}

	@Override
	protected Double getScore(Cabin cabin) {
		Double score = this.request.getAbsoluteDistance(cabin.getCurrentFloor()).doubleValue();

		if (!RequestType.OUT.equals(request.getType(cabin.getId()))) {
			score += 100d;
		}

		return score;
	}

}
