package de.wwu.muggl.binaryTestSuite;

import java.util.ArrayList;
import java.util.List;

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

	public final static String METHOD_testIntegerPutByteArray2 = "testIntegerPutByteArray2";

	// half a boxing issue, half array - put
	public static int testIntegerPutByteArray2() {
		Byte[] array = new Byte[5];
		array[3] = 2 + 4;
		return array[3];
	}

	public final static String METHOD_ArrayRefObjectrefBoolean = "ArrayRefObjectrefBoolean";

	public static boolean ArrayRefObjectrefBoolean() {
		List<Boolean> list1 = new ArrayList<>();
		list1.add(true);

		if (list1.get(0)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(testIntegerPutByteArray());
		System.out.println(testIntegerPutByteArray2());
		System.out.println(testObjectref());
		System.out.println(testObjectref2());
	}
}
