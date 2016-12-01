package de.wwu.muggl.binaryTestSuite;

/**
 * Testing the putfield instruction on an objectref/staticref
 * 
 * @author Max Schulze
 *
 */
public class Putfield {
	static Object testobj = new Object();

	char[] testobj2;
	int primitive;
	public static String METHOD_testPutStaticNull = "testPutStaticNull";

	static boolean testPutStaticNull() {
		testobj = null;
		return testobj == null;
	}

	public static String METHOD_testPutfieldNull = "testPutfieldNull";

	static boolean testPutfieldNull() {
		Putfield neu = new Putfield();
		neu.testPutfieldInst(null);

		return neu.testobj2 == null;
	}

	private void testPutfieldInst(char[] arg1) {
		this.testobj2 = arg1;
	}

	public static void main(String[] args) {
		System.out.println(testPutStaticNull());
		System.out.println(testPutfieldNull());
	}
}
