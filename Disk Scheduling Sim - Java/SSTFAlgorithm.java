import java.util.ArrayList;

public class SSTFAlgorithm extends ScheduleAlgorithmBase {
	public SSTFAlgorithm(int initPosition, int maxCylinders, int direction, ArrayList<Integer> q) {
		super(initPosition, maxCylinders, direction, q);
	}

	public String getName() {
		return "SSTF";
	}

	public void calcSequence() {
		while (!referenceQueue.isEmpty()) {
			int next = findClosest();
			seekToSector(next);
			referenceQueue.remove(Integer.valueOf(next));
		}
    }
	private int findClosest() {
        int closestDistance = Integer.MAX_VALUE;
        int closestRequest = -1;

        for (Integer request : referenceQueue) {
            int distance = Math.abs(position - request);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestRequest = request;
            }
        }
        return closestRequest;
	}
}
