package de.wwu.muggl.binaryTestSuite;

/**
 * Test language features introduced with Java 7, should only affect the compiler
 * 
 * @author Max Schulze
 *
 */
public class Java7Changes {

	public final static String METHOD_testBinaryInLiterals = "testBinaryInLiterals";

	static int testBinaryInLiterals() {
		byte test = (byte) 0b01010101;
		return Byte.valueOf(test).intValue();
	}

	public final static String METHOD_testUnderscoreLiteral = "testUnderscoreLiteral";

	static int testUnderscoreLiteral() {
		int test = 1_234_567;
		return test;
	}

	public final static String METHOD_testStringSwitch = "testStringSwitch";

	static int testStringSwitch() {
		String weekday = "Monday";
		switch (weekday) {
		case "Sunday":
			return 7;
		case "Monday":
			return 1;
		default:
			return -1;
		}
	}

	public static void main(String[] args) {

		System.out.println(testBinaryInLiterals());
		System.out.println(testUnderscoreLiteral());
		System.out.println(testStringSwitch());
	}
}
