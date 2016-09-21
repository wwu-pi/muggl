package de.wwu.muggl.binaryTestSuite;

public class ArrayrefToObjectref {

	// test does only produce raw array instructions
	public static int testObjectref() {

		int[] meinArray = { 1, 2, 3, 4, 5 };
		return meinArray.length;
	}

	// test produces bytecode: invokevirtual #20 // Method java/lang/Object.toString:()Ljava/lang/String;
	public static String testObjectref2() {
		int[] meinArray = { 1, 2, 3, 4, 5 };
		return meinArray.toString();
	}

	public static String testObjectref3() {
		int[] meinArray = { 1, 2, 3, 4, 5 };
		Object test = meinArray;

		return test.toString();
	}
}
