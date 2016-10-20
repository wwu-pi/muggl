package de.wwu.muggl.binaryTestSuite;

/**
 * Test String functions
 * 
 * @author Max Schulze
 *
 */
public class StringHandling {

	public static String METHOD_StringReferenceEquality = "StringReferenceEquality";

	public static boolean StringReferenceEquality() {
		return "testing" == "testing";
	}

	public static String METHOD_StringEquality = "StringEquality";

	public boolean StringEquality() {
		return "testing".equals("testing");
	}

	public static String METHOD_Substring = "Substring";

	// crashes on wrong implementation of System.Arraycopy
	public boolean Substring() {
		return "testing".substring(3, 5).equals("ti");
	}

	public static String METHOD_StartsWith = "StartsWith";

	public boolean StartsWith() {
		return "testing".startsWith("tes");
	}

}
