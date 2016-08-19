package test.lambda;

import java.util.Arrays;
import java.util.List;

public class CountLargerThan {

	public static boolean countLargerThan(int x, int a, int b, int c, int d, int e) {
		Integer[] ints =  {a, b, c, d, e};
		List<Integer> listy = Arrays.asList(ints);
		return listy.stream().filter(element -> element > x).count() == 1;
	}

}
