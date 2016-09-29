package de.wwu.muggl.binaryTestSuite;

import de.wwu.muggl.vm.Application;

public class GetCurrentThread {
	public final static String METHOD_getThreadGroup = "getThreadGroup";

	public static String getThreadGroup() {
		return Application.currentThread().getThreadGroup().getName();
	}

	public final static String METHOD_getThread = "getThread";

	public static String getThread() {
		return Application.currentThread().getName();
	}

	public static void main(String[] args) {
		System.out.println(getThread());
		System.out.println(getThreadGroup());
	}
}
