package de.wwu.muggl.binaryTestSuite.invokestatic;

import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

/**
 * Get the Enum values WITHOUT REFLECTION, but the internal SharedSecrets "Magic"... -.-
 * 
 * @author max
 *
 */
public enum MySharedSecrets {
	THINGY, BOB;

	public static void getValues() {
		MySharedSecrets[] test = SharedSecrets.getJavaLangAccess().getEnumConstantsShared(MySharedSecrets.class);

		System.out.println(test.length);
	}

	// superClass needed by children of getEnumConstantsShared
	// at  java.lang.Class.isEnum Line 12: Executing invokevirtual  0 73
	public static String getMySuperClass() {
		return MySharedSecrets.class.getSuperclass().getName();
	}
	
	// you will likely get a security exception if you call this.
	// Does not matter in the test cases
	public static void unsafer() {
		@SuppressWarnings("unused")
		Unsafe unsafe = Unsafe.getUnsafe();
		System.out.println(unsafe.ADDRESS_SIZE);
	}

	public static void main(String[] args) {
		MySharedSecrets.getValues();

	}

}
