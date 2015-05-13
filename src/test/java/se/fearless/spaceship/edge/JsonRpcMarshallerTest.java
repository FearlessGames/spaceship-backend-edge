package se.fearless.spaceship.edge;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.json.GsonSerializer;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonRpcMarshallerTest {

	private JsonRpcMarshaller jsonRpcMarshaller;

	@Before
	public void setUp() throws Exception {
		jsonRpcMarshaller = new JsonRpcMarshaller(new GsonSerializer());
	}

	@Test
	public void unmarshallSimpleCall() throws Exception {
		List<JsonRpcRequest> jsonRpcRequests = jsonRpcMarshaller.parseContent("[{\"method\": \"postMessage\", \"params\": [\"Hello all!\"], \"id\": 99}]");
		assertEquals(1, jsonRpcRequests.size());
		JsonRpcRequest jsonRpcRequest = jsonRpcRequests.get(0);
		assertEquals("postMessage", jsonRpcRequest.method);
		assertEquals("Hello all!", jsonRpcRequest.params[0]);
	}
}