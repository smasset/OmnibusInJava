package cabin.comparator;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import cabin.util.Cabin;
import cabin.util.FloorRequest;

public abstract class AbstractCabinComparator implements Comparator<Cabin> {
	protected FloorRequest request = null;

	public AbstractCabinComparator(FloorRequest request) {
		this.request = request;
	}

	protected abstract Double getScore(Cabin cabin);

	@Override
	public int compare(Cabin o1, Cabin o2) {
		return new CompareToBuilder().append(this.getScore(o1),
				this.getScore(o2)).toComparison();
	}

}
