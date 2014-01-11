package test.problematic;

/**
 * Fails in constant execution mode because objectref and arrayref are treated differently.
 */
public class ArrayClone {
	public void arrayClone() {
		int[] a = {1, 2};
		int[] b = a.clone();
		Object c = a.clone();
	}
	
	public void arrayClone2() {
		ArrayClone[][] e = new ArrayClone[5][5];
		ArrayClone[][] d = e.clone();
		
	}
	
/**
 * @see java.lang.Object#clone()
 */
@Override
protected Object clone() throws CloneNotSupportedException {
	return null;
}


	public static void main(String... args) {
		ArrayClone a = new ArrayClone();
		a.arrayClone2();
	}
}
