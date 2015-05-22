package se.fearless.spaceship.edge;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ReflectionMethodInvokerTest {
	private ReflectionMethodInvoker reflectionMethodInvoker;

	@Before
	public void setUp() throws Exception {
		reflectionMethodInvoker = new ReflectionMethodInvoker();
		reflectionMethodInvoker.register("SimpleClass", new SimpleClass());
	}

	@Test
	public void getReturnParameterForMethodInSimpleClassWithOneParameter() throws Exception {

		Class<?>[] methodParameters = reflectionMethodInvoker.getMethodParameters("SimpleClass", "methodOne");
		verifyThatTheParametersAre(methodParameters, String.class);
	}

	@Test
	public void getReturnParameterForMethodInSimpleClassWithTwoParameters() throws Exception {

		Class<?>[] methodParameters = reflectionMethodInvoker.getMethodParameters("SimpleClass", "methodTwo");
		verifyThatTheParametersAre(methodParameters, String.class, int.class);
	}

	@Test
	public void getReturnParametersForClassWithDto() throws Exception {
		Class<?>[] methodParameters = reflectionMethodInvoker.getMethodParameters("SimpleClass", "methodThree");
		verifyThatTheParametersAre(methodParameters, SimpleDto[].class);
	}

	@Test
	public void invokeSimpleMethodOne() throws Exception {
		String stringValue = "foo";
		Object returnValue = reflectionMethodInvoker.invoke("SimpleClass", "methodOne", new Object[]{stringValue});

		verifyThatReturnValueMatches(stringValue, returnValue);
	}

	@Test
	public void invokeMethodWithArrayArguments() throws Exception {
		SimpleDto firstDto = new SimpleDto();
		SimpleDto[] args = new SimpleDto[]{firstDto, new SimpleDto()};
		Object returnValue = reflectionMethodInvoker.invoke("SimpleClass", "methodThree", new Object[]{args});

		verifyThatReturnValueMatches(firstDto, returnValue);
	}

	@Test
	public void invokingAMethodOnANonExistingTargetShouldThrowException() throws Exception {
		try {
			reflectionMethodInvoker.invoke("SomeClass", "method", new Object[]{});
			fail();
		} catch (MethodInvocationException e) {
			assertEquals(String.format(ReflectionMethodInvoker.NO_TARGET_REGISTERED_WITH_NAME, "SomeClass"), e.getMessage());
		}
	}

	@Test
	public void invokingNonExistingMethodShouldThrowException() throws Exception {
		try {
			reflectionMethodInvoker.invoke("SimpleClass", "method", new Object[]{});
			fail();
		} catch (MethodInvocationException e) {
			assertEquals(String.format(ReflectionMethodInvoker.METHOD_NOT_FOUND_ON_TARGET, "method", "SimpleClass"), e.getMessage());
		}
	}

	@Test
	public void invokingPrivateMethodShouldThrowException() throws Exception {
		try {
			Object returnValue = reflectionMethodInvoker.invoke("SimpleClass", "privateMethod", new Object[]{});
			fail();
		} catch (MethodInvocationException e) {
			assertEquals(String.format(ReflectionMethodInvoker.METHOD_IS_NOT_ACCESSIBLE, "privateMethod"), e.getMessage());
		}
	}

	private void verifyThatReturnValueMatches(Object expectedValue, Object returnValue) {
		assertEquals(expectedValue.getClass(), returnValue.getClass());
		assertEquals(expectedValue, returnValue);
	}

	private void verifyThatTheParametersAre(Class<?>[] methodParameters, Class<?>... expectedClasses) {
		assertEquals(expectedClasses.length, methodParameters.length);
		for (int i = 0; i < methodParameters.length; i++) {

			assertEquals(expectedClasses[i], methodParameters[i]);
		}
	}


	public static class SimpleClass {
		public String methodOne(String s) {
			return s;
		}

		public String methodTwo(String s, int i) {
			return s + i;
		}

		public SimpleDto methodThree(SimpleDto[] dtos) {
			return dtos[0];
		}

		private void privateMethod() {
		}
	}


	public static class SimpleDto {

	}

}