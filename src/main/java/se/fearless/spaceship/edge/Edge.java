package se.fearless.spaceship.edge;

import se.fearless.common.json.GsonSerializer;
import se.fearless.service.HttpMethod;
import se.fearless.service.MicroService;
import se.fearless.service.Router;

public class Edge {
	public static void main(String[] args) throws InterruptedException {
		Router router = new Router();

		MicroService microService = new MicroService.Builder(router, "spaceship", "edge").withPort(8888).build();
		microService.start();

		final LoginHandler loginHandler = new LoginHandler(new RemoteLoginService(microService.getServiceLocator("auth"), new GsonSerializer()), fearCrypto, jsonSerializer, edgePassword);

		router.addRoute(HttpMethod.GET, "/login", loginHandler);
		router.addRoute(HttpMethod.GET, "/testAuth", new AuthRequestHandler(loginHandler, (userName, sessionKey) -> true));
		router.addRoute(HttpMethod.GET, "/shutdown", (request, response) -> {
			microService.stop();
			return response.close();
		});

		microService.waitTillShutdown();
	}

}
