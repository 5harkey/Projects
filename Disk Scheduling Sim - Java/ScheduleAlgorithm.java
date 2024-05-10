public interface ScheduleAlgorithm {
	public static final int RIGHT = 0;
	public static final int LEFT = 1;

	public String getName();
	public int getTotalTravel();
	public int getDirection();
	public String getSequence();
	public void calcSequence();
}
