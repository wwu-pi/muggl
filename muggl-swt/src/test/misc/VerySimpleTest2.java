package test.misc;


@SuppressWarnings("all")
public class VerySimpleTest2 {

	public int linkTest(int a, int b) {
		VerySimpleTest vst = new VerySimpleTest();
		return vst.addTest(a, b);
	}
	
	public int divideByZero(int a) {
		return a / 0;
	}
	
	public int exceptionTest(int a) {
		try {
			if (a >= 0) throw new Exception("a");
		} catch (Exception e) {
			return 5;
		}
		return 0;
	}
	
	public int exceptionTest2(int a) {
		try {
			if (a >= 0) throw new IndexOutOfBoundsException("a");
		} catch (Exception e) {
			return 5;
		}
		return 0;
	}
	
	public int exceptionTest3(int a) {
		try {
			if (a >= 0) throw new IndexOutOfBoundsException("a");
		} catch (IndexOutOfBoundsException e) {
			return 3;
		} catch (Exception e) {
			return 5;
		}
		return 0;
	}
	
	public int exceptionTest3a(int a) {
		try {
			if (a >= 0) throw new Exception("a");
		} catch (IndexOutOfBoundsException e) {
			return 3;
		} catch (Exception e) {
			return 5;
		}
		return 0;
	}
	
	/*
	public static void main(String[] args) {
		VerySimpleTest2 t = new VerySimpleTest2();
		int b = t.exceptionTest4(0);
	}
	*/
	
	public int exceptionTest4(int a) {
		try {
			if (a >= 0) throw new IndexOutOfBoundsException("a");
			a += 5;
		} finally {
			a++;
		}
		return a;
	}
	
	public int exceptionTest4a(int a) {
		try {
			if (a >= 0) throw new IndexOutOfBoundsException("a");
			a+= 5;
		} catch (Exception e) {
			a++;
		} finally {
			a += 2;
		}
		return a;
	}
	
	private int exceptionTest5a() throws Exception {
		throw new Exception("a");
	}
	
	public int exceptionTest5() {
		try {
			return exceptionTest5a();
		} catch (Exception e) {
			return 5;
		}
	}
	
	private int exceptionTest6a() throws Exception {
		throw new RuntimeException("a");
	}
	
	public int exceptionTest6() {
		try {
			return exceptionTest6a();
		} catch (IndexOutOfBoundsException e) {
			return 3;
		} catch (RuntimeException e) {
			return 4;
		} catch (Exception e) {
			return 5;
		}
	}
	
	private int exceptionTest7a() {
		return 1 / 0;
	}
	
	public int exceptionTest7() {
		try {
			return exceptionTest7a();
		} catch (Exception e) {
			return 5;
		}
	}
	
	public int exceptionTest8() {
		return exceptionTest7a();
	}
	
	public int selfInvocationTest(int a) {
		a++;
		if (a > 5) return a;
		return selfInvocationTest(a);
	}
	
}
