package test.preconditions;


/**
 * Verification of division algorithm on natural numbers
 * 
 * @author Manuel Montenegro
 *
 */
public class IntegerDivision {
	
	public boolean precondition(int a, int b) {
		return b != 0;
	}

	
	public boolean postcondition(int a, int b, int[] result) {
		return (result != null) && (result.length == 2) && 
				(0 <= result[1]) && (result[1] < Math.abs(b)) && (a == b * result[0] + result[1]);
	}

	
	/*
	 * Integer division algorithm. It relies on the algorithm of
	 * natural division.
	 * 
	 * Adapted from: 
	 * http://en.wikipedia.org/wiki/Division_algorithm#Division_by_repeated_subtraction
	 */
	public int[] quotientRemainderArray(int a, int b) {
		if (b < 0) {
			int[] result = quotientRemainderArray(a, -b);
			return new int[] { -result[0], result[1] };
		} else if (a < 0) {
			int[] result = quotientRemainderArray(-a, b);
			if (result[1] == 0) {
				return new int[] { -result[0], 0 };
			} else {
				return new int[] { -result[0] - 1, b - result[1] };
			}
		} else {
			NaturalDivision natDivisor = new NaturalDivision();
			return natDivisor.quotientRemainderArray(a, b);
		}
	}
	
	/*
	 * Dummy example showing that numbers array is left uninitialized
	 */
	public int simpleArray(int x) {
		int[] numbers = {x,0};
		return numbers[1];
	}
}
