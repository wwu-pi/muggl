package test.logicjava;

import de.wwu.logic.annotation.LogicVariable;
import de.wwu.logic.annotation.Search;
import de.wwu.logic.annotation.enums.SearchStrategy;
import de.wwu.logic.solutions.EmptySolution;
import de.wwu.logic.solutions.Solution;
import de.wwu.logic.solutions.Solutions;

/**
 *
 */
@SuppressWarnings("all")
public class Fermat {
	@LogicVariable
	protected int a, b, c, n;
	
	@Search(strategy = SearchStrategy.ITERATIVE_DEEPENING, deepeningIncrement = 5)
	public Solutions<Integer[]> fermat() {
		if (power(a, n) + power(b, n) == power(c, n)) {
			Integer[] solution = { a, b, c, n };
			return new Solutions<Integer[]>(new Solution<Integer[]>(solution));
		} else {
			return new Solutions<Integer[]>(new EmptySolution());
		}
	}

	public static void main(String... args) {
		Fermat f = new Fermat();
		f.fermatJava();
	}
	
	public void fermatJava() {
		int max = Integer.MAX_VALUE;
		a = 1; b = 1; c = 1; n = 1;
		
		while(power(a, n) + power(b, n) != power(c, n)) {
			a++;
			if (a == max) {
				a = 1;
				b++;
			}
			if (b == max) {
				b = 1;
				c++;
			}
			if (c == max) {
				c = 1;
				n++;
			}
			if (n == max) {
				System.out.println("Fermat was right!");
			}
		}
		
		throw new IllegalStateException("Fermat was wrong!");
	}
	
	protected int power(int b, int e) {
		if (e < 1) {
			throw new UnsupportedOperationException("Not possible.");
		}
		int r = b;
		for (int a = 1; a < e; a++) {
			r *= b;
		}
		return r;
	}
	
}
