package test.misc;



@SuppressWarnings("all")
public class VerySimpleTest4 extends VerySimpleTest3 {
	private static int staticInt = 5 + 6;
	public int fieldInt;
	
	public int getA() {
		return staticInt;
	}
	
	public int arg() {
		return getB();
	}
	
	public int getB() {
		return this.fieldInt;
	}
	
	public int getAa() {
		return aa;
	}
	
	public int getBb() {
		return this.bb;
	}
	
	public int putAndGetA(int q) {
		staticInt = q;
		return staticInt;
	}
	
	public int putAndGetAa(int q) {
		aa = q;
		return aa;
	}
	
	public int putAndGetB(int q) {
		this.fieldInt = q;
		return this.fieldInt;
	}
	
	public int putAndGetBB(int q) {
		this.bb = q;
		return this.bb;
	}
	
	public int abc(int q) {
		int r = 0;
		for (int a = 0; a < 10; a++)
		{
			VerySimpleTest4 v = new VerySimpleTest4();
			v.fieldInt = q;
			r += v.fieldInt; 
		}
		return r;
	}
	
	public static void main(String[] args) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		System.out.println(a);
		try {
		HansInterface hans = new HansClass();
		int a = hans.hansInt(333, 2, 6);
		
			System.out.println(a);
		} catch (Exception e) {
			
		}
		*/
	}
	
	public double addDouble(double a, double b) {
		return addD(a, b);
	}
	
	public double addDouble(double a, double b, double c) {
		return addD(addD(a, b), c);
	}
	
	public static double addD(double a, double b) {
		return a + b;
	}
	
	public double addDouble(int q, double a, int t, double b) {
		q = q + t;
		return a + b;
	}
	
}
