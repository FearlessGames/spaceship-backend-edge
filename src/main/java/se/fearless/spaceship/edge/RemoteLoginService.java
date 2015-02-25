package se.fearless.spaceship.edge;

import rx.Observable;

import java.util.Arrays;
import java.util.List;

public class RemoteLoginService {

	private final List<String> users = Arrays.asList("hiflyer", "demazia");

	Observable<LoginResult> login(String userName) {
		LoginResult loginResult = getLoginResult(userName);
		return Observable.just(loginResult);
	}

	private LoginResult getLoginResult(String userName) {
		if (users.contains(userName)) {
			return LoginResult.success();
		} else {
			return LoginResult.failed();
		}
	}
}
