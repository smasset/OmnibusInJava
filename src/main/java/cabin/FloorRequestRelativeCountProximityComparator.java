package cabin;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class FloorRequestRelativeCountProximityComparator implements Comparator<FloorRequest> {
	private Integer currentFloor = null;
	private Long currentTick = null;

	public FloorRequestRelativeCountProximityComparator(Integer currentFloor, Long currentTick) {
		this.currentFloor = currentFloor;
		this.currentTick = currentTick;
	}

	@Override
	public int compare(FloorRequest o1, FloorRequest o2) {
		return new CompareToBuilder()
				.append(o1.getRelativeCount(), o2.getRelativeCount())
				.append(o1.getAbsoluteDistance(this.currentFloor),
						o2.getAbsoluteDistance(this.currentFloor))
				.toComparison();
	}

}
