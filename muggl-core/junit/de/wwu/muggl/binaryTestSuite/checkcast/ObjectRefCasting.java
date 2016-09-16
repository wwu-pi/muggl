package de.wwu.muggl.binaryTestSuite.checkcast;

/**
 * there was a bug with objectref wrongly casting the return values of the execution
 * 
 * @author max
 *
 */
public class ObjectRefCasting {
	public static void main(String[] args) {
		System.out.println(returnJavaLangInteger());
		System.out.println(returnJavaLangIntegerCasted());
		System.out.println(returnInt());
		System.out.println(returnIntCasted());
	}

	private static Integer returnJavaLangInteger() {
		Integer test = 35;
		return test;
	}

	private static Integer returnJavaLangIntegerCasted() {
		int test = 34;
		return test;
	}

	private static int returnInt() {
		int test = 33;
		return test;
	}

	private static int returnIntCasted() {
		Integer test = 32;
		return test;
	}

}
