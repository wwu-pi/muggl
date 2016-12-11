package de.wwu.muggl.binaryTestSuite.invokestatic;

import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

/**
 * Get the Enum values WITHOUT REFLECTION, but the internal SharedSecrets "Magic"... -.-
 * 
 * @author max
 *
 */
@SuppressWarnings("restriction")
public enum MySharedSecrets {
	THINGY, BOB;
	static {
		System.out.println("<clinit> got called!");
	}

	public static int getValues() {
		MySharedSecrets[] test = SharedSecrets.getJavaLangAccess().getEnumConstantsShared(MySharedSecrets.class);
		return test.length;
	}

	public static int iterateValues() {
		int i = 0;
		for (@SuppressWarnings("unused")
		MySharedSecrets it : MySharedSecrets.values()) {
			i++;
		}
		return i;
	}

	// superClass needed by children of getEnumConstantsShared
	// at java.lang.Class.isEnum Line 12: Executing invokevirtual 0 73
	public static String getMySuperClass() {
		return MySharedSecrets.class.getSuperclass().getName();
	}

	public static String getMySuperSuperClass() {
		return MySharedSecrets.class.getSuperclass().getSuperclass().getName();
	}

	public static boolean getClassIsEnum() {
		return MySharedSecrets.class.isEnum();
	}

	public static String getObjectSuperclass() {
		try {
			return Object.class.getSuperclass().getName();
		} catch (NullPointerException e) {
			return "npe as expected";
		}
	}

	// you will likely get a security exception if you call this.
	// Does not matter in the test cases
	public static void unsafer() {
		@SuppressWarnings("unused")
		Unsafe unsafe = Unsafe.getUnsafe();
		System.out.println(Unsafe.ADDRESS_SIZE);
	}

	public static void main(String[] args) {
		MySharedSecrets.getValues();
		System.out.println(getMySuperClass());
		System.out.println(getMySuperSuperClass());
		System.out.println(getObjectSuperclass());

	}

}
