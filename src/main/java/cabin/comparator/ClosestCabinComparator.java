package cabin.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import cabin.util.Cabin;
import cabin.util.FloorRequest;

public class ClosestCabinComparator implements Comparator<Cabin> {
	protected FloorRequest request = null;

	public ClosestCabinComparator(FloorRequest request) {
		this.request = request;
	}

	protected Integer getDistance(Cabin cabin) {
		return this.request.getAbsoluteDistance(cabin.getCurrentFloor());
	}

	protected Integer getOutCount(Cabin cabin) {
		return this.request.getOutCount(cabin.getId());
	}

	@Override
	public int compare(Cabin o1, Cabin o2) {
		return new CompareToBuilder()
				.append(this.getDistance(o1), this.getDistance(o2))
				.append(-1 * this.getOutCount(o1), -1 * this.getOutCount(o2))
				.append(o1.getId(), o2.getId()).toComparison();
	}

}
