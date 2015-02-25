package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;
import rx.functions.Func1;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class RemoteLoginService {

	private static final String LOGIN_SERVER = "http://localhost:9999";

	Observable<LoginResult> login(String userName) {
		Observable<HttpClientResponse<ByteBuf>> httpGet = RxNetty.createHttpGet(LOGIN_SERVER + "/" + userName);
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
