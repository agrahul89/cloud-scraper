package pvt.webscraper.exceptions;

public class AppException extends Exception {

	private static final long serialVersionUID = 5037135794588366140L;

	public AppException() {
		super();
	}

	public AppException(String inMessage) {
		super(inMessage);
	}

	public AppException(String inMessage, Throwable inCause) {
		super(inMessage, inCause);
	}

	public AppException(Throwable inCause) {
		super(inCause);
	}
}
