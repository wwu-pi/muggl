package de.wwu.muggl.binaryTestSuite;

public class GetCurrentThread {
	public final static String METHOD_getThreadGroup = "getThreadGroup";

	public static String getThreadGroup() {
		return Thread.currentThread().getThreadGroup().getName();
	}

	public final static String METHOD_getThread = "getThread";

	public static String getThread() {
		
		return Thread.currentThread().getName();
	}

	public static void main(String[] args) {
		System.out.println(getThread());
		System.out.println(getThreadGroup());
	}
}
