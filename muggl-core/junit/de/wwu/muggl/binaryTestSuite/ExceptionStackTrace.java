package de.wwu.muggl.binaryTestSuite;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionStackTrace {

	public final static String METHOD_testExceptionStackTrace = "testExceptionStackTrace";

	public static String testExceptionStackTrace() {
		try {
			throwError();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString(); // stack trace as a string
		}
		return "";
	}

	static void throwError() throws IllegalArgumentException {
		throw new IllegalArgumentException("no arguments at all...");
	}

	public static void main(String[] args) {
		System.out.println(testExceptionStackTrace());
	}
}
