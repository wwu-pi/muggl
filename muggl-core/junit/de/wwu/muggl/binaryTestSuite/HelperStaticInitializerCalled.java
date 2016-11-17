package de.wwu.muggl.binaryTestSuite;

public class HelperStaticInitializerCalled {
	public static boolean testing = false;
	public boolean testing2 = false;
	static {
		InitializerOrder.calledHelperClassStatic = true;
		System.out.println("called static initializer");
	}

	public HelperStaticInitializerCalled() {
		InitializerOrder.calledHelperClassConstructor = true;
	}

	public HelperStaticInitializerCalled(boolean in) {
		// overloaded for doing some checks.
		assert InitializerOrder.calledHelperClassStatic;
		InitializerOrder.calledHelperClassConstructor = true;
	}
}
