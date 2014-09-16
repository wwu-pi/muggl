package test.preconditions;


/**
 * Class for proving the correctness of Greatest Common Divisor algorithm
 * 
 * @author Manuel Montenegro
 */
public class IntegerGcd {
	/*
	 * GCD precondition. Both elements must be strictly positive 
	 */
	private boolean precondition(int a, int b) {
		return a > 0 && b > 0;
	}
	
	/*
	 * Weak postcondition. The GCD should be a common divisor, but
	 * not necessarily the greatest one.
	 */
	private boolean weakPostcondition(int a, int b, int gcd) {
		NaturalDivision divisor = new NaturalDivision();
		if (divisor.precondition(a, gcd) && divisor.precondition(b, gcd)) {
			return divisor.remainder(a, gcd) == 0 && divisor.remainder(b, gcd) == 0;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Computes the greatest common divisor of two strictly positive integers via
	 * Euclid's algorithm. (Variant 1)
	 * 
	 * @param a  First integer
	 * @param b  Second integer
	 * @return  Greatest common divisor of a and b
	 */
	public int integerGcdBySubstractions(int a, int b) {
		while (a != b) {
			if (a > b) {
				a = a - b;
			} else {
				b = b - a;
			}
		}
		return a;
	}
	
	/**
	 * Computes the greatest common divisor of two strictly positive integers via
	 * Euclid's algorithm. (Variant 2)
	 * 
	 * @param a  First integer
	 * @param b  Second integer
	 * @return  Greatest common divisor of a and b
	 */
	public int integerGcdByRemainder(int a, int b) {
		NaturalDivision divisor = new NaturalDivision();
		while (b > 0) {
			int tmp = divisor.remainder(a, b);
			a = b;
			b = tmp;
		}
		return a;
	}
	
	
	public int recursiveGcd(int a, int b) {
		if (b == 0) {
			return a;
		} else {
			NaturalDivision divisor = new NaturalDivision();
			return recursiveGcd(b, divisor.remainder(a, b));
		}
	}
	
	/*
	public int integerGcdByRemainderCheckPrecondition(int a, int b) {
		NaturalDivision divisor = new NaturalDivision();
		while (b > 0) {
			int tmp;
			if (!divisor.precondition(a, b)) {
				return 1;
			} else {
				tmp = divisor.remainder(a, b);
			}
			a = b;
			b = tmp;
		}
		return 0;
	}*/
	
	
	public int checkIntegerGcdBySubstractions(int a, int b) {
		if (!precondition(a, b)) return 1;
		int gcd = integerGcdBySubstractions(a, b);
		if (!weakPostcondition(a, b, gcd)) return -1;
		return 0;
	}
	
	public int checkIntegerGcdByRemainder(int a, int b) {
		if (!precondition(a, b)) return 1;
		int gcd = integerGcdByRemainder(a, b);
		if (!weakPostcondition(a, b, gcd)) return -1;
		return 0;
	}
	
	public int checkRecursiveGcd(int a, int b) {
		if (!precondition(a, b)) return 1;
		int gcd = recursiveGcd(a, b);
		if (!weakPostcondition(a, b, gcd)) return -1;
		return 0;
	}
	
}
