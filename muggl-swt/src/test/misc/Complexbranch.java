package test.misc;

/**
 *
 */
public class Complexbranch {
	
	/**
	 * 
	 *
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param e
	 * @param f
	 * @return 2, 1, 0
	 */
	public static int main(int a, int b, int c, int d, int e, int f) {
		char z;
		if (a < e || e + a != c + b || e + c < a + b + d) {
			a++;
			switch (a + b)
			{
				case 0:
					return 2;
				case 10:
					a = b - e;
					break;
				case 23:
					e = a + b + d;
					break;
				default:
					c = a;
					a = b;
			}
			c--;
		} else {
			b++;
			if (c < b || (a > b && e < d) || a == e) c++;
		}
		
		a = b + c;
		z = 0;

		while (f < d && z < 5) {
			z++;
			e = c + e + b;
			f -= 2 * a + e + 1;
			if (e < f) break;
			a = d + a;
			if (f == a) break;
		}
		if ((a < b || b < c) && (a < c || e < d)) return 1;

		a = b + c;
		z = 0;
		while (f + e < a + b && z < 5) {
			z++;
			e = c + e + b;
			f -= 2 * a + e;
			if (e < f) break;
			a = d + a;
			if (f == a) break;
		}
		return 0;
	}
}
