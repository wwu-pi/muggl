package de.wwu.muggl.binaryTestSuite;

/**
 * Testing the putfield instruction on an objectref/staticref
 * 
 * @author Max Schulze
 *
 */
public class Putfield {
	static Object testobj = new Object();

	public static String METHOD_testPutfieldNull = "testPutfieldNull";

	static boolean testPutfieldNull() {
		testobj = null;
		return testobj == null;
	}

	public static void main(String[] args) {
		System.out.println(testPutfieldNull());
	}
}
