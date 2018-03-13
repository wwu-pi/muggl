package de.wwu.muggl.binaryTestSuite.testVMInit;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Properties;

public class TestInitializeSystemClass {

	public static void main(String[] args) {
		System.out.println(tryGetProperty());
		System.out.println(mandatoryProperties());
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

	@SuppressWarnings("restriction")
	public static String tryGetProperty() {
		return sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
	}

	/**
	 * Should be the check if the InitializeSystemClasses has been executed successfully.
	 * 
	 * @return
	 */
	@SuppressWarnings("restriction")
	public static boolean isbooted() {
		// System.out.println(sun.misc.VM.isBooted());
		return sun.misc.VM.isBooted();
	}

	public static boolean testReturnValues() {
		return true;
	}

	public static final String METHOD_MANDATORYPROPS = "mandatoryProperties";

	public static boolean mandatoryProperties() {
		int count = 0;
		// mandatory system properties to test, see java.lang.System:509
		String[] props = { "java.version", "java.vendor", "java.vendor.url", "java.home", "java.class.version",
				"java.class.path", "os.name", "os.arch", "os.version", "file.separator", "path.separator",
				"line.separator", "user.name", "user.home", "user.dir" };

		for (String string : props) {
			if (System.getProperty(string) == null) {
				System.out.println("fatal: mandatory property not found: " + string);
			} else
				count++;
		}
		return props.length == count;
	}

	public final static String METHOD_testGetSystemProperty = "testGetSystemProperty";

	public static int testGetSystemProperty() {
		final int[] values = { 1, 2, 3 };

		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				// TestProperty is not there, so should return null
				// but not fail, because system properties are wrongly initialized!
				return values[0] = Integer.getInteger("TestProperty", 99);
			}
		});

		return (int) values[0];
	}

	public final static String METHOD_testDoPrivileged = "testDoPrivileged";

	public static boolean testDoPrivileged() {
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				return true;
			}
		});

	}
}
