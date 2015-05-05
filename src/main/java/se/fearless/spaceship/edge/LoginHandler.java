package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;
import se.fearless.common.json.JsonSerializer;
import se.fearless.common.security.FearCrypto;

import java.nio.charset.Charset;
import java.util.Base64;

public class LoginHandler implements RequestHandler<ByteBuf, ByteBuf> {

	private final RemoteLoginService remoteLoginService;
	private final FearCrypto fearCrypto;
	private final JsonSerializer jsonSerializer;
	private final byte[] key;

	public LoginHandler(RemoteLoginService remoteLoginService, FearCrypto fearCrypto, JsonSerializer jsonSerializer, String edgePassword) {
		this.remoteLoginService = remoteLoginService;
		this.fearCrypto = fearCrypto;
		this.jsonSerializer = jsonSerializer;

		key = fearCrypto.generateKey(edgePassword);
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
		byte[] encryptedData = fearCrypto.encrypt(key, userName.getBytes(Charset.forName("UTF-8")));
		ClientLoginResult loginResult = new ClientLoginResult(Base64.getEncoder().encodeToString(encryptedData));
		response.writeString(jsonSerializer.toJson(loginResult));
	}
}

class ClientLoginResult {
	private String sessionKey;

	public ClientLoginResult(String sessionKey) {
		this.sessionKey = sessionKey;
	}
}
