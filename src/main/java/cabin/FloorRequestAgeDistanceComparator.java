package cabin;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class FloorRequestAgeDistanceComparator implements Comparator<FloorRequest> {
	private Integer currentFloor = null;
	private Long currentTick = null;

	public FloorRequestAgeDistanceComparator(Integer currentFloor, Long currentTick) {
		this.currentFloor = currentFloor;
		this.currentTick = currentTick;
	}

	private Double getScore(FloorRequest request) {
		return Double.valueOf(request.getAge(this.currentTick)
				* request.getAbsoluteDistance(this.currentFloor));
	}

	@Override
	public int compare(FloorRequest o1, FloorRequest o2) {
		return new CompareToBuilder()
				.append(this.getScore(o1), this.getScore(o2))
				.append(o1.getRelativeCount(), o2.getRelativeCount())
				.toComparison();
	}

}
