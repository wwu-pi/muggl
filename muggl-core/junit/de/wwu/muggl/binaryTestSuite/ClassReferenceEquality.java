package de.wwu.muggl.binaryTestSuite;

public class ClassReferenceEquality {

	public static boolean test() {
		int one = 1;
		int two = 2;
		Object obj1 = one;
		Object obj2 = two;

		return obj1.getClass() == obj2.getClass() && obj2.getClass() == Integer.class;

	}

	public static void main(String[] args) {
		System.out.println(test());
	}
}
