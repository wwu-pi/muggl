package test.problematic;

/*
 * Not enough test cases are generated. If using int instead of Integer, there are no problems.
 */

public class SimpleBlur {

	public static Integer[] blur(Integer[] image) {
		if (image == null) throw new IllegalArgumentException("Null is not possible!");

		Integer length = image.length;
		if (length == 0) throw new IllegalArgumentException("Can not blur empty image!");

		Integer n = length;
		while (n * n != length) {
			n /= 2;
			if (n == 1) {
				throw new IllegalArgumentException("Not quadratic!");
			}
		}

		if (n < 3) {
			throw new IllegalArgumentException("Sides are too  short!");
		}
		
		Integer[] image2 = new Integer[length];
		for (Integer a = 0; a < length; a++) {
			Integer neighbours = 0;
			long pixel = 0l;
			// Left
			if (a % n != 0) {
				neighbours++;
				pixel += image[a - 1];
			}

			// Upper
			if (a >= n) {
				neighbours++;
				pixel += image[a - n];
			}

			// Right
			if (a % n != n - 1) {
				neighbours++;
				pixel += image[a + 1];
			}

			// Lower
			if (a < length - n) {
				neighbours++;
				pixel += image[a + n];
			}

			// Calc
			pixel += (8 - neighbours) * image[a];
			pixel = ((pixel * 10 / 8l) + 5) / 10;
			image2[a] = (int) pixel ;
		}

		return image2;
	}
}
