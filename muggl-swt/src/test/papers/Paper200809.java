package test.papers;

@SuppressWarnings("all")
public class Paper200809 {

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
	 * Greatest common divisor.
	 *
	 * @param m
	 * @param n
	 * @return
	 */
    public static int gcd(int m, int n){
		if (m < 0) throw new IllegalArgumentException("No natural number.");
		if (n < 0) throw new IllegalArgumentException("No natural number.");
		
		if (m < n) {int t = m; m = n; n = t;}
		int r = m - n;
		if (r == 0) return n;
		return gcd(n, r);
   }
    
	/**
	 * Greatest common divisor.
	 *
	 * @param m
	 * @param n
	 * @return
	 */
    public static int gcd2(int m, int n){
		while(n != m) {
			if (n > m) {
				n -= m;
			} else {
				m -= n;
			}
		}
		return n;
   }
    
    /**
     * Fibonacci numbers.
     *
     * @param x
     * @return The fibonacci number of x.
     */
    public long fibonacci(long x) {
    	if (x == 0) return 0L;
    	if (x == 1) return 1L;
    	return fibonacci(x - 1) + fibonacci(x - 2);
    }    
    
    /**
     * Mergesort
     *
     * @param data
     */
    public static void mergesort(int[] data) {
    	mergesort(data, 0, data.length - 1);
    }
    
    /**
     * Mergesort implementation
     *
     * @param data
     * @param left
     * @param right
     */
    private static void mergesort(int[] data, int left, int right) {
    	// Remaining array is empty or has one element.
    	if (left >= right) return;
    	
    	// Determine the middle and sort the two arrays.
    	int middle = (left + right) / 2;
    	mergesort(data, left, middle);
    	mergesort(data, middle + 1, right);
    	
    	// Merge the two arrays. 
    	merge(data, left, middle, right);
    }
    
    /**
     * Mergesort merge method.
     *
     * @param data
     * @param left
     * @param middle
     * @param right
     */
    private static void merge(int[] data, int left, int middle, int right) {
    	// No need to continue if the right side is empty.
    	if (middle + 1 > right) return;
    	
    	// Temporary array.
    	int[] dataTemp = new int[data.length];
    	
    	// Copy the left side into the temporary array.
    	for (int a = left; a <= middle; a++)
    	{
    		dataTemp[a] = data[a];
    	}
    	
    	// Copy the right side in reverse order. This makes the further processing easier.
    	for (int a = middle + 1; a <= right; a++)
    	{
    		dataTemp[a] = data[right + middle + 1 - a];
    	}
    	
    	// Merge back to the main array.
    	int fromLeft = left;
    	int fromRight = right;
    	for (int a = left; a <= right; a++)
    	{
    		if (dataTemp[fromLeft] < dataTemp[fromRight]) {
    			data[a] = dataTemp[fromLeft];
    			fromLeft++;
    		} else {
    			data[a] = dataTemp[fromRight];
    			fromRight--;
    		}
    	}
    }
    
    /**
     * Quicksort
     *
     * @param data
     * @return
     */
    public int[] quicksort(int[] data) {	
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
	private void quicksort(int[] data, int left, int right) {
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
	
	public static void main(String args[]) {
		Paper200809 p = new Paper200809();
		System.out.println(p.squareRootByNewton(Double.NEGATIVE_INFINITY));
	}
	
	/**
	 * Babylonian method!
	 *
	 * @param x
	 * @return
	 * @throws IllegalArgumentException
	 */
	public double squareRootByNewton(double x) throws IllegalArgumentException {
		if (x != x) throw new IllegalArgumentException(); // NaN
		if (x > Double.MAX_VALUE) throw new IllegalArgumentException();
		if (x < 0.0) throw new IllegalArgumentException();
		
		final int maxIterations = 10;
		double currentGuess = 1;
		
		for (int a = 0; a < maxIterations; a++)
		{
			double currentGuessNew = 0.5 * (currentGuess + x / currentGuess);
			//System.out.println(a + "\t" + currentGuess);
			if (currentGuessNew == currentGuess) break;
			currentGuess = currentGuessNew;
		}
		
		return currentGuess;
	}
	
}
