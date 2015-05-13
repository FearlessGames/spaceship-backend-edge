package se.fearless.spaceship.edge;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import org.slf4j.Logger;
import rx.Observable;

import java.nio.charset.StandardCharsets;

import static org.slf4j.LoggerFactory.getLogger;

public class JsonRpcHandler implements RequestHandler<ByteBuf, ByteBuf> {
	private final Logger logger = getLogger(getClass());
	private final JsonRpcMarshaller jsonRpcMarshaller;

	public JsonRpcHandler(JsonRpcMarshaller jsonRpcMarshaller) {
		this.jsonRpcMarshaller = jsonRpcMarshaller;
	}

	@Override
	public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {

		return request.getContent().flatMap(byteBuf -> {
			String postContent = byteBuf.toString(StandardCharsets.UTF_8);
			Iterable<JsonRpcRequest> jsonRpcRequests = jsonRpcMarshaller.parseContent(postContent);
			Observable<JsonRpcRequest> requestObservable = Observable.from(jsonRpcRequests);
			Observable<JsonRpcResponse> jsonRpcResponseObservable = requestObservable.flatMap(jsonRpcRequest -> new JsonRpcCommand(jsonRpcRequest).observe());
			return jsonRpcResponseObservable.toList().map(jsonRpcMarshaller::formatResponse);

		}).flatMap(response::writeStringAndFlush);

	}


}
