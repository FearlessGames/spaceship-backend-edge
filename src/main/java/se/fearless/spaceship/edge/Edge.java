package se.fearless.spaceship.edge;

import com.netflix.eureka2.client.resolver.ServerResolver;
import com.netflix.eureka2.client.resolver.ServerResolvers;
import se.fearless.service.*;

public class Edge {
	public static void main(String[] args) throws InterruptedException {
		Router router = new Router();

		EurekaServerInfo eurekaServerInfo = new EurekaServerInfo(ServerResolvers.from(new ServerResolver.Server("localhost", 2222)),
				ServerResolvers.from(new ServerResolver.Server("localhost", 2223)));
		MicroService microService = new MicroService(8889, router, "spaceship", "edge", eurekaServerInfo, new HostnameProvider());
		microService.start();

		final LoginHandler loginHandler = new LoginHandler(new RemoteLoginService(microService.getServiceLocator("auth")));

		router.addRoute(HttpMethod.GET, "/login", loginHandler);
		router.addRoute(HttpMethod.GET, "/testAuth", new AuthRequestHandler(loginHandler, (userName, sessionKey) -> true));

		microService.waitTillShutdown();
	}

}
