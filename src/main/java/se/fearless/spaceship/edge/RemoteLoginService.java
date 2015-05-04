package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;
import se.fearless.common.json.JsonSerializer;
import se.fearless.service.ServiceLocator;
import se.fearless.spaceship.auth.AuthResultDTO;

import java.nio.charset.Charset;

public class RemoteLoginService {
	private final ServiceLocator authServiceLocator;
	private final JsonSerializer jsonSerializer;

	public RemoteLoginService(ServiceLocator authServiceLocator, JsonSerializer jsonSerializer) {
		this.authServiceLocator = authServiceLocator;
		this.jsonSerializer = jsonSerializer;
	}

	Observable<LoginResult> login(String type, String userName) {
		String server = authServiceLocator.get();

		Observable<HttpClientResponse<ByteBuf>> httpGet = RxNetty.createHttpGet(server + '/' + type + '/' + userName);
		return httpGet.flatMap(byteBufHttpClientResponse -> {
			Observable<ByteBuf> content = byteBufHttpClientResponse.getContent();

			return content.map(byteBuf -> {
				AuthResultDTO authResult = jsonSerializer.fromJson(AuthResultDTO.class, byteBuf.toString(Charset.forName("UTF-8")));
				if (authResult.success) {
					return LoginResult.success(authResult.userName);
				} else {
					return LoginResult.failed();
				}
			});

		});

	}

}
