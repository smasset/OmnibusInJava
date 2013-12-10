package cabin.comparator;

import cabin.util.Cabin;
import cabin.util.FloorRequest;

public class ClosestCabinComparator extends AbstractCabinComparator {

	public ClosestCabinComparator(FloorRequest request) {
		super(request);
	}

	@Override
	protected Double getScore(Cabin cabin) {
		Double score = this.request.getAbsoluteDistance(cabin.getCurrentFloor()).doubleValue();
		return score;
	}

}
