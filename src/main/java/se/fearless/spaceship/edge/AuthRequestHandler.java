package se.fearless.spaceship.edge;

import com.google.common.collect.Iterables;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

import java.util.List;
import java.util.Map;

class AuthRequestHandler implements RequestHandler<ByteBuf, ByteBuf> {
	private final RequestHandler<ByteBuf, ByteBuf> delegate;
	private final Authenticator authenticator;

	AuthRequestHandler(RequestHandler<ByteBuf, ByteBuf> delegate, Authenticator authenticator) {
		this.delegate = delegate;
		this.authenticator = authenticator;
	}

	@Override
	public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
		Map<String, List<String>> queryParameters = request.getQueryParameters();
		String userName = getParameter(queryParameters, "userName");
		String sessionKey = getParameter(queryParameters, "sessionKey");

		if (authenticator.allowsAccess(userName, sessionKey)) {
			return delegate.handle(request, response);
		} else {
			response.setStatus(HttpResponseStatus.FORBIDDEN);
			return response.close();
		}
	}

	private String getParameter(Map<String, List<String>> queryParameters, String parameterName) {
		return Iterables.getOnlyElement(queryParameters.get(parameterName));
	}

	public interface Authenticator {
		boolean allowsAccess(String userName, String sessionKey);
	}
}
