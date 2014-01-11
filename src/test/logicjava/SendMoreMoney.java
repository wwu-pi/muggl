package test.logicjava;

import de.wwu.logic.annotation.LogicVariable;
import de.wwu.logic.annotation.Search;
import de.wwu.logic.annotation.enums.SearchStrategy;
import de.wwu.logic.solutions.EmptySolution;
import de.wwu.logic.solutions.Solution;
import de.wwu.logic.solutions.Solutions;

@SuppressWarnings("all")
public class SendMoreMoney {
	@LogicVariable
	protected int e, d, m, n, o, r, s, y;
	
	@Search(strategy = SearchStrategy.DEPTH_FIRST)
	public Solutions<Integer[]> smm() {
		if ((s * 1000 + e * 100 + n * 10 + d)
				+ (m * 1000 + o * 100 + r * 10 + e) == (m * 10000 + o
				* 1000 + n * 100 + e * 10 + y)) {
			int[] variables = {e, d, m, n, o, r, s, y};
			if (alldifferent(variables)){
				Integer[] solution = {e, d, m, n, o, r, s, y};
				return new Solutions(new Solution<Integer[]>(solution));
			}
		}
		return new Solutions(new EmptySolution());
	}
	
	public boolean alldifferent(int[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = i + 1; j < a.length; j++) {
				if (a[i] == a[j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void main(String... args) {
		SendMoreMoney sendMoney = new SendMoreMoney();
		Solutions<Integer[]> solutions = sendMoney.smm();

		for (Solution<Integer[]> solution : solutions) {
			Integer[] values = solution.getSolution();
			for (int value : values) {
				System.out.println(value);
			}
		}
	}
	
}
