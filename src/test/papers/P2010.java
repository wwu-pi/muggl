package test.papers;

/**
 *
 */
public class P2010 {
	
	/**
	 * Greatest common divisor.
	 *
	 * @param m
	 * @param n
	 * @return bla
	 */
    public static int gcd(int m, int n){
		if (m < 0) m *= -1;
		if (n < 0) m *= -1;
		
		if (m < n) {int t = m; m = n; n = t;}
		int r = m - n;
		if (r == 0) return n;
		return gcd(n, r);
   }
    
}
