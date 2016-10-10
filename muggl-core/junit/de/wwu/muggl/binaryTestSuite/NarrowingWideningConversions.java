package de.wwu.muggl.binaryTestSuite;

/**
 * This class shall test the §2.11.4 Type Conversion Instructions
 * 
 * @author Max Schulze
 *
 */
public class NarrowingWideningConversions {
	// NARROWING

	public static byte i2b() {
		int test = 4;
		return (byte) test;
	}

	public static char i2c() {
		int test = 49;
		return (char) test;
	}

	public static short i2s() {
		int test = 4;
		return (short) test;
	}

	public static int l2i() {
		long test = 3245;
		return (int) test;
	}

	public static int f2i() {
		float test = 3L;
		return (int) test;
	}

	public static long f2l() {
		float test = 3L;
		return (long) test;
	}

	public static int d2i() {
		double test = 3.9999999999999;
		return (int) test;
	}

	public static long d2l() {
		double test = 3.9999999999999;
		return (long) test;
	}

	public static float d2f() {
		double test = 3.9999999999999;
		return (float) test;
	}

	// WIDENING
	public static long i2l() {
		int test = 32550;
		return (long) test;
	}

	public static float i2f() {
		int test = 32445;
		return (float) test;
	}

	public static double i2d() {
		int test = 32443;
		return (double) test;
	}

	public static float l2f() {
		long test = 48888;
		return (float) test;
	}

	public static double l2d() {
		long test = 499999;
		return (double) test;
	}

	public static double f2d() {
		float test = 444444;
		return (double) test;
	}

	// implicit ones, via Integer. No full coverage, just spot checks
	public static byte l2b() {
		long test = -7;
		return (byte) test;
	}

	public static short d2s() {
		double test = 3.49999999999999;
		return (short) test;
	}
}
