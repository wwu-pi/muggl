package test.logicjava;

import de.wwu.logic.annotation.LogicVariable;
import de.wwu.logic.annotation.Search;
import de.wwu.logic.annotation.enums.SearchStrategy;
import de.wwu.logic.solutions.EmptySolution;
import de.wwu.logic.solutions.Solution;
import de.wwu.logic.solutions.Solutions;

@SuppressWarnings("all")
public class Interval {
	@LogicVariable
	protected double	x;

	@Search(strategy = SearchStrategy.ITERATIVE_DEEPENING, deepeningIncrement = 5)
	public Solutions<Double> inInterval(double x1, double x2) {
		if (x1 <= x && x <= x2) {
			return new Solutions<Double>(new Solution<Double>(x));
		} else {
			return new Solutions<Double>(new EmptySolution());
		}
	}

	public static void main(String... args) {
		Solutions<Double> solutions = (new Interval()).inInterval(3.0, 5.0);
		for (Solution<Double> solution : solutions)
			if (solution.isGround())
				System.out.println(solution.getSolution());
			else
				System.out.println(solution.findExampleResult().getSolution());
	}
}