package test.misc;

import de.wwu.muggl.vm.classfile.ClassFile;

@SuppressWarnings("all")
public class VerySimpleTest extends SuperClass {

	public int addTest(int a, int b) {
		return a + b;
	}
	
	public static int addOrSub(int a, int b) {
		if (a < b) {
			return a - b;
		}
		return a + b;
	}
	
	public static int choice(int a) {
		int b = a * 2;
		if (a < b + 5) {
			return a;
		}
		return -a;
	}
	
	public static int addOrSubWithMul(int a) {
		int b = a * 2; 
		if (a < b) {
			return a - b;
		}
		return a + b;
	}
	
	public static int staticAddTest(int a, int b) {
		return a + b;
	}
	
	public int addTest2(int a, int b) {
		try {
			return a + b;
		} catch (Exception e) {
			return a;
		}
	}
	
	public int shiftTest(int a, int b, long y, long z) {
		long f = y >> z;
		long g = y << z;
		long h = y >>> z;
		long i = y << a;
		long j = y >> a;
		long k = y >>> a;
		return 1;
	}
	
	public static void main(String... args) {
		VerySimpleTest v = new VerySimpleTest();
		v.shiftTest(100, 3, 2352457547457l, 46457547346l);
	}
	
	public boolean ifTest(double a) {
		a += 4.2;
		a += 0.0001;
		if (a > 4.999)
		{
			return false;
		}
		return true;
	}
	
	public int switching(int ffs) {
		switch (ffs)
		{
			case 0: return 0;
			case 1: return 1;
			case 2: return 2;
			case 3: return 3;
			case 4: return 4;
			case 5: return 5;
			default: return 9999;
		}
	}
	
	public int switching2(int ffs) {
		switch (ffs)
		{
			case 0: return 0;
			case 1: return 1;
			case 2: return 2;
			case 3: return 3;
			case 4: return 4;
			case 60: return 60;
			default: return 9999;
		}
	}
	
	public void exceptionTest(int a) throws Exception {
		try {
			a++;
			a++;
		} catch (Exception e) {
			a--;
		}
		a += 4;
	}
	
	protected final synchronized String prefixTest() {
		return "";
	}
	
	@SuppressWarnings("unused")
	private static strictfp void prefixTest2() {
		
	}
	
	@SuppressWarnings("unused")
	private static java.lang.Error paramTest(double a, int b, float[] c, long[][] d, boolean[][][] e, String f, ClassFile g) {
		return null;
	}
	
	@SuppressWarnings("unused")
	private static java.lang.Error paramTest2(double a, int b, float c, long d, boolean e, String f, ClassFile g, double h, double i, double j, double k, double l, double m, double n, double o, double p, double q, double r, double s, double t, double u, double v, double w, double x, double y, double z,
			Double aa, Float ab, Integer ac, Short ad, Byte ae, Character af, Boolean ag, double ah, double ai, double aj, double ak, double al, double am, double an, double ao, double ap, double aq, double ar, double as, double at, double au, double av, double aw, double ax, double ay, double az,
			double ba, double bb, double bc, double bd, double be, double bf, double bg, double bh, double bi, double bj, double bk, double bl, double bm, double bn, double bo, double bp, double bq, double br, double bs, double bt, double bu, double bv, double bw, double bx, double by, double bz,
			double ca, double cb, double cc, double cd, double ce, double cf, double cg, double ch, double ci, double cj, double ck, double cl, double cm, double cn, double co, double cp, double cq, double cr, double cs, double ct, double cu, double cv, double cw, double cx, double cy, double cz,
			double da, double db, double dc, double dd, double de, double df, double dg, double dh, double di, double dj, double dk, double dl, double dm, double dn, double dp, double dq, double dr, double ds, double dt, double du, double dv, double dw, double dx, double dy, double dz,
			double ea
	) {
		return null;
	}
	
	@SuppressWarnings("unused")
	public void moreParameters(byte a, char b, short c) {
		return;
	}
	
	public String stringTest() {
		return String.valueOf(2);
	}
	
	public int mathTest() {
		return Math.min(5, 8);
	}
	
	public int superTest(int a) {
		return superMethod(a) + superSuperMethod(a);
	}
	
}
