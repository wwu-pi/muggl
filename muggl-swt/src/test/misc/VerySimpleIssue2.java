package test.misc;

public class VerySimpleIssue2 extends SuperClass {

	public static int fib(int n) {
	    int r = 1;
	    if (n > 1)
	        r = n * fib(n - 1);
	    return r;
	}
	
}