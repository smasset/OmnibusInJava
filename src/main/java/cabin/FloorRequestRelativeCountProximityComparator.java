package cabin;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class FloorRequestRelativeCountProximityComparator implements Comparator<FloorRequest> {
	private Integer currentFloor = null;

	public FloorRequestRelativeCountProximityComparator(Integer currentFloor) {
		this.currentFloor = currentFloor;
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
