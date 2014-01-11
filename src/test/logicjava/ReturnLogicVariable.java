package test.logicjava;

import de.wwu.logic.annotation.LogicVariable;
import de.wwu.logic.solutions.Solution;
import de.wwu.logic.solutions.Solutions;

/**
 *
 */
@SuppressWarnings("all")
public class ReturnLogicVariable {
	@LogicVariable
	protected int m, n;
	
	public Solutions<Integer> returnSomething() {
		if (m == 1) {
			return new Solutions(new Solution(n));
		} else {
			return new Solutions(new Solution(1));
		}
	}
	
}
