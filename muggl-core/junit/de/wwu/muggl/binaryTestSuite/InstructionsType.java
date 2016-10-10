package de.wwu.muggl.binaryTestSuite;

/**
 * This class shall test the JVM spec Table 2.11.1-A "Type support in the Java Virtual Machine instruction set".
 * 
 * This is not an attempt to full coverage, but at least some spot checks
 * 
 * @author Max Schulze
 *
 */
public class InstructionsType {

	public final static String METHOD_byte_imul = "byte_imul";

	// store, load, ireturn, i2b on byte, imul
	public static byte byte_imul() {
		byte test1 = 3;
		byte test2 = 2;
		return (byte) (test1 * test2);
	}

	public final static String METHOD_char_iinc = "char_iinc";

	public static char char_iinc() {
		// increases the char from '0' to '1' (see ASCII table)
		char test1 = 48;
		test1++;
		return test1;
	}

	public final static String METHOD_short_ineg = "short_ineg";

	// ineg on short
	public static short short_ineg() {
		short test1 = 48;
		return (short) (test1 * -1);
	}

	public final static String METHOD_bytecompifeq = "bytecompifeq";

	// ifne on byte (via integer)
	public static boolean bytecompifeq() {
		byte byte1 = 6;
		return (byte1 == 0) ? true : false;
	}

	public final static String METHOD_byteStoreField = "byteStoreField";
	private static byte testing;

	// test storing int (internal representation) in byte field
	public static boolean byteStoreField() {
		testing = 1;
		return (testing == 0) ? true : false;
	}

	public final static String METHOD_booleanInternalInt = "booleanInternalInt";

	// test if boolean type information gets lost after ireturn, iloads, and auto-boxing
	public static boolean booleanInternalInt() {
		Boolean mBoolean = false;
		return booleanTestingFunction(booleanReturnFunction(mBoolean));
	}

	private static boolean booleanTestingFunction(boolean booleanReturnFunction) {
		return !booleanReturnFunction;
	}

	private static boolean booleanReturnFunction(Boolean mBoolean) {
		return !mBoolean;
	}

	public static void main(String[] args) {
		System.out.println(byte_imul());
		System.out.println(char_iinc());
		System.out.println(short_ineg());
	}
}
