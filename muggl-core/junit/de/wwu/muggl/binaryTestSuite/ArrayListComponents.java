package de.wwu.muggl.binaryTestSuite;

import java.util.ArrayList;

public class ArrayListComponents {

	public final static String METHOD_testArrayListGrow = "testArrayListGrow";

	public static int testArrayListGrow() {
		// taken from sun.invoke.util.BytecodeDescriptor:52

		ArrayList<Class<?>> ptypes = new ArrayList<Class<?>>();
		ptypes.add(Integer.class);
		return ptypes.size();
	}

	public static void main(String[] args) {
		System.out.println(testArrayListGrow());
	}
}
