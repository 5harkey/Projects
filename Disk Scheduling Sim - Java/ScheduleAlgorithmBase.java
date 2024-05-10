import java.util.ArrayList;

public abstract class ScheduleAlgorithmBase implements ScheduleAlgorithm {
	protected int position;
	protected int distance;
	protected int maxCylinders;
	protected int direction;
	protected ArrayList<Integer> referenceQueue;
	protected String sequence;

	public ScheduleAlgorithmBase(int initPosition, int maxCylinders, int direction, ArrayList<Integer> q) {
		position = initPosition;
		this.maxCylinders = maxCylinders;
		this.direction = direction;
		referenceQueue = new ArrayList<Integer>(q);
		distance = 0;
		sequence = initPosition + " ";
		calcSequence();
	}

	public String getName() {
		return "UNKWN";
	}

	public void calcSequence() {
	}

	public int getTotalTravel() {
		return distance;
	}

	public int getDirection() {
		return direction;
	}
	
	public String getSequence() {
		return sequence;
	}

	protected void seekToSector(int p) {
		distance += Math.abs(position - p);
		position = p;
		sequence = sequence + p + " ";
	}
}
