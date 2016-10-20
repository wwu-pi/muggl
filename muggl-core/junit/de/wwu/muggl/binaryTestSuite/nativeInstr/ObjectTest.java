package de.wwu.muggl.binaryTestSuite.nativeInstr;

/**
 * Test native functions on Objects
 * 
 * @author Max Schulze
 *
 */
public class ObjectTest {

	public static final String METHOD_ObjectHashCodeStable = "test_ObjectHashCodeStable";
	
	public static boolean test_ObjectHashCodeStable() {
		Object daddel = new Integer[] { 65123, 2 };
		int hash1 = daddel.hashCode();
		int hash2 = daddel.hashCode();
		return Integer.compare(hash1, hash2) == 0;
	}

	public static void main(String[] args) {
		System.out.print(test_ObjectHashCodeStable());
	}

}
