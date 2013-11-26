package cabin;

import cabin.util.Command;

public interface Cabin {

	public Command nextCommand();
	public void setNextFloor(Integer nextFloor);
	public Integer getCurrentFloor();
	public Integer getPopulation();

}
