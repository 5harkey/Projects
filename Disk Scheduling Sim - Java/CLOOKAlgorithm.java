import java.util.ArrayList;
import java.util.Collections;

public class CLOOKAlgorithm extends ScheduleAlgorithmBase {
    public CLOOKAlgorithm(int initPosition, int maxCylinders, int direction, ArrayList<Integer> q) {
        super(initPosition, maxCylinders, direction, q);
    }

    public String getName() {
        return "CLOOK";
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
        	for (Integer i: leftRequests) {
        		seekToSector(i);
        	}
        }
        else {
        	Collections.reverse(rightRequests);
        	Collections.reverse(leftRequests);
        	for (Integer i: leftRequests) {
        		seekToSector(i);
        	}
        	for (Integer i: rightRequests) {
        		seekToSector(i);
        	}
        }
    }
}
