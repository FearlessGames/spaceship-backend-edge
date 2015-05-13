package se.fearless.spaceship.edge;

public class JsonRpcRequest {
	public static final String JSON_RPC = "2.0";

	public String jsonrpc;
	public Long id;
	public String method;
	public String[] params = new String[0];
	public String originJson;
}