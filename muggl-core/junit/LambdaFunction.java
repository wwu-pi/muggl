import java.util.function.Function;

public class LambdaFunction {

	public static void main(String[] args) {

		// Create a Function from a lambda expression.
		// ... It returns the argument multiplied by two.
		Function<Integer, Integer> func = new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				return t * 2;
			}
		};

		// Apply the function to an argument of 10.
		int result = func.apply(10);
		System.out.println(result);
	}

}
