package de.wwu.muggl.binaryTestSuite.invokestatic;

import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

/**
 * Get the Enum values WITHOUT REFLECTION, but the internal SharedSecrets
 * "Magic"... -.-
 * 
 * @author max
 *
 */
public enum MySharedSecrets {
	THINGY, BOB;

	public static void getValues() {
		System.out
				.println(SharedSecrets.getJavaLangAccess().getEnumConstantsShared(MySharedSecrets.class)[0].toString());
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
