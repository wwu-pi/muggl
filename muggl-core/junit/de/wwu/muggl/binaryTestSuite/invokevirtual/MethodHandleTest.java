package de.wwu.muggl.binaryTestSuite.invokevirtual;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.AccessController;
import java.security.PrivilegedAction;

import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class MethodHandleTest {

	public static void main(String[] args) throws Throwable {
		test_methodHandleArray();
		findVirtualInvokeExact();
		System.out.println("1");
		findStaticInvokeExact();
		System.out.println("2");
		testWithBootstrap();
		execute();
	}

	public final static String METHOD_methodHandleArray = "test_methodHandleArray";
	@CallerSensitive
	public static void test_methodHandleArray() {
		// done int MethodHandleImpl.java:1083
		MethodHandle[] FAKE_METHOD_HANDLE_INVOKE = new MethodHandle[2];
	}
	
	public final static String METHOD_findVirtualInvokeExact = "findVirtualInvokeExact";

	public static void findVirtualInvokeExact() throws Throwable {
		String s;
		MethodType mt;
		MethodHandle mh;
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		System.out.println("markermax --29");
		// mt is {(char,char) => String}
		mt = MethodType.methodType(String.class, char.class, char.class);
		System.out.println("markermax --32");
		mh = lookup.findVirtual(String.class, "replace", mt);
		System.out.println("markermax --34");
		// (Ljava/lang/String;CC)Ljava/lang/String;
		s = (String) mh.invokeExact("daddy", 'd', 'n');
		assert (s.equals("nanny"));
	}

	public final static String METHOD_findStaticInvokeExact = "findStaticInvokeExact";

	public static void findStaticInvokeExact() throws Throwable {

		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle mh = lookup.findStatic(MethodHandleTest.class, "printHelloWorld",
				MethodType.methodType(void.class));
		mh.invokeExact();
	}

	@SuppressWarnings("unused")
	private static void printHelloWorld() {
		System.out.println("Hello, World!");
	}

	/**
	 * Mock an invokedynamic call with call to Bootstrap and following invoke
	 * 
	 * @throws Throwable
	 */
	public final static String METHOD_testWithBootstrap = "testWithBootstrap";

	public static void testWithBootstrap() throws Throwable {
		CallSite callSite = mockInvoke(MethodHandles.lookup(), "printHelloWorld", MethodType.methodType(void.class));

		callSite.getTarget().invokeExact();
	}

	private static MethodHandle mh;

	/**
	 * Mock the call to java.lang.invoke.MethodHandle.invoke with the minimal arguments
	 * 
	 * @param caller
	 * @param name
	 * @param type
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	private static CallSite mockInvoke(MethodHandles.Lookup caller, String name, MethodType type)
			throws NoSuchMethodException, IllegalAccessException {

		mh = caller.findStatic(caller.lookupClass(), name, MethodType.methodType(void.class));

		if (!type.equals(mh.type()))
			mh = mh.asType(type);

		return new ConstantCallSite(mh);
	}

	public static void withArgs() throws Throwable {

		MethodHandles.Lookup lookup = MethodHandles.lookup();

		// ??????? #printArgs(Object[]) ????
		Class<?> callerClass = lookup.lookupClass(); // (who am I?)
		MethodHandle printArgsMethod = lookup.findStatic(callerClass, "printArgs1",
				MethodType.methodType(String.class, Object[].class));

		CallSite callSite1 = new ConstantCallSite(printArgsMethod);
		String obj = (String) callSite1.dynamicInvoker().invokeExact((Object[]) new String[] { "xxx", "yyy" });
		assert (obj.contentEquals("printArgs: 2"));
		obj = (String) callSite1.dynamicInvoker().invoke("xxx", "yyy", 111);
		assert (obj.contentEquals("printArgs: 3"));

	}

	@SuppressWarnings("unused")
	private static String printArgs1(Object... args) {
		// System.out.println(Arrays.deepToString(args));
		return "printArgs: " + args.length;
	}

	public static void execute() throws Throwable {
		// Object x, y;
		// String s;
		// int i;
		// MethodType mt;
		// MethodHandle mh;
		// MethodHandles.Lookup lookup = MethodHandles.lookup();
		// // mt is {(char,char) => String}
		// mt = MethodType.methodType(String.class, char.class, char.class);
		// mh = lookup.findVirtual(String.class, "replace", mt);
		// // (Ljava/lang/String;CC)Ljava/lang/String;
		// s = (String) mh.invokeExact("daddy", 'd', 'n');
		// assert (s.equals("nanny"));
		// // weakly typed invocation (using MHs.invoke)
		// s = (String) mh.invokeWithArguments("sappy", 'p', 'v');
		// assert (s.equals("savvy"));
		// // mt is {Object[] => List}
		// mt = MethodType.methodType(java.util.List.class, Object[].class);
		// mh = lookup.findStatic(java.util.Arrays.class, "asList", mt);
		// // mt is {(Object,Object,Object) => Object}
		// // mt = MethodType.genericMethodType(3);
		// // mh = MethodHandles.collectArguments(mh, mt);
		// // // mt is {(Object,Object,Object) => Object}
		// // // (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
		// // x = mh.invokeExact((Object) 1, (Object) 2, (Object) 3);
		// // assert (x.equals(java.util.Arrays.asList(1, 2, 3)));
		// // mt is { => int}
		// mt = MethodType.methodType(int.class);
		// mh = lookup.findVirtual(java.util.List.class, "size", mt);
		// // (Ljava/util/List;)I
		// i = (int) mh.invokeExact(java.util.Arrays.asList(1, 2, 3));
		// assert (i == 3);
		// // mt = MethodType.methodType(void.class, String.class);
		// // mh = lookup.findVirtual(java.io.PrintStream.class, "println", mt);
		// // mh.invokeExact(System.out, "Hello, world.");
		// // // (Ljava/io/PrintStream;Ljava/lang/String;)V

	}

	// boolean problem that arised

	static boolean DEBUG_METHOD_HANDLE_NAMES = false;

	public final static String METHOD_testBoolean = "testBoolean";

	public static void testBoolean() {
		final Object[] values = { false };
		// AccessController.doPrivileged(new PrivilegedAction<Void>() {
		// public Void run() {
		// System.out.println("testing");
		values[0] = Boolean.getBoolean("java.lang.invoke.MethodHandle.DEBUG_NAMES");
		// return null;
		// }
		// });
		System.out.println((Boolean) values[0]);
		DEBUG_METHOD_HANDLE_NAMES = (Boolean) values[0];
	}

}
