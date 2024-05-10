import java.util.ArrayList;

public class FCFSAlgorithm extends ScheduleAlgorithmBase {
	public FCFSAlgorithm(int initPosition, int maxCylinders, int direction, ArrayList<Integer> q) {
		super(initPosition, maxCylinders, direction, q);
	}

	public String getName() {
		return "FCFS";
	}

	public void calcSequence() {
		for (Integer next : referenceQueue)
			seekToSector(next);
	}
}
