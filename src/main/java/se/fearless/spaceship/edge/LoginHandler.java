package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginHandler {

	private final List<String> users = Arrays.asList("hiflyer", "demazia");

	public Observable<Void> handle(HttpServerRequest<ByteBuf> request, final HttpServerResponse<ByteBuf> response) {
		String userName = request.getQueryParameters().get("userName").get(0);
		if (users.contains(userName)) {
			response.writeString("Correct user\n");
		} else {
			response.writeString("Unknown user\n");
		}
		return response.close();
	}
}
