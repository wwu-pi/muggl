package de.wwu.muggl.binaryTestSuite;

public class CatchCorrectExceptions {
	public static char[] input = { 'v', 'e', 'r', 'y', 'n', 'i', 'c', 'e' };

	public final static String METHOD_ArrayOutOfBound = "catchArrayOutOfBound";

	/**
	 * tests for behaviour needed in sun.reflect.generics.parser.SignatureParser.next
	 * caload instr. had the wrong exception
	 * @return Character ':'
	 */
	public static char catchArrayOutOfBound() {
		try {
			return input[8];
		} catch (ArrayIndexOutOfBoundsException e) {
			return ':';
		}

	}

	public static void main(String[] args) {
		System.out.print(catchArrayOutOfBound());
	}
}
