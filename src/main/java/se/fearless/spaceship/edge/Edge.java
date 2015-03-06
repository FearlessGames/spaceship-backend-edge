package se.fearless.spaceship.edge;

import se.fearless.service.HttpMethod;
import se.fearless.service.MicroService;
import se.fearless.service.Router;

public class Edge {
	public static void main(String[] args) {
		final LoginHandler loginHandler = new LoginHandler(new RemoteLoginService());
		Router router = new Router();
		router.addRoute(HttpMethod.GET, "/login", loginHandler);
		router.addRoute(HttpMethod.GET, "/testAuth", new AuthRequestHandler(loginHandler, (userName, sessionKey) -> true));
		MicroService microService = new MicroService(8888, router);
		microService.start();
	}

}
