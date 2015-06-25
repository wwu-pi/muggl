package test.preconditions;

public class Average {
	
	public boolean precondition_division(int a, int b) {
		return b != 0;
	}
	
	public double division(double a, int b) {
		return a/b;
	}

	public double average(int[] arr) {
		double sum = 0;
		for (int x : arr) {
			sum += x;
		}
		return division(sum, arr.length);
	}
	
	public int average_check_precondition(int[] arr) {
		int sum = 0;
		for (int x : arr) {
			sum += x;
		}
		if (!precondition_division(sum, arr.length)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
	public static void main(String[] args) {
		Average avg = new Average();
		int[] test = {3,4,5,6,7};
		System.out.println(avg.average(test));
	}
}
