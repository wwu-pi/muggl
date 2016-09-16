package de.wwu.muggl.binaryTestSuite.invokevirtual;

import java.util.Hashtable;

public class HashTableEmpty {

	public static void main(String[] args) {
		execute();
	}

	public static void execute() {

		Hashtable<String, Integer> numbers = new Hashtable<String, Integer>();
		if (numbers.isEmpty())
			System.out.println("HashTable isEmpty");
	}

}
