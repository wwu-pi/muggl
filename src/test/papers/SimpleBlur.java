package test.papers;

/**
 *
 */
public class SimpleBlur {

	public static int[] blur(int[] image) {
		if (image == null) throw new IllegalArgumentException("Null is not possible!");

		int length = image.length;
		if (length == 0) throw new IllegalArgumentException("Can not blur empty image!");

		int n = length;
		while (n * n != length) {
			n /= 2;
			if (n == 1) {
				throw new IllegalArgumentException("Not quadratic!");
			}
		}

		if (n < 3) {
			throw new IllegalArgumentException("Sides are too  short!");
		}
		
		int[] image2 = new int[length];
		for (int a = 0; a < length; a++) {
			int neighbours = 0;
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
