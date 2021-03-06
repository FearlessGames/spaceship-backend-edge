package se.fearless.spaceship.edge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionMethodInvoker implements MethodInvoker {

	public static final String NO_TARGET_REGISTERED_WITH_NAME = "No target registered with name \"%s\"";
	public static final String METHOD_NOT_FOUND_ON_TARGET = "Method \"%s\" not found on target \"%s\"";
	public static final String METHOD_IS_NOT_ACCESSIBLE = "Method \"%s\" is not accessible";
	public static final String ARGUMENTS_DON_T_MATCH_EXPECTED = "Arguments don't match. Expected (%s) but were (%s)";
	private final Map<String, Object> targets = new ConcurrentHashMap<>();

	@Override
	public Object invoke(String name, String methodName, Object[] args) {
		Method method = null;
		Object target = targets.get(name);
		if (target == null) {
			throw new MethodInvocationException(String.format(NO_TARGET_REGISTERED_WITH_NAME, name));
		}
		method = getMethod(methodName, target, name);
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException e) {
			throw new MethodInvocationException(String.format(METHOD_IS_NOT_ACCESSIBLE, methodName));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			String actualArgs = Arrays.stream(args).map(Object::getClass).map(Class::getName).reduce((s, s2) -> s + ", " + s2).orElse("");
			Class<?>[] expectedParamTypes = method.getParameterTypes();
			String expectedArgs = Arrays.stream(expectedParamTypes).map(aClass -> aClass.getName()).reduce((s, s2) -> s + ", " + s2).orElse("");

			throw new MethodInvocationException(String.format(ARGUMENTS_DON_T_MATCH_EXPECTED, expectedArgs, actualArgs));
		}
		return null;
	}

	private Method getMethod(String methodName, Object target, String targetName) {
		Method[] methods = target.getClass().getDeclaredMethods();
		for (Method currentMethod : methods) {
			if (currentMethod.getName().equals(methodName)) {
				return currentMethod;
			}
		}
		throw new MethodInvocationException(String.format(METHOD_NOT_FOUND_ON_TARGET, methodName, targetName));
	}

	@Override
	public void register(String name, Object target) {
		targets.put(name, target);
	}

	@Override
	public Class<?>[] getMethodParameters(String name, String methodName) {
		Object target = targets.get(name);
		Method method = getMethod(methodName, target, name);
		if (method != null) {
			return method.getParameterTypes();
		}
		return null;
	}
}
