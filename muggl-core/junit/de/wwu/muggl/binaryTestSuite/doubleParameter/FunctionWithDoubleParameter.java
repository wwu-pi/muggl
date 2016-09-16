package de.wwu.muggl.binaryTestSuite.doubleParameter;

public class FunctionWithDoubleParameter {

	public static void main(String[] args) {
		System.out.println(makeStringWithDoubleParameters(1.11, 2.22));

	}

	public static String makeStringWithDoubleParameters(double d, double e) {
		return "result: " + d + " and " + e;
	}

}
