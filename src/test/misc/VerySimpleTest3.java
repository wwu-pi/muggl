package test.misc;

@SuppressWarnings("all")
public class VerySimpleTest3 implements SubInterface {
	protected static int aa = 6;
	protected int bb;
	
	public int arrayTestA() {
		// aaload!
		int[] a = {1,2};
		return a[1];
	}
	
	public String arrayTestB() {
		String[] a = {"oh","nein"};
		return a[1];
	}
	
	public double arrayTest2() {
		VerySimpleTest3[] v = new VerySimpleTest3[2];
		v[0] = null;
		v[1] = new VerySimpleTest3();
		return v[1].incDouble(99);
	}
	
	public double incDouble(int a) {
		a += 1;
		return a;
	}
	
	public int finallyTest(int a) {
		int b = a + 2;
		try {
			a = b + a / 4;
			if (a > 5) throw new Error("");
		} finally {
			a++;
		}
		return a;
	}
	
	void tryFinally() {
	    try {
	    	arrayTestB();
	    } finally {
	    	arrayTestA();
	    }
	}
	
	public String stringTest() {
		return "abc";
	}
	
	public String stringTest2() throws Exception {
		throw new Exception(stringTest());
	}
	
	public Double doubleTest() {
		return 2578954687.890D;
	}
	
	public int incALot(int a) {
		a += 500;
		return a;
	}
	
	public static void main(String[] args) {
		String[] strings = new String[5];
		strings[3] = "argl";
		System.out.println(strings[3]);
		System.out.println(0<<8|242);
	}
	
}
