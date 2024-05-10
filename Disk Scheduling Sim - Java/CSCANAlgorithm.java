import java.util.ArrayList;
import java.util.Collections;

public class CSCANAlgorithm extends ScheduleAlgorithmBase {
	public CSCANAlgorithm(int initPosition, int maxCylinders, int direction, ArrayList<Integer> q) {
		super(initPosition, maxCylinders, direction, q);
	}

	public String getName() {
		return "CSCAN";
	}

	public void calcSequence() {
		ArrayList<Integer> rightRequests = new ArrayList<>();
        ArrayList<Integer> leftRequests = new ArrayList<>();
       
        for (Integer i: referenceQueue) {
        	if (i > position) rightRequests.add(i);
        	else leftRequests.add(i);
        }
        Collections.sort(rightRequests);
        Collections.sort(leftRequests);
        if (direction == RIGHT) {
        	for (Integer i: rightRequests) {
        		seekToSector(i);
        	}
        	seekToSector(maxCylinders - 1);
        	seekToSector(0);
        	for (Integer i: leftRequests) {
        		seekToSector(i);
        	}
        }
        else {
        	Collections.reverse(leftRequests);
        	Collections.reverse(rightRequests);
        	for (Integer i: leftRequests) {
        		seekToSector(i);
        	}
        	seekToSector(0);
        	seekToSector(maxCylinders - 1);
        	for (Integer i: rightRequests) {
        		seekToSector(i);
        	}
        }
	}
}
