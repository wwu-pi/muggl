package de.wwu.muggl.binaryTestSuite.invokespecial;

public class LambdaDemoMinimal {

	public static void main(String[] args) {
		executeTest();
	}

	private static void executeTest() {
		Runnable r = () -> System.out.println("Hello from executeTest");
		r.run();
	}

}
