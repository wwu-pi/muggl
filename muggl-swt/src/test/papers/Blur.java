package test.papers;

/**
 *
 */
public class Blur {

	public static int[] blur(int[] image) {
		int length = image.length;
		int n = length;
		
		while (n * n != length) {
			n /= 2;
			if (n == 1) {
				throw new IllegalArgumentException("Not quadratic!");
			}
		}
		
		if (n < 3){
			throw new IllegalArgumentException("Sides are too short!");
		}
		
		/*
		boolean different = false;
		if (length > 0) {
			int value = image[0];
			for (int a = 1; a < length; a++) {
				if (value != image[a] / 10){
					different = true;
					break;
				}
			}
		}
		if (!different) throw new IllegalArgumentException("argl");
		*/
		
		int[] image2 = new int[length];
		for (int a = 0; a < length; a++) {
			int neighbours = 0;
			int pixel = 0;
			// Left
			if (a % n != 0) {
				neighbours++;
				pixel += image[a - 1] / 8;
			}
			
			// Upper
			if (a >= n) {
				neighbours++;
				pixel += image[a - n] / 8;
			}
			
			// Right
			if (a % n != n - 1) {
				neighbours++;
				pixel += image[a + 1] / 8;
			}
			
			// Lower
			if (a < length - n) {
				neighbours++;
				pixel += image[a + n] / 8;
			}
			
			// Calc
			pixel += (8 - neighbours) * image[a] / 8;
			image2[a] = pixel;
		}
	
		return image2;
	}
	
	public static void main(String... args) {
		int[] image = {0, 0, 0, 0, 12, 10, 12, 5, 0, 3, 0, 7, 0, 6, 0, 0};
		int[] image2 = blur(image);
		int[] image3 = SimpleBlur.blur(image);
		
	}
//		int[] image = {0, 0, 0, 0, 120, 120, 120, 120, 0, 0, 0, 0, 0, 0, 0, 0};
//		int[] image2 = {120, 120, 120, 0, 120, 120, 0, 0, 120, 0, 0, 0, 0, 0, 0, 120};
//		int[] image3 = {120, 120, 120, 0, 120, 120, 0, 0, 120, 0, 0, 0, 0, 0, 0};
//		int[] image4 = {120, 120, 120};
//		int[] image5 = {120, 120};
//		image = blur(image);
//		image2 = blur(image2);
//		try {
//			blur(image3);
//		} catch (IllegalArgumentException e) {
//			
//		}
//		try {
//			blur(image4);
//		} catch (IllegalArgumentException e) {
//			
//		}
//		try {
//			blur(image5);
//		} catch (IllegalArgumentException e) {
//			
//		}
//	}
	
}
