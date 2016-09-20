package de.wwu.muggl.binaryTestSuite.testVMInit;

import java.util.Enumeration;
import java.util.Properties;

public class TestInitializeSystemClass {

	public static void main(String[] args) {
		System.out.println(tryGetProperty());
		System.out.println(listSystemProperties());
		System.out.println(isbooted());
	}

	/**
	 * If you are not allowed to compile this in eclipse due to access restrictions, go to the properties of your Java
	 * project, i.e. by selecting "Properties" from the context menu of the project in the "Package Explorer". There, go
	 * to "Java Build Path", tab "Libraries". There, expand the library entry, select "Access rules", "Edit..." and
	 * "Add..." a "Resolution: Accessible" with a corresponding rule pattern. "**" should do the job.
	 * 
	 * @return if not configured, null
	 */

	public static String tryGetProperty() {
		return sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
	}

	/**
	 * Should be the check if the InitializeSystemClasses has been executed successfully.
	 * 
	 * @return
	 */
	public static boolean isbooted() {
		System.out.println(sun.misc.VM.isBooted());
		return sun.misc.VM.isBooted();
	}

	public static boolean testReturnValues() {
		return true;
	}

	public static int listSystemProperties() {
		Properties p = System.getProperties();
		int i = 0;
		if (p != null) {
			System.out.println("size: " + p.size());
			
			Enumeration<Object> keys = p.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = (String) p.get(key);
				System.out.println(i + ") " + key + ": " + value);
				i++;
			}
		}
		return i;
	}
}
