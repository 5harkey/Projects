import java.util.Scanner;

public class ArrayClient {

	public static void main(String[] args) {
		System.out.println("AC0350: Array Manager - Jack Sharkey");
		
		Scanner in = new Scanner(System.in);

		int numPages = in.nextInt();
		int frameSize = in.nextInt();
		ArrayManager mgr = new ArrayManager(numPages, frameSize);

		int nextArray = 0;
		int numArrays = in.nextInt();
		Array[] arrays = new Array[numArrays];

		int size;
		int arrayNum;
		int index;
		int value;

		while (in.hasNext()) {
			String action = in.next();
			try {
				switch (action) {
				case "C":
					size = in.nextInt();
					System.out.printf("Creating array-%d as size %d\n", nextArray, size);
					arrays[nextArray] = mgr.createArray(size);
					nextArray++;
					break;
				case "D":
					arrayNum = in.nextInt();
					System.out.printf("Deleting array-%d\n", arrayNum);
					mgr.deleteArray(arrays[arrayNum]);
					arrays[arrayNum] = null;
					break;
				case "S":
					arrayNum = in.nextInt();
					index = in.nextInt();
					value = in.nextInt();
					System.out.printf("Setting array-%d[%d] = %d\n", arrayNum, index, value);
					arrays[arrayNum].setValue(index, value);
					break;
				case "G":
					arrayNum = in.nextInt();
					index = in.nextInt();
					System.out.printf("Array-%d[%d] = %d\n", arrayNum, index, arrays[arrayNum].getValue(index));
					break;
				case "A":
					arrayNum = in.nextInt();
					System.out.printf("Creating array-%d as an alias to array-%d\n", nextArray, arrayNum);
					arrays[nextArray] = mgr.aliasArray(arrays[arrayNum]);
					nextArray++;
					break;
				
				case "W": // COW
					arrayNum = in.nextInt();
					System.out.printf("Creating array-%d as a COW copy to array-%d\n", nextArray, arrayNum);
					arrays[nextArray] = mgr.copyArray(arrays[arrayNum]);
					nextArray++;
					break;
				
				case "R":
					arrayNum = in.nextInt();
					size = in.nextInt();
					System.out.printf("Resizing array-%d to size %d\n", arrayNum, size);
					mgr.resizeArray(arrays[arrayNum], size);
					break;
				case "P":
					printArrays(arrays, nextArray, mgr);
					break;
				case "M":
					mgr.printMemory();
					break;
				case "#":
					String comment = in.nextLine();
					System.out.printf("# %s\n", comment);
					break;
				default:
					System.out.println("Bad Command: " + action);
					System.exit(0);
				}
			} catch (OutOfMemoryException e) {
				System.out.println("*** Out of Memory Exception: " + e.getMessage());
			} catch (SegmentationViolationException e) {
				System.out.println("*** Segmentation Violation: " + e.getMessage());
			} catch (NullPointerException e) {
				System.out.println("*** Null Pointer Exception: Attempted to use a non-existant Array");
			}
		}
		in.close();
	}

	private static void printArrays(Array[] arrays, int numArrays, ArrayManager mgr) {
		for (int i = 0; i < numArrays; i++) {
			Array a = arrays[i];
			if (a == null)
				System.out.printf("Array-%d: <deleted>\n", i);
			else {
				System.out.printf("Array-%d: %s\n", i, a);
			}
		}
	}
}
