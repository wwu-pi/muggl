package test.logicjava;

import de.wwu.logic.annotation.LogicVariable;
import de.wwu.logic.annotation.Search;
import de.wwu.logic.annotation.enums.SearchStrategy;
import de.wwu.logic.solutions.Solution;
import de.wwu.logic.solutions.Solutions;

/**
 *
 */
@SuppressWarnings("all")
public class Rectangle {
	@LogicVariable
	protected double x, y;
	
	@Search(strategy = SearchStrategy.ITERATIVE_DEEPENING, deepeningIncrement = 5)
	public Solutions<Boolean> inRectangle(double x1, double y1, double x2, double y2) {
		if (x1 <= x && x <= x2 && y1 <= y && y <= y2) {
			return new Solutions(new Solution(true));
		} else {
			return new Solutions(new Solution(false));
		}
	}
	
}
