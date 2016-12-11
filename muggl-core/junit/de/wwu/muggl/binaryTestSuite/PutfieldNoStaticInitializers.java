package de.wwu.muggl.binaryTestSuite;

/**
 * Testing the putfield instruction on an objectref/staticref
 * 
 * @author Max Schulze
 *
 */
public class PutfieldNoStaticInitializers {
	static Object testobj;

	char[] testobj2;
	static int primitive;
	static boolean prim2;

	static boolean testPutStaticNull() {
		testobj = null;
		return testobj == null;
	}

	static boolean testPutfieldNull() {
		PutfieldNoStaticInitializers neu = new PutfieldNoStaticInitializers();
		neu.testPutfieldInst(null);

		return neu.testobj2 == null;
	}

	static boolean testPutStaticBoolean(boolean whatToStore) {
		prim2 = whatToStore;
		return prim2 == false;
	}

	static boolean testPutStaticInt(int whatToStore) {
		primitive = whatToStore;
		return primitive > 2;
	}

	private void testPutfieldInst(char[] arg1) {
		this.testobj2 = arg1;
	}

	public static void main(String[] args) {
		System.out.println(testPutStaticNull());
		System.out.println(testPutfieldNull());
	}
}
