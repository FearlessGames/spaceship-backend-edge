package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

public class LoginHandler implements RequestHandler<ByteBuf, ByteBuf> {

	private final RemoteLoginService remoteLoginService;

	public LoginHandler(RemoteLoginService remoteLoginService) {
		this.remoteLoginService = remoteLoginService;
	}

	@Override
	public Observable<Void> handle(HttpServerRequest<ByteBuf> request, final HttpServerResponse<ByteBuf> response) {
		String userName = getParameter(request, "userName");
		String type = getParameter(request, "type");
		Observable<LoginResult> login = remoteLoginService.login(type, userName);

		if (login.toBlocking().first().isSuccess()) {
			writeSessionKey(response, userName);
		} else {
			response.writeString("Unknown user");
		}
		response.writeString("\n");
		return response.close();
	}

	private String getParameter(HttpServerRequest<ByteBuf> request, String name) {
		return request.getQueryParameters().get(name).get(0);
	}

	private void writeSessionKey(HttpServerResponse<ByteBuf> response, String userName) {
		response.writeString("{sessionKey:'"+ userName + "'}");
	}
}
