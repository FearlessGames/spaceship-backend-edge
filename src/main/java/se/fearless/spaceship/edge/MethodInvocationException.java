package se.fearless.spaceship.edge;

public class MethodInvocationException extends RuntimeException {
	public MethodInvocationException(String message) {
		super(message);
	}

	public MethodInvocationException(String message, Throwable cause) {
		super(message, cause);
	}
}
