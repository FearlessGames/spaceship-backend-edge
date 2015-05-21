package se.fearless.spaceship.edge;

public interface MethodInvoker {
	Object invoke(String name, String method, Object[] args);

	void register(String name, Object target);

	Class<?>[] getMethodParameters(String name, String method);
}


