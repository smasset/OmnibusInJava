package cabin;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class AbstractFloorRequestComparator implements Comparator<FloorRequest> {
	protected Mode mode = null;
	protected Integer currentFloor = null;
	protected Long currentTick = null;

	public AbstractFloorRequestComparator(Mode mode, Integer currentFloor, Long currentTick) {
		this.mode = mode;
		this.currentFloor = currentFloor;
		this.currentTick = currentTick;
	}

	protected abstract Double getScore(FloorRequest request);

	@Override
	public int compare(FloorRequest o1, FloorRequest o2) {
		return new CompareToBuilder().append(this.getScore(o1),
				this.getScore(o2)).toComparison();
	}

}
