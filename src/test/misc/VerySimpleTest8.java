package test.misc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;

@SuppressWarnings("all")
public class VerySimpleTest8 {

	public int ldcQuickTest(int a) {

		if (a < 50) {
			a *= 2;
		}

		int r = 100000;

		if (a > r) {
			return r + 5;
		}

		return r - 5;
	}

	public boolean booleanTest(boolean a, boolean b) {
		if (a) return true;
		if (b) return false;
		return (a | b);
	}

	public static void fibTimeWrapper(long x) {
		long now = System.nanoTime();
		fibonacci(x);
		long time = System.nanoTime() - now;
		System.out.println(time);
	}

	/**
	 * Fibonacci numbers.
	 * 
	 * @param x
	 * @return The fibonacci number of x.
	 */
	public static long fibonacci(long x) {
		if (x == 0) return 0l;
		if (x == 1) return 1l;
		return fibonacci(x - 1) + fibonacci(x - 2);
	}

	public static void fibTimeWrapper(int x) {
		long now = System.nanoTime();
		fibonacci(x);
		long time = System.nanoTime() - now;
		System.out.println(time);
	}

	/**
	 * Fibonacci numbers.
	 * 
	 * @param x
	 * @return The fibonacci number of x.
	 */
	public static long fibonacci(int x) {
		if (x == 0) return 0l;
		if (x == 1) return 1l;
		return fibonacci(x - 1) + fibonacci(x - 2);
	}

	public static int blaTest(int a) {
		if (a < 2) {
			a = blaTest(a);
		}
		return a + 1;
	}

	/*
	 * public static void main(String... args) { VerySimpleTest8 v = new VerySimpleTest8();
	 * v.fibTimeWrapper(31L); }
	 */

	public static void main(String... args) {
		
	e();	
		
		int passes = 16;
    	long characters = 8192;
    	int factor = 2;
    	File file = new File("a.txt");
    	try {
    		OutputStream fileOutputStream = new FileOutputStream(file);
    		BufferedOutputStream buffered = new BufferedOutputStream(fileOutputStream);
    	
    		for (int a = 0; a < passes; a++) {
    			characters *= factor;
    			long timeStart = System.nanoTime();
				for (int b = 0; b < characters; b++) {
					buffered.write(-128);
				}
				
				long timeEnd = System.nanoTime();
				double timeSeconds = (timeEnd - timeStart) / 1000000000d;
				double perSecond = Math.round((((double) characters) / timeSeconds) * 100d) / 100d;
				String perSecondString;
				if (perSecond > 1024d) {
					perSecondString = Double.toString(Math.round(perSecond / 1024d * 100d) / 100d) + "k";
				} else {
					perSecondString = Double.toString(perSecond);
				}
				System.out.println((a + 1) + ": Wrote\t" + characters + "\tin\t" + timeSeconds + "s \t(" + perSecondString + " characters per second)");
    		}
    		
    		buffered.close();
    		fileOutputStream.close();
    		
		} catch (IOException e) {
			// TODO
		}
		int a = 0;
    }

	public static void foreachtest() {
		int[] a = { 1, 2, 3, 4, 5, 6, };
		int c = 0;
		for (int b : a) {
			c += b;
		}
	}

	public static int gcd(int m, int n) {
		if (m < n) {
			int t = m;
			m = n;
			n = t;
		}
		int r = m - n;
		if (r == 0) return n;
		return gcd(n, r);
	}

	int	x, y;

	public void dup() {
		x = (x ^= y) ^ (y ^= x);
	}

	public void d() {
		double d = 4354356.5687;
		Double d2 = 2358389.656;
	}
	
	public static void e() {
		int i = 6;
		try {
			try {
				if (i > 1) throw new IllegalArgumentException("argl");
			} catch (IllegalArgumentException e) {
				if (i > 2) throw new SymbolicExecutionException();
			} finally {
				if (i > 0) throw new ExecutionException();
			}
		} catch (ExecutionException e) {
			int a = 7;
		}
		int a = 5;
	}
	
	/**
	 * TODO!
	 *
	 * @return
	 */
	public static int arrayref() {
		int[] a = new int[3];
		a[1] = 3;
		Object b = a;
		int[] c = (int[]) b;
		return c[1];
	}
	
}
