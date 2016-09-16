package de.wwu.muggl.binaryTestSuite.invokedynamic;

public class LambdaDemoMinimal {

	public static void main(String[] args) {
		Runnable r = () -> System.out.println("Hello from executeTest");
		r.run();
		//executeTest();
	}

//	private static void executeTest() {
//		Runnable r = () -> System.out.println("Hello from executeTest");
//		r.run();
//	}

}
