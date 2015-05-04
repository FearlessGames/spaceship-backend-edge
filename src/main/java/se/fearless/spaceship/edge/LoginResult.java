package se.fearless.spaceship.edge;

public class LoginResult {
	boolean result;
	String userName;

	private LoginResult(boolean result, String userName) {
		this.result = result;
		this.userName = userName;
	}

	private static final LoginResult FAIL = new LoginResult(false, null);

	public boolean isSuccess() {
		return result;
	}

	public static LoginResult success(String userName) {
		return new LoginResult(true, userName);
	}

	public static LoginResult failed() {
		return FAIL;
	}

}
