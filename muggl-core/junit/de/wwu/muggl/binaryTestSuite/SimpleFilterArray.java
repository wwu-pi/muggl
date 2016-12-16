package de.wwu.muggl.binaryTestSuite;

public class SimpleFilterArray {
	public final static String METHOD_testArrayEntries = "testArrayEntries";

	public static int testArrayEntries(int a) {
		int[] numbArray = { 1 };
		int counter = 0;

		for (int i : numbArray) {
			if (i > a)
				counter++;
		}

		return counter;
	}
}
