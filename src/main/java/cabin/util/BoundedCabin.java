package cabin.util;

public interface BoundedCabin extends Cabin {

	public Integer getUpperLimit();
	public void setUpperLimit(Integer upperLimit);

	public Integer getLowerLimit();
	public void setLowerLimit(Integer lowerLimit);

	public void reset(Integer size, Integer lowerLimit, Integer upperLimit);
	public boolean isWithinLimits(Integer floor);
}
