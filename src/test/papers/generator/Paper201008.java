package test.papers.generator;

/**
 *
 */
public class Paper201008 {

	/**
	 * Binary search.
	 * 
	 * @param v The element to search for.
	 * @param a The array to search the element in.
	 * @return The position of the element; or -1, if it is not contained.
	 */
	public static int binSearch(int v, int[] a) {
		int low = 0;
		int up = a.length - 1;
		while (low <= up) {
			int mid = (low + up) / 2;
			if (v < a[mid]) {
				up = mid - 1;
			} else if (v > a[mid]) {
				low = mid + 1;
			} else {
				return mid;
			}
		}
		return -1;
	}
	

    /**
     * Quicksort
     *
     * @param data
     * @return a
     */
    public static int[] quicksort(int[] data) {	
    	quicksort(data, 0, data.length - 1);
    	return data;
	}

    /**
     * Quicksort implementation.
     *
     * @param data
     * @param left
     * @param right
     */
	private static void quicksort(int[] data, int left, int right) {
		int leftSplit, rightSplit, pivot, temp;
		
		leftSplit = left;
		rightSplit = right - 1;
		pivot = data[right];
		
		do {
			while (data[leftSplit] <= pivot && leftSplit < right)
			{
				leftSplit++;
			}
			while (pivot <= data[rightSplit] && rightSplit > left)
			{
				rightSplit--;
			}
			if (leftSplit < rightSplit) {
				temp = data[leftSplit];
				data[leftSplit] = data[rightSplit];
				data[rightSplit] = temp;
			}
		} while (leftSplit < rightSplit);
		
		if (data[leftSplit]  > pivot) {
			temp = data[leftSplit];
			data[leftSplit] = data[right];
			data[right] = temp;
		}
		
		if (left < rightSplit) quicksort(data, left, rightSplit);
		if (leftSplit + 1 < right) quicksort(data, leftSplit + 1, right);
	}
	
	/**
	 * 
	 *
	 * @param sortMe
	 * @return a
	 */
	public static int[] swapsort(int[] sortMe) {
		int startwert = 0;

		while (startwert < sortMe.length - 1) {

			int kleinere = countSmallerOnes(sortMe, startwert);

			if (kleinere > 0) {
				int tmp = sortMe[startwert];
				sortMe[startwert] = sortMe[startwert + kleinere];
				sortMe[startwert + kleinere] = tmp;
			} else {
				startwert++;
			}
		}
		
		return sortMe;
	}

	private static int countSmallerOnes(final int[] countHere, final int index) {
		int counter = 0;
		for (int i = index + 1; i < countHere.length; i++) {
			if (countHere[index] > countHere[i]) {
				counter++;
			}
		}
		return counter;
	}

	
	/**
	 * Vector multiplication.
	 *
	 * @param v1
	 * @param v2
	 * @return a
	 */
	public static double dotProduct(double[] v1, double[] v2) {
		if (v1.length != v2.length) {
			throw new IllegalArgumentException();
		}
		
		double result = 0.0;
		for (int a = 0; a < v1.length; a++) {
			result += v1[a] * v2[a];
		}
		return result;
	}
	
	/**
	 * Histogram
	 *
	 * @param values
	 * @param classes
	 * @return a
	 */
	public static int[] histogram(int[] values, int classes) {
		int min = Integer.MAX_VALUE;
		int max = 0;
		for (int a = 0; a < values.length; a++) {
			if (values[a] < min)
				min = values[a];
			if (values[a] > max)
				max = values[a];
		}
		double width = (max - min + 1) / (double) classes;
		
		int[] histogram = new int[classes];
		for (int a = 0; a < values.length; a++) {
			histogram[(int) ((values[a] - min) / width)]++;
		}
		
		return histogram;
	}
	
	/**
	 * 
	 *
	 * @param f
	 * @return a
	 */
	public static double[] fft(double[] f) {
		int n = f.length;
		if (n == 0)
			throw new IllegalArgumentException();
		if (n == 1)
			return f;
		if (n % 2 != 0)
			throw new IllegalArgumentException();
		
		double[] g = new double[n / 2];
		double[] u = new double[n / 2];
		for (int a = 0; a < n; a++) {
			if (a % 2 == 0) {
				g[a / 2] = f[a];
			} else {
				u[a / 2] = f[a];
			}
		}
		g = fft(g);
		u = fft(u);
		
		double[] c = new double[n];
		int nhalf = n / 2;
		for (int a = 0; a < nhalf; a++) {
			double mult = Math.pow(Math.E, -2 * Math.PI * a / n);
			c[a] = g[a] + u[a] * mult;
			c[a + nhalf] = g[a] - u[a] * mult;
		}
		
		return c;
	}
	
	/**
	 * 
	 *
	 * @param unsorted
	 * @return a
	 */
	public static int[] heapSort(int unsorted[]) {
		int end = unsorted.length;
		createHeap(unsorted);
		for (int i = 1; i < unsorted.length; i++) {
			swap(0, end - 1, unsorted);
			seep(0, unsorted, end - 1);
			end--;
		}
		return unsorted;
	}

	private static void createHeap(int a[]) {
		int border = 0;
		if ((a.length % 2) == 1)
			border = (a.length - 1) / 2;
		else
			border = a.length / 2;
		for (int i = border - 1; i >= 0; i--) {
			seep(i, a);
		}
	}

	private static void seep(int index, int a[]) {
		seep(index, a, a.length);
	}

	private static void seep(int index, int a[], int end) {
		int border = 0;
		if ((end % 2) == 1)
			border = (end - 1) / 2;
		else
			border = end / 2;
		if (index <= border && end > 1) {
			int pointto = 0;
			pointto = 2 * (index + 1);
			if (pointto < end) {
				if (!((a[index] > a[pointto - 1]) && (a[index] > a[pointto]))) {
					if (a[pointto] > a[pointto - 1]) {
						swap(index, pointto, a);
						if (pointto <= (border - 1)) seep(pointto, a, end);
					} else {
						swap(index, pointto - 1, a);
						if ((pointto - 1) <= (border - 1)) seep(pointto - 1, a, end);
					}
				}
			} else {
				if (a[index] < a[pointto - 1]) swap(index, pointto - 1, a);
			}
		}
	}

	private static void swap(int indexA, int indexB, int a[]) {
		int temp = a[indexA];
		a[indexA] = a[indexB];
		a[indexB] = temp;
	}

	
	/**
	 * 
	 *
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String... args) {
		int[] a = {2, 5, 2, 6};	
		int b = binSearch(5, a);
		b = binSearch(4, a);
		
		swapsort(a);
		
		double[] c = {5.5, 4.4, 3.3};
		double[] d = {2.2, 5.0, 1.0};
		double e = dotProduct(c, d);
		
		int[] f = {2, 5, 13};
		int[] g = histogram(f, 1);
		g = histogram(f, 2);
		g = histogram(f, 3);
		g = histogram(f, 4);
		
		int[] h = {2, 5, 13, 4, 3, 5, 3, 74, 25, 2, 60, 4, 3, 5, 3, 36, 36, 23, 64, 23, 45, 23, 16, 27};
		g = histogram(h, 2);
		g = histogram(h, 5);
		
		double[] i = {1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8};
		i = fft(i);
	
		double[] j = {1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1};
		j = fft(j);
	}
	
	
}
