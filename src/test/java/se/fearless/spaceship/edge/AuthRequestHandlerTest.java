package se.fearless.spaceship.edge;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import org.junit.Before;
import org.junit.Test;
import se.fearless.rxtestutils.HttpRequestMocks;

import java.util.List;
import java.util.Map;

import static se.mockachino.Mockachino.*;

public class AuthRequestHandlerTest {

	public static final String USERNAME_VALUE = "hiflyer";
	public static final String SESSIONKEY_VALUE = "123456789";
	private RequestHandler<ByteBuf, ByteBuf> delegate;
	private AuthRequestHandler.Authenticator authenticator;
	private AuthRequestHandler authRequestHandler;
	private HttpServerRequest<ByteBuf> request;
	private HttpServerResponse<ByteBuf> response;

	@Before
	public void setUp() throws Exception {
		delegate = HttpRequestMocks.requestHandler();
		authenticator = mock(AuthRequestHandler.Authenticator.class);
		authRequestHandler = new AuthRequestHandler(delegate, authenticator);

		request = HttpRequestMocks.request();
		when(request.getHttpMethod()).thenReturn(HttpMethod.GET);
		when(request.getPath()).thenReturn("/protectedPath");
		Map<String, List<String>> parameters = ImmutableMap.of(
				AuthRequestHandler.USER_NAME_PARAMETER_NAME, Lists.newArrayList(USERNAME_VALUE),
				AuthRequestHandler.SESSION_KEY_PARAMETER_NAME, Lists.newArrayList(SESSIONKEY_VALUE));
		when(request.getQueryParameters()).thenReturn(parameters);

		response = HttpRequestMocks.response();
	}

	@Test
	public void authenticatedRequestGoesThroughToHandler() throws Exception {
		when(authenticator.allowsAccess(USERNAME_VALUE, SESSIONKEY_VALUE)).thenReturn(true);

		authRequestHandler.handle(request, response);

		verifyOnce().on(delegate).handle(request, response);
	}

	@Test
	public void accessForbiddenWithoutAuthentication() throws Exception {
		when(authenticator.allowsAccess(USERNAME_VALUE, SESSIONKEY_VALUE)).thenReturn(false);

		authRequestHandler.handle(request, response);

		verifyNever().on(delegate).handle(request, response);
		verifyOnce().on(response).setStatus(HttpResponseStatus.FORBIDDEN);
	}
}