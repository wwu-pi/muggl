package test.preconditions;

public class InsertionSort {
	
	public boolean isOrdered(int[] array, int n) {
		for (int i = 0; i < n - 1; i++) {
			if (array[i] > array[i+1]) return false;
		}
		return true;
	}
	
	public boolean insertOrderedPrecondition(int[] array, int n) {
		return (n >= 0 && n < array.length && isOrdered(array, n));
	}

	
	/**
	 * Muggl throws a ClassCastException with this function. 
	 */
	public void sillyFunction(int[] array, int position) {
		int j = array[position];
	}
	
	/**
	 * ...but this one works fine.  
	 */
	public void sillyFunction(int[] array, Integer position) {
		int j = array[position];
	}
	
	/**
	 * Sorting by insertion. Iterative algorithm.
	 * 
	 * But it seems Muggl cannot handle this, because of the error shown
	 * above (sillyFunction)
	 */
	public void insertOrdered(int[] array, int n) {
		while (n > 0 && array[n] < array[n-1]) {
			int tmp = array[n-1];
			array[n-1] = array[n];
			array[n] = tmp;
			n = n - 1;
		}
	}
	
	public void insertionSort(int[] array) {
		for (int i = 0; i < array.length; i++) {
			insertOrdered(array, i);
		}
	}

}
