package se.fearless.spaceship.edge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionMethodInvoker implements MethodInvoker {

	private final Map<String, Object> targets = new ConcurrentHashMap<>();

	@Override
	public Object invoke(String name, String methodName, Object[] args) {
		Method method = null;
		Object target = targets.get(name);
		if (target == null) {
			throw new MethodInvocationException("No target registered with name " + name);
		}
		method = getMethod(methodName, target);
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Method getMethod(String methodName, Object target) {
		Method[] methods = target.getClass().getMethods();
		for (Method currentMethod : methods) {
			if (currentMethod.getName().equals(methodName)) {
				return currentMethod;
			}
		}
		return null;
	}

	@Override
	public void register(String name, Object target) {
		targets.put(name, target);
	}

	@Override
	public Class<?>[] getMethodParameters(String name, String methodName) {
		Object target = targets.get(name);
		Method method = getMethod(methodName, target);
		if (method != null) {
			return method.getParameterTypes();
		}
		return null;
	}
}
