package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;
import se.fearless.service.MicroService;

public class Edge {
	public static void main(String[] args) {
		final LoginHandler loginHandler = new LoginHandler();
		MicroService microService = new MicroService(8888, (request, response) -> {
			String path = request.getPath();
			if (path.equals("/login")) {
				return loginHandler.
						handle(request, response);
			} else {
				response.setStatus(HttpResponseStatus.FORBIDDEN);
				return response.close();
			}
		});
		microService.start();
	}
}
