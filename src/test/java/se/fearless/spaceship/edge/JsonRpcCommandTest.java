package se.fearless.spaceship.edge;

import org.junit.Test;
import se.fearless.common.json.rpc.JsonRpcRequest;

public class JsonRpcCommandTest {

	@Test
	public void invokeCommand() throws Exception {
		JsonRpcRequest request = new JsonRpcRequest();
		request.id = 1L;
		request.method = "runThis";

		JsonRpcCommand command = new JsonRpcCommand(request);


	}
}