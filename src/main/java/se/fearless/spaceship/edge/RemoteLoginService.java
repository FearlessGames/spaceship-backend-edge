package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import se.fearless.common.json.JsonSerializer;
import se.fearless.service.RemoteServiceCaller;
import se.fearless.spaceship.auth.AuthResultDTO;

import java.nio.charset.StandardCharsets;

public class RemoteLoginService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final RemoteServiceCaller remoteServiceCaller;
	private final JsonSerializer jsonSerializer;

	public RemoteLoginService(RemoteServiceCaller remoteServiceCaller, JsonSerializer jsonSerializer) {
		this.jsonSerializer = jsonSerializer;
		this.remoteServiceCaller = remoteServiceCaller;
	}

	Observable<LoginResult> login(String type, String token) {
		String path = '/' + type + '/' + token;
		return remoteServiceCaller.callService("auth", path, this::parseLoginResult);
	}

	private LoginResult parseLoginResult(ByteBuf byteBuf) {
		AuthResultDTO authResult = jsonSerializer.fromJson(byteBuf.toString(StandardCharsets.UTF_8), AuthResultDTO.class);
		if (authResult.isSuccess()) {
			return LoginResult.success(authResult.getUserName());
		} else {
			return LoginResult.failed();
		}
	}

}
