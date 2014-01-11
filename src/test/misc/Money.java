package test.misc;

/**
 *
 */
public class Money {

	/**
	 * 
	 *
	 * @param d
	 * @param e
	 * @param m
	 * @param n
	 * @param o
	 * @param r
	 * @param s
	 * @param y
	 * @return n
	 */
	public static int getMoney(int d, int e, int m, int n, int o, int r, int s, int y) {
		if (s > 0 && m > 0) {
			if (d != e && d != m && d != n && d != o && d != r && d != s && d != y &&
						  e != m && e != n && e != o && e != r && e != s && e != y &&
						  		    m != n && m != o && m != r && m != s && m != y &&
						  		    		  n != o && n != r && n != s && n != y &&
						  		    		  		    o != r && o != s && o != y &&
						  		    		  		    		  r != s && r != y &&
						  		    		  		    		  			s != y
			
				) {
				if ((	         s * 1000 + e * 100 + n * 10 + d) + 
					(	         m * 1000 + o * 100 + r * 10 + e) ==
					(m * 10000 + o * 1000 + n * 100 + e * 10 + y)) {
					return s * 1000 + e * 100 + n * 10 + d + m * 1000 + o * 100 + r * 10 + e;
				}
			}
		}
		return Integer.MIN_VALUE;
	}
	
	/**
	 * 
	 *
	 * @param d
	 * @param e
	 * @param m
	 * @param n
	 * @param o
	 * @param r
	 * @param s
	 * @param y
	 * @return n
	 */
	public static int getMoney2(int d, int e, int m, int n, int o, int r, int s, int y) {
		if (d >= 0 && e >= 0 && m > 0 && n >= 0 && o >= 0 && r >= 0 && s > 0 && y >= 0) {
			if (d <= 9 && e <= 9 && m <= 9 && n <= 9 && o <= 9 && r <= 9 && s <= 9 && y <= 9) {
				if (d != e && d != m && d != n && d != o && d != r && d != s && d != y &&
						  e != m && e != n && e != o && e != r && e != s && e != y &&
						  		    m != n && m != o && m != r && m != s && m != y &&
						  		    		  n != o && n != r && n != s && n != y &&
						  		    		  		    o != r && o != s && o != y &&
						  		    		  		    		  r != s && r != y &&
						  		    		  		    		  			s != y
			
				) {
					if ((	         s * 1000 + e * 100 + n * 10 + d) + 
						(	         m * 1000 + o * 100 + r * 10 + e) ==
						(m * 10000 + o * 1000 + n * 100 + e * 10 + y)) {
						return s * 1000 + e * 100 + n * 10 + d + m * 1000 + o * 100 + r * 10 + e;
					}
				}
			}
		}
		return Integer.MIN_VALUE;
	}
	
	/**
	 * 
	 *
	 * @param d
	 * @param e
	 * @param m
	 * @param n
	 * @param o
	 * @param r
	 * @param s
	 * @param y
	 * @param carry1
	 * @param carry2
	 * @param carry3
	 * @param carry4
	 * @return f
	 */
	public static boolean getMoney3(int d, int e, int m, int n, int o, int r, int s, int y, int carry1, int carry2, int carry3, int carry4) {
		if (carry1 >= 0 && carry1 <= 1 && carry2 >= 0 && carry2 <= 1 && carry3 >= 0 && carry3 <= 1 && carry4 >= 0 && carry4 <= 1) {
			if (d >= 0 && e >= 0 && m > 0 && n >= 0 && o >= 0 && r >= 0 && s > 0 && y >= 0) {
				if (d <= 9 && e <= 9 && m <= 9 && n <= 9 && o <= 9 && r <= 9 && s <= 9 && y <= 9) {
					if (d != e && d != m && d != n && d != o && d != r && d != s && d != y &&
							  e != m && e != n && e != o && e != r && e != s && e != y &&
							  		    m != n && m != o && m != r && m != s && m != y &&
							  		    		  n != o && n != r && n != s && n != y &&
							  		    		  		    o != r && o != s && o != y &&
							  		    		  		    		  r != s && r != y &&
							  		    		  		    		  			s != y
				
					) {
						int send = s * 1000 + e * 100 + n * 10 + d;
						int more = m * 1000 + o * 100 + r * 10 + e;
						int money = m * 10000 + o * 1000 + n * 100 + e * 10 + y;

						if (send > 0 && send <= 9999) {
							if (more > 0 && more <= 9999) {
								if (money > 0 && money <= 99999) {
									if (y == d + e - carry1) {
										if (e == n + r - carry2 + carry1) {
											if (n == e + o - carry3 + carry2) {
												if (o == s + m - carry4 + carry3) {
													if (m == carry4) {
														return send + more == money;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
}
