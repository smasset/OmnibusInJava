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
		CompareToBuilder builder = new CompareToBuilder();

		builder.appendSuper(o1.compareTo(o2));

		double d1 = Math.abs(this.currentFloor - o1.getFloor());
		double d2 = Math.abs(this.currentFloor - o2.getFloor());
		builder.append(d1, d2);

		return builder.toComparison();
	}

}
