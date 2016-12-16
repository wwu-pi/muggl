package de.wwu.muggl.binaryTestSuite.invokedynamic;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class LambdaDemo {

	public static void main(String[] args) throws Throwable {
		lambdaSugarRunnable();
		lambdaMetafactoryAuto();
		GenerateLambdaMetafactoryManual();
	}

	public final static String METHOD_lambdaMetafactoryAuto = "lambdaMetafactoryAuto";

	public static void lambdaMetafactoryAuto() {
		LambdaDemo myself = new LambdaDemo();

		// with type declaration
		MathOperation addition = (int a, int b) -> a + b;

		// // with out type declaration
		// MathOperation subtraction = (a, b) -> a - b;
		//
		// // with return statement along with curly braces
		// MathOperation multiplication = (int a, int b) -> {
		// return a * b;
		// };
		//
		// // without return statement and without curly braces
		// MathOperation division = (int a, int b) -> a / b;

		System.out.println("10 + 5 = " + myself.operate(10, 5, addition));
		// System.out.println("10 - 5 = " + tester.operate(10, 5, subtraction));
		// System.out.println("10 x 5 = " + tester.operate(10, 5, multiplication));
		// System.out.println("10 / 5 = " + tester.operate(10, 5, division));
		//
		// // with parenthesis
		// GreetingService greetService1 = message -> System.out.println("Hello " + message);
		//
		// // without parenthesis
		// GreetingService greetService2 = (message) -> System.out.println("Hello " + message);
		//
		// greetService1.sayMessage("Mahesh");
		// greetService2.sayMessage("Suresh");
	}

	public final static String METHOD_GenerateLambdaMetafactoryManual = "GenerateLambdaMetafactoryManual";

	/**
	 * The LambdaMetafactory.metafactory is usually what is in a bootstrapMethod for invokedynamic
	 * 
	 * @throws Throwable
	 */
	public static void GenerateLambdaMetafactoryManual() throws Throwable {

		MethodHandles.Lookup lookup = MethodHandles.lookup();

		MethodType mt = MethodType.methodType(void.class);

		// must be a functional interface
		MethodType invokedType = MethodType.methodType(MyFuncIntf.class);
		MethodHandle mh = lookup.findStatic(LambdaDemo.class, "printHelloWorld", mt);

		CallSite lambda = LambdaMetafactory.metafactory(lookup, "performAction", invokedType, mt, mh, mt);

		((MyFuncIntf) lambda.getTarget().invoke()).performAction();
	}

	/**
	 * Very Simple functional interface
	 * 
	 * @author max
	 *
	 */
	interface MyFuncIntf {
		void performAction();
	}

	/**
	 * functional interface with exactly one abstract method
	 * 
	 * @author max
	 *
	 */
	interface MathOperation {
		int operation(int a, int b);
	}

	// interface GreetingService {
	// void sayMessage(String message);
	// }

	private int operate(int a, int b, MathOperation mathOperation) {
		return mathOperation.operation(a, b);
	}

	public final static String METHOD_lambdaSugarRunnable = "lambdaSugarRunnable";

	public static void lambdaSugarRunnable() {
		Runnable r = () -> System.out.println("Hello from executeTest");
		r.run();
	}

	@SuppressWarnings("unused")
	private static void printHelloWorld() {
		System.out.println("Hello, World!");
	}

}
