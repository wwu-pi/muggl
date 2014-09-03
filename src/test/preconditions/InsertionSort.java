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

	public boolean insertOrderedPostcondition(int[] array, int n) {
		return isOrdered(array, n+1);
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
	 */
	public void insertOrdered(int[] array, Integer n) {
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

	/*
	public int insertionSort_check_precondition(int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (!insertOrderedPrecondition(array, i)) {
				return 1;
			}
			insertOrdered(array, i);
			if (!insertOrderedPostcondition(array, i)) {
				return -1;
			}
		}
		return 0;
	}*/
	
	public int simpleArraySwap(int[] array) {
		// Swap the first two elements
		int tmp = array[0];
		array[0] = array[1];
		array[1] = tmp;
		
		if (array[0] > array[1]) return 1;
		return 0;
	}
}
