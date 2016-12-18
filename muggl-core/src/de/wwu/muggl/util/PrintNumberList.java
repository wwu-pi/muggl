package de.wwu.muggl.util;

import java.util.Arrays;

/**
 * "Continual example"
 * 
 * @author max
 *
 */
public class PrintNumberList {
	public static void main(String[] args) {
		// List<Integer> numbers = Arrays.asList(1, 2, 3);
		Arrays.asList(1, 2, 3).forEach(System.out::println);
	}
}
