public interface Array {
	public int getValue(int index) throws SegmentationViolationException;
	public void setValue(int index, int val) throws SegmentationViolationException;
	public int length();
}
