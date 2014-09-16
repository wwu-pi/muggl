package test.preconditions;

public class SelectionSort {
	
	public boolean maximum_precondition(int[] elements, int n) {
		//return n > 0 && n <= elements.length;
		return false;
	}
	
	public boolean isMaximum(int[] elements, int n, int result) {
		for (int i = 0; i < n; i++) {
			if (elements[i] > elements[result]) {
				return false;
			}
		}
		return true;
	}
	
	public boolean maximum_postcondition(int[] elements, int n, int result) {
		//return 0 >= result && result < n && isMaximum(elements, n, result);
		return false;
	}
	
	public int maximum(int[] elements, int n) {
		int maxIndex = 0;
		for (int i = 1; i < n; i++) {
			if (elements[maxIndex] < elements[i]) {
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	public void selectionSort(int[] elements) {
		for (int j = elements.length; j > 0; j--) {
			int maxIndex = maximum(elements, j);
			int tmp = elements[j-1];
			elements[j-1] = elements[maxIndex];
			elements[maxIndex] = tmp;
		}
	}
	
	public int selectionSort_check_precondition(int[] elements) {
		for (Integer j = elements.length; j > 0; j--) {
			if (!maximum_precondition(elements, j)) return 1;
			int maxIndex = maximum(elements, j);
			if (!maximum_postcondition(elements, j, maxIndex)) return -1;
			int tmp = elements[j-1];
			elements[j-1] = elements[maxIndex];
			elements[maxIndex] = tmp;
		}		
		return 0;
	}
	
	public static void main(String[] args) {
		int[] myarray = new int[] {10,5,3,7,1};
		System.out.println(new SelectionSort().selectionSort_check_precondition(myarray));
	}

}
