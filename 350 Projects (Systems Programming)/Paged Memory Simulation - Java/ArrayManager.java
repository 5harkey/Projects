import java.util.ArrayList;
import java.util.Arrays;

public class ArrayManager {
	private int numFrames;
	private int frameSize;
	private int[] data;
	private ArrayList<Integer> freeFrameList;
	private int[] referenceCounts;

	public ArrayManager(int numFrames, int frameSize) {
		this.numFrames = numFrames;
		this.frameSize = frameSize;
		data = new int[numFrames * frameSize];
		
		freeFrameList = new ArrayList<Integer>(numFrames);
		referenceCounts = new int[numFrames];
		
		for (int i = 0; i < numFrames; ++i) {
			freeFrameList.add(i);
			referenceCounts[i] = 0;
		}
	}

	public Array createArray(int size) throws OutOfMemoryException {
		//check to see if enough space available; size/frameSize  -- round up
		int numFramesNeeded = (int) Math.ceil((double) size/ frameSize);
		if (numFramesNeeded > freeFrameList.size()) {
			throw new OutOfMemoryException(String.format("Cannot create array of size %d with "
					+ "%d frames of %d integers available", size, freeFrameList.size(), frameSize));
		}


		int[] pt = new int[numFramesNeeded];	//otherwise, create new page table
		for (int i = 0; i < numFramesNeeded; ++i) {	
				pt[i] = freeFrameList.remove(0);//add free frames from freeFrameList to pageTable
				++referenceCounts[pt[i]];	//Increment Reference Counts for allocated frames
		}
		PagedArray pa = new PagedArray(pt, size);//create paged array with size and pageTable
		
		return pa;
	}

	public Array aliasArray(Array a) {
		PagedArray pa = (PagedArray) a;
		return new PagedArray(pa.getPageTable(), pa.length());
	}

	public void deleteArray(Array a) {
		PagedArray pa = (PagedArray) a;
		int[] pt = pa.getPageTable();
		for (int i = 0; i < pt.length; ++i) {
			freeFrameList.add(pt[i]);
			--referenceCounts[pt[i]];	//Decrement Reference Counts for corresponding deleted frames
		}
		pa.setLength(0);
	}
	
	public Array copyArray(Array a) {
		PagedArray pa = (PagedArray) a;
		int[] pt = pa.getPageTable();
		for (int i = 0; i < pt.length; ++i) {
			++referenceCounts[pt[i]];		//Increment reference counts for frames
		}
		return new PagedArray(Arrays.copyOf(pt, pt.length), pa.length());
	}

	public void resizeArray(Array a, int newSize) throws OutOfMemoryException {		
		PagedArray pa = (PagedArray) a;
		int oldNumPages = pa.getPageTable().length;
		int newNumPages = (int) Math.ceil((double)newSize / frameSize);
		int[] pt = new int[newNumPages];
		
		if (newNumPages < oldNumPages) {
			for (int i = 0; i < newNumPages; ++i) {
				pt[i] = pa.getPageTable()[i];
			}
			for (int i = newNumPages; i < oldNumPages; ++i) {
				freeFrameList.add(pa.getPageTable()[i]);
				--referenceCounts[pa.getPageTable()[i]];	//Decrement Reference Counts for freed frames
			}
		}
		if (newNumPages > (freeFrameList.size() - oldNumPages)) {
			throw new OutOfMemoryException(String.format("Cannot resize array of size %d with "
					+ "%d frames of %d integers available", newSize, freeFrameList.size(), frameSize));
		}
		else if (newNumPages > oldNumPages){
			//Create new pageTable for pa
			//Copy necessary pages to new pt
			for (int i = 0; i < oldNumPages; ++i) {
				pt[i] = pa.getPageTable()[i];
			}
			for (int i = oldNumPages; i < newNumPages; ++i) {
				pt[i] = freeFrameList.remove(0);
				++referenceCounts[pt[i]];	//Increment Reference Counts for newly allocated frames
			}
		}
		pa.setLength(newSize);
		pa.setPageTable(pt);
		//setPageTable and Length of pa to be the new pt and newSize
	}

	public void printMemory() {
		// Calculate how full the memory is
		double percentFilled = 100 * (((double) numFrames - (double) freeFrameList.size()) / (double) numFrames);
		// Print dash or x for available frames or not available frames respectively
		System.out.printf("Memory [%dx%d] %.2f%% full\n", numFrames, frameSize, percentFilled);
		for (int i = 0; i < numFrames; ++i) {
			System.out.print(referenceCounts[i]);
		}
		System.out.println();
	}

	private class PagedArray implements Array {
		private int[] pageTable;
		private int length;

		public PagedArray(int pageTable[], int length) {
			this.pageTable = pageTable;
			this.length = length;
		}

		public int getValue(int index) throws SegmentationViolationException {
			if (index >= length || index < 0) {
				throw new SegmentationViolationException(String.format("Index %d is out of range. Expected 0->%d", index, length - 1));
			}
			int pageNum =(int) Math.floor((double) index / frameSize);
			int offSet = index % frameSize;
			int i = pageTable[pageNum] * frameSize + offSet;
			return data[i];
		}

		public void setValue(int index, int val) throws SegmentationViolationException {
		    if (index >= length || index < 0) {
		        throw new SegmentationViolationException(String.format("Index %d is out of range. Expected 0->%d", index, length - 1));
		    }

		    int pageNum = (int) Math.floor((double) index / frameSize);
		    int offSet = index % frameSize;
		    int frameIndex = pageTable[pageNum];

		    if (referenceCounts[frameIndex] == 1) {
		        // If reference count is 1, update data[dataIndex]
		        int dataIndex = frameIndex * frameSize + offSet;
		        data[dataIndex] = val;
		    } else {
		        // If reference count is greater than 1, allocate a new frame
		        if (freeFrameList.isEmpty()) {
		            // Handle out of memory scenario
		            throw new OutOfMemoryException("Out of memory. Cannot allocate a new frame.");
		        }

		        // Update reference counts for old and new frames
		        --referenceCounts[frameIndex];
		        int newFrameIndex = freeFrameList.remove(0);
		        ++referenceCounts[newFrameIndex];

		        // Update page table
		        pageTable[pageNum] = newFrameIndex;

		        // Copy data to the new frame
		        int newDataIndex = newFrameIndex * frameSize + offSet;
		        data[newDataIndex] = val;
		    }
		}


		public String toString() {
			String s = String.format("Length %d @", length);
			for (int i=0; i<pageTable.length; ++i) {
				s += pageTable[i] + " ";
			}
			return s;
		}

		public int[] getPageTable() {
			
			return pageTable;
		}
		
		public void setPageTable(int[] pageTable) {
			this.pageTable = pageTable;
		}
		
		public int length() {
			
			return length;
		}
		
		public void setLength(int length) {
			this.length = length;
		}
	}
}
