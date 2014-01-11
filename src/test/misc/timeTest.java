package test.misc;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressWarnings("all")
public class timeTest {

	public static long oddFactorialX(long x) {
		// Leave the recursion.
		if (x <= 1) return 1;
		// Do some silly, time consuming stuff
		int c = 0;
		int e = 0;
		for (int b = 0; b < x; b++)
		{
			c += b / 5;
			for (int d = 0; d < c; d++)
			{
				e += d / 2;
			}
		}
		
		// / or *.
		if (x % 2 == 1) return x / oddFactorialX(x - 1);
		return x * oddFactorialX(x - 1);
	}
	
	public static void countTheTimeForFactorialX(int x) {
		Calendar cal = new GregorianCalendar();
		long start = cal.getTimeInMillis();
		long a = oddFactorialX(x);
		System.out.println(a);
		cal = new GregorianCalendar();
		long end = cal.getTimeInMillis();
		System.out.println("Duration: " + (end - start));
	}
	
	public static void main(String[] args) {
		countTheTimeForFactorialX(500);
	}


	
}
