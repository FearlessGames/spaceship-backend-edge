package se.fearless.spaceship.edge;

import se.fearless.common.json.JsonSerializer;

import java.util.Arrays;
import java.util.List;

public class JsonRpcMarshaller {

	private final JsonSerializer jsonSerializer;

	public JsonRpcMarshaller(JsonSerializer jsonSerializer) {
		this.jsonSerializer = jsonSerializer;
	}


	public String formatResponse(List<JsonRpcResponse> jsonRpcResponses) {
		StringBuilder result = new StringBuilder("[");

		boolean notFirstRequest = false;
		for (JsonRpcResponse jsonRpcResponse : jsonRpcResponses) {
			String individualResult = jsonSerializer.toJson(jsonRpcResponse);
			if (individualResult != null && individualResult.length() > 0) {
				if (notFirstRequest) {
					result.append(", ");
				}
				result.append(individualResult);
				notFirstRequest = true;
			}
		}
		result.append("]");
		return result.toString();


	}

	public List<JsonRpcRequest> parseContent(String content) {
		System.out.println("Parsing content on " + Thread.currentThread());
		JsonRpcRequest[] jsonRpcRequests = jsonSerializer.fromJson(content, JsonRpcRequest[].class);
		return Arrays.asList(jsonRpcRequests);
	}
}
