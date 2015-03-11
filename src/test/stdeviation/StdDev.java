package test.stdeviation;

public class StdDev {

	static double eps = 0.000001;

	public static double sqrt(double r) {
		assert (r >= 0.0); // precondition
		double a, a1 = 1.0;
		do {
			a = a1;
			// correct: a1 = (a+r/a)/2.0;
			a1 = a + r / a / 2.0; // erroneous!
			System.out.println("a:" + a + ", a1:" + a1);
			assert a == 1.0 || (a1 > 1.0 ? a1 < a : a1 > a) : "no convergence";
		} while (Math.abs(a - a1) >= eps);
		assert (Math.abs(a1 * a1 - r) < eps); // postcondition
		return a1;
	}

	public static double stddev(double[] a) {
		int n = a.length;
		assert (n > 0); // precondition
		double sum = 0.0;
		double sumsqr = 0.0;
		for (int i = 0; i < n; i++) {
			sum += a[i];
			sumsqr += a[i] * a[i];
		}
		double mean = sum / n;
		return sqrt(sumsqr / n - (mean * mean));
	}

}
