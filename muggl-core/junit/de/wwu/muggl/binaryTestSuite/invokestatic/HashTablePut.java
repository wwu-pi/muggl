package de.wwu.muggl.binaryTestSuite.invokestatic;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import sun.reflect.Reflection;

@SuppressWarnings("restriction")
public class HashTablePut {
	
	public static void main(String[] args) {
		executeHashMapPut();
		executeHashTablePut();
		executeEnumMap();
		executeHashMapReflection();
	}

	public static void executeHashTablePut() {

		Hashtable<String, Integer> numbers = new Hashtable<String, Integer>();
		numbers.put("one", 1);
	}

	public static void executeHashMapPut() {
		HashMap<String, Integer> number2 = new HashMap<>();
		number2.put("two", 2);
	}

	public enum STATE {
		NEW, RUNNING, WAITING, FINISHED;
	}

	public static void executeEnumMap() {

		// Java EnumMap Example 1: creating EnumMap in java with key as enum
		// type STATE
		EnumMap<STATE, String> stateMap = new EnumMap<STATE, String>(STATE.class);

		// Java EnumMap Example 2:
		// putting values inside EnumMap in Java
		// we are inserting Enum keys on different order than their natural
		// order
		stateMap.put(STATE.RUNNING, "Program is running");

		// Java EnumMap Example 3:
		// printing size of EnumMap in java
		System.out.println("Size of EnumMap in java: " + stateMap.size());

	}
		
	public static void executeHashMapReflection() {

        @SuppressWarnings("rawtypes")
		Map<Class,String[]> map = new HashMap<Class,String[]>();
        map.put(Reflection.class,
            new String[] {"fieldFilterMap", "methodFilterMap"});
        map.put(System.class, new String[] {"security"});
	}

}
