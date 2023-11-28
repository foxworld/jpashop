package hello.jpashop.exception;

public class NotEnoughtStockException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4871353516080320428L;

	public NotEnoughtStockException(String message) {
		super(message);
	}
	
	public NotEnoughtStockException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public NotEnoughtStockException(Throwable cause) {
		super(cause);
	}

	protected NotEnoughtStockException(String message, Throwable cause, boolean enableSupperession, boolean wriablestackTrace) {
		super(message, cause, enableSupperession, wriablestackTrace);
	}
}
