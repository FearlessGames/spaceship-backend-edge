package se.fearless.spaceship.edge;

import io.netty.handler.codec.http.HttpResponseStatus;
import se.fearless.service.MicroService;

public class Edge {
	public static void main(String[] args) {
		final LoginHandler loginHandler = new LoginHandler(new RemoteLoginService());
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
