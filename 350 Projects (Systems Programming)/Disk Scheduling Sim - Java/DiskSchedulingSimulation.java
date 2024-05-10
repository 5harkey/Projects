import java.util.Scanner;
import java.util.ArrayList;

public class DiskSchedulingSimulation {
	private final static int NUM_ALGORITHMS = 10;
	private static int maxCylinders = 0;
	private static int initPos = 0;
	private static ArrayList<Integer> ref = null;

	public static void main(String[] args) {	
		Scanner in = new Scanner(System.in);
		System.out.print("Textbook example or Random (T or R)? ");
		char choice = in.nextLine().toUpperCase().charAt(0);
		
		if (choice == 'T') {
			maxCylinders = 200;
			initPos = 53;
			int[] array = { 98, 183, 37, 122, 14, 124, 65, 67 };
			ref = new ArrayList<>();
			for (int i = 0; i < array.length; i++)
				ref.add(array[i]);
		} else {
			System.out.print("Size of Disk (cylinders)? ");
			maxCylinders = in.nextInt();
			initPos = (int) (Math.random() * maxCylinders);
			System.out.print("Reference string length? ");
			int numReferences = in.nextInt();
			ref = makeReferenceString(numReferences, maxCylinders);
		}
		in.close();
		
		reportStats();

		ScheduleAlgorithm[] algos = new ScheduleAlgorithm[NUM_ALGORITHMS];
		algos[0] = new FCFSAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.RIGHT, ref);
		algos[1] = new SSTFAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.RIGHT, ref);
		algos[2] = new SCANAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.RIGHT, ref);
		algos[3] = new SCANAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.LEFT, ref);
		algos[4] = new CSCANAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.RIGHT, ref);
		algos[5] = new CSCANAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.LEFT, ref);
		algos[6] = new LOOKAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.RIGHT, ref);
		algos[7] = new LOOKAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.LEFT, ref);
		algos[8] = new CLOOKAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.RIGHT, ref);
		algos[9] = new CLOOKAlgorithm(initPos, maxCylinders, ScheduleAlgorithm.LEFT, ref);

		System.out.printf("%-6s(D) %7s   %s\n", "Algo", "Travel", "Sequence");
		System.out.printf("=========================================================\n");

		for (int a = 0; a < NUM_ALGORITHMS; a++) {
			ScheduleAlgorithm algo = algos[a];
			System.out.printf("%-6s(%c) %7d   %s\n", algo.getName(), algo.getDirection() == ScheduleAlgorithm.RIGHT ? 'R' : 'L', algo.getTotalTravel(), algo.getSequence());
		}
	}

	private static ArrayList<Integer> makeReferenceString(int n, int max) {
		ArrayList<Integer> a = new ArrayList<>();

		for (int i = 0; i < n; i++)
			a.add((int) (Math.random() * max));

		return a;
	}
	
	private static void reportStats() {
		System.out.printf("\n=========================================================\n");
		System.out.printf("ACO350 - Jack Sharkey\n");
		System.out.printf("Disk Scheduling Simulation\n");
		System.out.printf("=========================================================\n");
		System.out.printf("   # Algorithms tested  : %d\n", NUM_ALGORITHMS);
		System.out.printf("   # of Cylinders       : %d\n", maxCylinders);
		System.out.printf("   Initial Head Position: %d\n", initPos);
		System.out.printf("   Disk Access Queue    : ");
		for (Integer i : ref)
			System.out.printf("%d ", i);
		System.out.printf("\n=========================================================\n");
	}
}
