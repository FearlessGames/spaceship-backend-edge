package se.fearless.spaceship.edge;

import com.netflix.eureka2.client.resolver.ServerResolver;
import com.netflix.eureka2.client.resolver.ServerResolvers;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;
import se.fearless.service.EurekaServerInfo;
import se.fearless.service.HttpMethod;
import se.fearless.service.MicroService;
import se.fearless.service.Router;

public class Edge {
	public static void main(String[] args) throws InterruptedException {
		Router router = new Router();

		EurekaServerInfo eurekaServerInfo = new EurekaServerInfo(ServerResolvers.from(new ServerResolver.Server("localhost", 2222)),
				ServerResolvers.from(new ServerResolver.Server("localhost", 2223)));
		MicroService microService = new MicroService.Builder(router, "spaceship", "edge").withPort(8888).build();
		microService.start();

		final LoginHandler loginHandler = new LoginHandler(new RemoteLoginService(microService.getServiceLocator("auth")));

		router.addRoute(HttpMethod.GET, "/login", loginHandler);
		router.addRoute(HttpMethod.GET, "/testAuth", new AuthRequestHandler(loginHandler, (userName, sessionKey) -> true));
		router.addRoute(HttpMethod.GET, "/shutdown", (request, response) -> {
			microService.stop();
			return response.close();
		});

		microService.waitTillShutdown();
	}

}
