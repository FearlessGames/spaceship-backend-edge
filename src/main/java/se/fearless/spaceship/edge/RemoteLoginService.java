package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;
import rx.functions.Func1;
import se.fearless.service.ServiceLocator;

import java.nio.charset.Charset;

public class RemoteLoginService {
	private final ServiceLocator authServiceLocator;

	public RemoteLoginService(ServiceLocator authServiceLocator) {
		this.authServiceLocator = authServiceLocator;
	}

	Observable<LoginResult> login(String userName) {
		String server = authServiceLocator.get();

		Observable<HttpClientResponse<ByteBuf>> httpGet = RxNetty.createHttpGet(server + "/basic/" + userName);
		return httpGet.flatMap(new Func1<HttpClientResponse<ByteBuf>, Observable<LoginResult>>() {
			@Override
			public Observable<LoginResult> call(HttpClientResponse<ByteBuf> byteBufHttpClientResponse) {
				Observable<ByteBuf> content = byteBufHttpClientResponse.getContent();

				return content.map(byteBuf -> {
					if (byteBuf.toString(Charset.defaultCharset()).equals("SUCCESS")) {
						return LoginResult.success();
					} else {
						return LoginResult.failed();
					}

				});

			}
		});

	}

}
