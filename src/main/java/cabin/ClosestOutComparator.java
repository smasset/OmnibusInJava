package cabin;

public class ClosestOutComparator extends AbstractFloorRequestComparator {

	public ClosestOutComparator(Mode mode, Integer currentFloor, Long currentTick) {
		super(mode, currentFloor, currentTick);
	}

	@Override
	protected Double getScore(FloorRequest request) {
		Double score = request.getAbsoluteDistance(this.currentFloor).doubleValue();

		if (!RequestType.OUT.equals(request.getType())) {
			score += 100d;
		}

		return score;
	}

}
