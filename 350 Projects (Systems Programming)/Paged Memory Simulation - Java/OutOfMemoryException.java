public class OutOfMemoryException extends RuntimeException {
	String message;
	public OutOfMemoryException(String message) {
		this.message = message;
	}
}
