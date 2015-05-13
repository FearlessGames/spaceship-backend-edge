package se.fearless.spaceship.edge;

import rx.Observable;
import rx.schedulers.Schedulers;
import se.fearless.common.json.rpc.JsonRpcRequest;
import se.fearless.common.json.rpc.JsonRpcResponse;

import java.util.Date;

public class JsonRpcCommand {
	private final JsonRpcRequest jsonRpcRequest;

	public JsonRpcCommand(JsonRpcRequest jsonRpcRequest) {
		this.jsonRpcRequest = jsonRpcRequest;
	}

	public Observable<JsonRpcResponse> observe() {
		Observable<JsonRpcResponse> o = Observable.create(observer -> {
			observer.onNext(invoke());
			observer.onCompleted();
		});

		return o.subscribeOn(Schedulers.computation());


	}

	private JsonRpcResponse invoke() {

		try {

			System.out.println("Emulating work for jsonRpcId " + jsonRpcRequest.id + " on thread " + Thread.currentThread() + "Start Time: " + new Date());
			Thread.sleep(2000);
			System.out.println("Done with work for jsonRpcId " + jsonRpcRequest.id + " on thread " + Thread.currentThread() + "Start Time: " + new Date());
		} catch (InterruptedException e) {

		}

		JsonRpcResponse jsonRpcResponse = new JsonRpcResponse();
		jsonRpcResponse.id = jsonRpcRequest.id;
		jsonRpcResponse.result = "Done";
		return jsonRpcResponse;
	}
}