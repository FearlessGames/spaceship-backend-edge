package se.fearless.spaceship.edge;

public abstract class LoginResult {

	private static final LoginResult SUCCESS = new LoginResult() {
		@Override
		public boolean isSuccess() {
			return true;
		}
	};
	private static final LoginResult FAIL = new LoginResult() {
		@Override
		public boolean isSuccess() {
			return false;
		}
	};

	public boolean isSuccess() {
		return true;
	}

	public static LoginResult success() {
		return SUCCESS;
	}

	public static LoginResult failed() {
		return FAIL;
	}

}
