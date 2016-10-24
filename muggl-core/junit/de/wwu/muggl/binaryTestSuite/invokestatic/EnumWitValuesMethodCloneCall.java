package de.wwu.muggl.binaryTestSuite.invokestatic;

/**
 * Get a .value() function that uses .clone instead of arraycopy.
 * 
 * The eclipse-ecj does compile arraycopy isntructions, but javac does it with <enum>.clone()
 * 
 * @author max
 *
 */
public enum EnumWitValuesMethodCloneCall {
	THINGY, BOB;

	public static int iterateValues() {
		int i = 0;
		for (@SuppressWarnings("unused")
		EnumWitValuesMethodCloneCall it : EnumWitValuesMethodCloneCall.values()) {
			i++;
		}
		return i;
	}

	public static void main(String[] args) {
		System.out.println(iterateValues());
	}

}
