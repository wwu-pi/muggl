package de.wwu.muggl.binaryTestSuite;

public class InitializerOrder {
	static {
		boolean assertsEnabled = false;
		assert assertsEnabled = true; // Intentional side effect!!!
		if (!assertsEnabled)
			throw new RuntimeException("Asserts must be enabled!!!");
	}

	public static boolean calledHelperClassStatic, calledHelperClassConstructor = false;

	public static String METHOD_testInitializations = "testInitializations";

	public static boolean testInitializations() {
		assert !calledHelperClassStatic;
		assert !calledHelperClassConstructor;

		@SuppressWarnings("unused")
		HelperStaticInitializerCalled test[];
		assert !calledHelperClassStatic;

		assert !HelperStaticInitializerCalled.testing;
		assert calledHelperClassStatic;

		HelperStaticInitializerCalled dudu = new HelperStaticInitializerCalled();
		assert !dudu.testing2;
		assert calledHelperClassConstructor;
		return true;
	}

	public static String METHOD_testInitializationsArrayNull = "testInitializationsArrayNull";

	public static boolean testInitializationsArrayNull() {
		HelperStaticInitializerCalled test[] = null;
		assert test == null;
		// assert test.getClass() != null;
		return true;
	}

	public static String METHOD_testInitializationsArray = "testInitializationsArray";

	public static boolean testInitializationsArray() {
		assert !calledHelperClassStatic;
		assert !calledHelperClassConstructor;

		HelperStaticInitializerCalled test[] = { new HelperStaticInitializerCalled(true) };
		// look at this problem in bytecode. there should be no class initialization on anewarray, only on the new
		// instruction! @see jvms §5.5
		assert calledHelperClassStatic;
		assert !test[0].testing2;
		assert calledHelperClassConstructor;
		return true;
	}

	public static void main(String[] args) {
		// testInitializations();
		// testInitializationsArray();
		testInitializationsArrayNull();
		// run either test,not both because variables will be contaminated
	}
}
