package cabin.util;


public interface Cabin {
	public static final Integer DEFAULT_CABIN_COUNT = 1;
	public static final Integer DEFAULT_CABIN_SIZE = null;
	public static final Integer DEFAULT_START_FLOOR = 0;

	public Command nextCommand();
	public void setNextFloor(Integer nextFloor);

	public Integer getId();
	public Integer getStartFloor();
	public Integer getCurrentFloor();
	public Integer getPopulation();
	public Mode getMode();

	public void getIn();
	public void getOut();
	public void reset(Integer size);
	public void thresholds(Integer alertThreshold, Integer panicThreshold);
	public String getLastDirection();
	public void setLastDirection(String lastDirection);
	public void setOpenAllDoors(boolean openAllDoors);

}
