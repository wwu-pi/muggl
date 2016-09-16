package de.wwu.muggl.binaryTestSuite.checkcast;

/**
 * This is to provoke a checkcast byte instruction on which Muggl will crash.
 * 
 * @author max
 *
 * @param <T>
 */
public class Checker<T> {
	private final T t;

	public Checker(T t) {
		super();
		this.t = t;
	}

	public static void main(String[] args) {
		System.out.println(doClassesMatch());
	}

	public static String doClassesMatch() {

		int test = 5;
		Checker<Integer> test2 = new Checker<Integer>(5);

		if (Integer.compare(test, test2.t) == 0) {
			return "They Match";
		}
		return "they don't";

	}
}
