package de.wwu.muggl.binaryTestSuite.invokevirtual;

public class IntegerCache {

	public static void main(String[] args) {
		execute();
	}

	/**
	 * This will internally set-up calls to java.lang.Integer$IntegerCache IntegerCache usually caches up to [-127...
	 * 128]
	 */
	public static final String METHOD_execute = "execute";

	public static int execute() {
		int ret = 0;
		Integer integer1 = 3;
		Integer integer2 = 3;

		if (integer1 == integer2) {
			System.out.println("integer1 == integer2");
			ret += 1;
		} else
			System.out.println("integer1 != integer2");

		Integer integer3 = 300;
		Integer integer4 = 300;

		if (integer3 == integer4) {
			System.out.println("integer3 == integer4");
			ret += 2;
		} else
			System.out.println("integer3 != integer4");
		return ret;
	}

}
