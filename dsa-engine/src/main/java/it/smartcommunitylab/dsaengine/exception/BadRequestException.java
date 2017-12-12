package it.smartcommunitylab.dsaengine.exception;

public class BadRequestException extends Exception {
	private static final long serialVersionUID = -8800169773297577684L;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message) {
		super(message);
	}
}
