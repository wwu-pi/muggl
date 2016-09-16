package de.wwu.muggl.binaryTestSuite.invokevirtual;

public class IntegerCache {

	public static void main(String[] args) {
		execute();
	}

	/**
	 * This will internally set-up calls to java.lang.Integer$IntegerCache
	 */
	public static void execute() {
		Integer integer1 = 3;
		Integer integer2 = 3;

		if (integer1 == integer2)
			System.out.println("integer1 == integer2");
		else
			System.out.println("integer1 != integer2");

		Integer integer3 = 300;
		Integer integer4 = 300;

		if (integer3 == integer4)
			System.out.println("integer3 == integer4");
		else
			System.out.println("integer3 != integer4");

	}

}
