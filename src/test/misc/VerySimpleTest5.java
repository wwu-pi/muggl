package test.misc;

@SuppressWarnings("all")
public class VerySimpleTest5 {
	
	int doBla(int b, int c) {
		if (b > 5 && c > 5)
		{
			for (int d = 0; d < c; d++)
			{
				b++;
			}
			return b * 2;
		}
		if (c > 5) return c;
		return b;
	}
	
	int strongerEquation(int b) {
		if (b >= -1) {
			if (b > -1) {
				if (b > 6) {
					if (b >= 5) {
						return 5;
					}
					return 4;
				}
				return 3;
			}
			return 2;
		}
		return 1;
	}
	
	int invoketest(int a) {
		a++;
		if (a < 5) return invoketest(a);
		return a;
	}
	
	int treeTest(int a, int b, int c) {
		if (a > 0) {
			if (b > 0) {
				if (c > 0) {
					return -a-b-c;
				}
				return -a-b+c;
			}
			if (c > 0) {
				return -a+b-c;
			}
			return -a+b+c;
		}
		if (b > 0) {
			if (c > 0) {
				return a-b-c;
			}
			return a-b+c;
		}
		if (c > 0) {
			return a+b-c;
		}
		return a+b+c;
	}
	
	int programFlowTest(int a, int b)
	{
		if (a > 0)
		{
			if (b > 0)
			{
				return a + b;
			}
			return a  - b;
		}
		if (b > 0)
		{
			return a + b;
		}
		return a - b;
	}
	
	int doWhileTest(int a)
	{
		do {
			a++;
		} while (a < 5);
		return a;
	}
	
	int forTest(int a) {
		int c = 0;
		for (int b = 0; b < a; b++)
		{
			c += 2;
		}
		return c;
	}
	
	int fastSolutionForTest(int a) {
		if (a < 10)
		{
			int c = 0;
			for (int b = 0; b < a; b++)
			{
				c += 2;
			}
			return c;
		}
		return 1;
	}
	
	int IntegerInt(int a) {
		Integer i = new Integer(a);
		Integer i2 = i * 2;
		return i2;
	}
	
	String getClassTest() {
		Integer i = new Integer(1);
		Class c = i.getClass();
		String a = c.toString();
		return a;
	}
	
	int equationViolation(int b) {
		if (b > 0) {
			if (b < 0) {
				return 4;
			}
			return 0;
		}
		if (b > 0) {
			return 1;
		}
		return 2;
	}
	
	int equationViolation2(int b, int c) {
		if (b > 0) {
			if (b < 0) {
				return 4;
			}
			if (c > 0) {
				return 5;
			}
			if (c < 0) {
				if (c + b < 0) {
					return 6;
				}
				return 7;
			}
			return 0;
		}
		return -1;
	}
	
	int equationViolation2WithReference(int b, int c, @SuppressWarnings("unused") Object o) {
		if (b > 0) {
			if (b < 0) {
				return 4;
			}
			if (c > 0) {
				return 5;
			}
			if (c < 0) {
				if (c + b < 0) {
					return 6;
				}
				return 7;
			}
			return 0;
		}
		return -1;
	}
	
	int oneIf(int b) {
		if (b > 5) {
			return 5;
		}
		return 0;
	}
	
	public static void main(String[] args) {
		Object o = new Object();
		int r = System.identityHashCode(o);
		System.err.println(r);
		
		Integer i = new Integer(5);
		Integer i2 = new Integer(5);
		System.err.println(System.identityHashCode(i));
		System.err.println(System.identityHashCode(i2));
	}
	
}
