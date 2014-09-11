package test.preconditions;


/**
 * Verification of division algorithm on natural numbers
 * 
 * @author Manuel Montenegro
 *
 */
public class NaturalDivision {
	
	class ResultPair {
		private int quotient;
		private int remainder;
		
		public ResultPair(int quotient, int remainder) {
			this.quotient = quotient;
			this.remainder = remainder;
		}

		public int getQuotient() {
			return quotient;
		}

		public int getRemainder() {
			return remainder;
		}
	}
	
	public boolean precondition(int a, int b) {
		return (a >= 0) && (b > 0);
		// If we use the following weaker precondition, Muggl shows that the postcondition
		// does not hold.
		//
		//return b > 0;
	}

		
	public boolean postcondition(int a, int b, int q, int r) {
		return (0 <= r) && (r < b) && (a == b*q + r);
	}
		
	public boolean postconditionArray(int a, int b, int[] result) {
		return (result != null) && (result.length == 2) && 
				(0 <= result[1]) && (result[1] < b) && (a == b*result[0] + result[1]);
	}

	
	public int quotient(int a, int b) {
		int n = 0;
		while (a >= b) {
			a = a - b;
			n = n + 1;
		}
		return n;
	}
	
	public int remainder(int a, int b) {
		while (a >= b) {
			a = a - b;
		}
		return a;
	}
		
	
	/*
	 * Muggl generates an incorrect test case for this example  
	 */
	public ResultPair quotientRemainder(int a, int b) {
		int n = 0;
		while (a >= b) {
			a = a - b;
			n = n + 1;
		}
		return new ResultPair(n, a);		
	}

	public int[] quotientRemainderArray(int a, int b) {
		int n = 0;
		while (a >= b) {
			a = a - b;
			n = n + 1;
		}
		return new int[] {n, a};
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
	

}
