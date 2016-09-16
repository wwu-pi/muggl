package de.wwu.muggl.binaryTestSuite.checkcast;

/**
 * This is to provoke a checkcast byte instruction on which Muggl will crash.
 * 
 * @author max
 * 
 * @param <T>
 */
public class CheckerAlternative<T> {
	private final T t;

	public CheckerAlternative(T t) {
		super();
		this.t = t;
	}

	public static void main(String[] args) {
		System.out.println(produceCheckcastInstr());
	}

	public static String produceCheckcastInstr() {

		int test = 5;
		CheckerAlternative<Integer> test2 = new CheckerAlternative<Integer>(5);
		return Integer.toString(test + test2.t);
	}
}
