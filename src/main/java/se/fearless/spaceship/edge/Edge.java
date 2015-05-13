package se.fearless.spaceship.edge;

import se.fearless.common.json.GsonSerializer;
import se.fearless.common.security.AesCrypto;
import se.fearless.common.security.FearCrypto;
import se.fearless.service.HttpMethod;
import se.fearless.service.MicroService;
import se.fearless.service.Router;

public class Edge {

	public static final String EDGE_PASSWORD = "$goiTt0OHZyNjW1J";

	public static void main(String[] args) throws InterruptedException {
		Router router = new Router();

		MicroService microService = new MicroService.Builder(router, "spaceship", "edge").withPort(8888).build();
		microService.start();

		GsonSerializer jsonSerializer = new GsonSerializer();
		FearCrypto fearCrypto = new AesCrypto();
		final LoginHandler loginHandler = new LoginHandler(new RemoteLoginService(microService.getServiceLocator("auth"), jsonSerializer), fearCrypto, jsonSerializer, EDGE_PASSWORD);

		router.addRoute(HttpMethod.GET, "/login", loginHandler);
		router.addRoute(HttpMethod.GET, "/shutdown", (request, response) -> {
			microService.stop();
			return response.close();
		});

		microService.waitTillShutdown();
	}

}
