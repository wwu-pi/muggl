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
				(0 <= result[1]) && (result[1] < Math.abs(b)) && (a == b*result[0] + result[1]);
	}

	
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
	
	public int simpleArray(int x) {
		int[] numbers = {x,0};
		return numbers[1];
	}
	
	
/*	
	public int nonNaturalQuotientRemainderArrayCheckPrecondition(int a, int b) {
		if (b < 0) {
			if (!precondition(a, -b)) return 1; 
			int[] result = quotientRemainderArray(a, -b);
			return 0;
		} else if (a < 0) {
			if (!precondition(-a, b)) return 2; 
			int[] result = quotientRemainderArray(-a, b);
			return 0;
		} else {
			if (!precondition(a, b)) return 3; 
			return 0;
		}
	}
	
	
	public int checkNonNaturalQuotientRemanderArray(int a, int b) {
		if (!nonNaturalDivisionPrecondition(a, b)) return 1;
		int[] result = nonNaturalQuotientRemainderArray(a, b);
		if (!nonNaturalDivisionPostconditionArray(a,b,result)) return -1;
		return 0;		
	}
	
	public int checkQuotientRemainder(int a, int b) {
		if (!precondition(a,b)) return 1;
		int q = quotient(a,b);
		int r = remainder(a,b);
		if (!postcondition(a,b,q,r)) return -1;
		return 0;
	}
	
	public int checkQuotientRemainderArray(int a, int b) {
		if (!precondition(a,b)) return 1;
		int[] result = quotientRemainderArray(a, b);
		if (!postconditionArray(a,b,result)) return -1;
		return 0;		
	}
	*/

}
