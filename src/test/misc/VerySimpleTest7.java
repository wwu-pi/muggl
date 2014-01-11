package test.misc;


@SuppressWarnings("all")
public class VerySimpleTest7 {
	private static short[] a;
	private int c;

	public void abc() {
		throw new NullPointerException();
	}
	
	void finallyTest() {
		int e = 0;
		try {
			abc();
		} finally {
			e = 1;
		}
	}
	
	protected void finallyTest2() {
		int e = 0;
		try {
			abc();
		} finally {
			if (e == 2)
				throw new ArithmeticException();
			e = 5;
		}
	}
	
	private void athrow() {
		RuntimeException e;
		int a = 5;
		if (a == 6) {
			e = new NullPointerException();
		} else {
			e = new ArithmeticException();
		}
		throw e;
	}
	
	public int argl() {
		int[] a = new int[2];
		a[1] = 5;
		return a[1];
	}
	
	public void a() {
		String a = "abc";
		Class r = a.getClass();
		int q = r.getModifiers();
		int e = q * 2;
	}
	
	private static void setC(VerySimpleTest7 v) {
		v.c = 5;
	}
	
	public static void massiveLdcTest() {
		long now = System.nanoTime();
		for (int a = 0; a < 1000000; a++)
		{
			String hans = "a";
		}
		long time = System.nanoTime() - now;
		System.out.println(time);
	}
	
	public static void massiveLdcTest2() {
		long now = System.nanoTime();
		for (int a = 0; a < 1000000; a++)
		{
			String hans = "\"Hallo, wie geht es euch?\", fragte der geisteskranke, aber nicht besonders scheue Hase.";
		}
		long time = System.nanoTime() - now;
		System.out.println(time);
	}
	
	public static void massiveLdcTest3() {
		long now = System.nanoTime();
		for (int a = 0; a < 1000000; a++)
		{
			String hans = " * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />" +
					" * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />" +
					" * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />" +
					" * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />" +
					" * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />" +
					" * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />" +
					" * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />" +
					" * The ConfigReader offers static methods to load and save settings from a configuration file. All\r\n" + 
					" * methods might be used with a file explicitly supplied as a FileReader or Writer. If they are\r\n" + 
					" * called without arguments, the currently set configuration file is uses.<br />";
		}
		long time = System.nanoTime() - now;
		System.out.println(time);
	}
	
	public void insertStringCacheTest() {
		String a1 = "";
		String a2 = "";
		
		String a3 = "a";
		String a4 = "a";
		String a5 = "abc";
		
		String a6 = "bc";
		String a7 = "b";
		
		String a8 = "cccccc";
		String a9 = "ccc";
		String a10 = "cc";
		String a11 = "c";
		
		String a12 = "\u4563bc";
		String a13 = "ab\u4563c";
		String a14 = "ab\u4563d";
		String a15 = "ab\u4563";
	}
	
	public void stringReferenceValueTimeTest() {
		long now = System.nanoTime();
		
		for (int a = 0; a < 50000; a++)
		{
			String argl = "a";
			String bargl = String.valueOf("a");
			String barf = "a" + argl;
			String barf2 = "a" + argl;
			String carf = "a" + "a";
			String carf2 = "a" + "a";
			String carf3 = "a";
			carf3 += "a";
			String kurt1 = "";
			String a1 = "";
			String a2 = "";
			String b1 = "\"Hallo, wie geht es euch?\", fragte der geisteskranke, aber nicht besonders scheue Hase.";
			String b2 = "\"Hallo, wie geht es euch?\", fragte der geisteskranke, aber nicht besonders scheue Hase.";
			String b3 = "\"Hallo";
			String b5 = "a";
			String b6 = "c";
			String b7 = "d";
			String b8 = "e";
			String b9 = "f";
			String b10 = "g";
			String b11 = "h";
			String b12 = "e";
			String b13 = "eggege";
			String b14 = "eggegeegeg";
			String b15 = "egeggege";
			String b16 = "ggenzhgenngoewge";
			String b17 = "gwehngngews";
			String b18 = "j hnsfbugewbzegwnogew";
			String b19 = "wgjngnoewngeu";
			String b20 = " pngrn wegn";
			String b21 = "whwgngw7";
			String b22 = "jnmwegn9hgerngew9";
			
			switch (a)
			{
				case 490:
					System.out.println("500:\t" + (System.nanoTime() - now));
					break;
				case 4990:
					System.out.println("5000:\t" + (System.nanoTime() - now));
					break;
				case 24990:
					System.out.println("25000:\t" + (System.nanoTime() - now));
					break;
			}
		}
		
		System.out.println("50000:\t" + (System.nanoTime() - now));
	}
	
	public void stringReferenceValueTest() {
		// All but gargl should be the same reference values.
		String argl = "a";
		String bargl = String.valueOf("a");
		String sargl = "a";
		String gargl = new String("a");
		String fargl = gargl.intern();
		
		// All should be different reference values but carf and carf2.
		String narf = argl + argl;
		String barf = "a" + argl;
		String barf2 = "a" + argl;
		String carf = "a" + "a";
		String carf2 = "a" + "a";
		String carf3 = "a";
		carf3 += "a";
		String darf = gargl.intern() + String.valueOf("a");
		String garf = narf.substring(0,1) + carf.substring(1);
		String garf2 = narf.substring(0,1) + carf.substring(1);

		// All should be the same reference values.
		String hans = narf.intern();
		String hans2 = barf.intern();
		String hans3 = barf2.intern();
		String hans4 = carf.intern();
		String hans5 = carf3.intern();
		String hans6 = darf.intern();
		String hans7 = garf.intern();
		String hans8 = garf2.intern();
		
		// 1 and 3 should be the same reference values.
		String kurt1 = "";
		String kurt2 = new String("");
		String kurt3 = kurt2.intern();
		String kurt4 = "a".substring(1);
		
		// The first three should be the same reference values.
		String a1 = "";
		String a2 = "";
		String a3 = a2.intern();
		String a4 = a1 + a2;
		String a5 = a1 + a3.intern();
		String a6 = a5.intern();
		
		// These should be the same reference values, but the third and the fourth one.
		String b1 = "\"Hallo, wie geht es euch?\", fragte der geisteskranke, aber nicht besonders scheue Hase.";
		String b2 = "\"Hallo, wie geht es euch?\", fragte der geisteskranke, aber nicht besonders scheue Hase.";
		String b3 = "\"Hallo";
		String b4 = b2.substring(6);
		String b5 = (b3 + b4).intern();
	}
	
	public static void main(String... args) {
		massiveLdcTest();
		massiveLdcTest2();
		massiveLdcTest3();
		
		String a = new String("a");
		String b = new String("a");
		String copy = a;
		String c = new String(a);
		char[] chars = {'\u0061'};
		String d = new String(chars);
		boolean bool = a == b;
		bool = a == copy;
		bool = a == c;
		bool = a == d;
		
		int r = args.length;
		System.out.print(r);
	}
	
}

class Other { static String hello = "Hello"; }
