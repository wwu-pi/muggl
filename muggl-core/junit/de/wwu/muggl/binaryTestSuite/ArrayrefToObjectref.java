package de.wwu.muggl.binaryTestSuite;

public class ArrayrefToObjectref {

	public final static String METHOD_testObjectref = "testObjectref";

	// test does only produce raw array instructions
	public static int testObjectref() {

		int[] meinArray = { 1, 2, 3, 4, 5 };
		return meinArray.length;
	}

	public final static String METHOD_testObjectref2 = "testObjectref2";

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

	public final static String METHOD_testIntegerPutByteArray = "testIntegerPutByteArray";

	public static int testIntegerPutByteArray() {
		byte[] array = new byte[5];

		array[3] = 2 + 4;
		return array[3];
	}

	public static void main(String[] args) {
		System.out.println(testIntegerPutByteArray());
	}
}
