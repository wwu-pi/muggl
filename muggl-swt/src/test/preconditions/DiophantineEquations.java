package test.preconditions;



/**
 * Verification of a solver for diophatine equations.
 * 
 * For the moment, it solves equations of the form
 * 
 * a*x + b*y = gcd(a,b) 
 * 
 * where x and y are integers.
 * 
 * @author Manuel Montenegro
 *
 */
public class DiophantineEquations {
	
	public boolean precondition(int a, int b) {
		return a > 0 && b > 0;
	}
	
	public boolean postcondition(int a, int b, int[] solution) {
		IntegerGcd gcdCalculator = new IntegerGcd();
		int gcd = gcdCalculator.integerGcdBySubstractions(a, b);
		return a * solution[0] + b * solution[1] == gcd;
	}
	
	
	public int[] solveEquation(int a, int b) {
		NaturalDivision divisor = new NaturalDivision();
		int remainder = divisor.remainder(a, b);
		if (remainder == 0) {
			int[] result = new int[2];
			result[0] = 0;
			result[1] = 1;
			return result;
		} else {
			int[] previousIteration = solveEquation(b, remainder);
			int[] result = new int[2];
			result[0] = previousIteration[1];
			result[1] = previousIteration[0] - previousIteration[1] * (a/b);
			return result;
		}
	}
	
	public int checkSolveEquation(int a, int b) {
		if (!precondition(a,b)) { 
			return 1;
		} else {
			int[] solution = solveEquation(a, b);
			if (!postcondition(a, b, solution)) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
