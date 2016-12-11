package de.wwu.muggl.binaryTestSuite.doubleParameter;

public class FunctionWithDoubleParameter {

	public static void main(String[] args) {
		System.out.println(makeStringWithDoubleParameters(1.23));

	}

	public static final String METHOD_makeStringWithDoubleParameters = "makeStringWithDoubleParameters";

	public static String makeStringWithDoubleParameters(double d) {
		return "result: " + d;
	}

	public static final String METHOD_calcWithDoubleParameters = "calcWithDoubleParameters";

	public static double calcWithDoubleParameters(double d, double e) {
		return d - e;
	}

}
