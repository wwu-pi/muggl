package de.wwu.muggl.binaryTestSuite;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import sun.reflect.Reflection;

/**
 * Debug HashMap inconsistencies
 * 
 * @author Max Schulze
 *
 */
@SuppressWarnings("restriction")
public class HashMapTest {

	public static String METHOD_test_HashMap = "test_HashMap";

	public static String test_HashMap() {
		Map<String, String> getraenke = new HashMap<String, String>();
		getraenke.put("Sekt", "Freixenet");
		getraenke.put("daddel", "duduu");
		return getraenke.get("Sekt");
	}

	public static String METHOD_test_HashMapConcurrent = "test_HashMapConcurrent";

	public static String test_HashMapConcurrent() {
		Map<String, String> getraenke = new ConcurrentHashMap<String, String>();
		assert ("daddel".hashCode() == "daddel".hashCode());
		getraenke.put("Sekt", "Freixenet");
		return getraenke.get("Sekt");
	}

	public static String METHOD_test_HashMapComplicated = "test_HashMapComplicated";

	/**
	 * Returning a .class object should be stable, i.e. always the same .class object for the same objectref
	 * 
	 * Code example from sun.reflect.Reflection constructor
	 * 
	 * @return
	 */
	public static String test_HashMapComplicated() {
		Map<Class<?>, String[]> map = new HashMap<Class<?>, String[]>();
		map.put(Reflection.class, new String[] { "fieldFilterMap", "methodFilterMap" });

		return map.get(Reflection.class)[0];
	}

	public static String METHOD_test_HashMapInHashMap = "test_HashMapInHashMap";

	public static String test_HashMapInHashMap() {
		Map<Class<?>, String[]> map = new HashMap<Class<?>, String[]>();
		map.put(Reflection.class, new String[] { "fieldFilterMap", "methodFilterMap" });

		Set<Entry<Class<?>, String[]>> test = map.entrySet();
		int i = 0;
		for (@SuppressWarnings("unused")
		Entry<Class<?>, String[]> entry : test) {
			i++;
		}
		return test.size() + " " + i;
		// Map<Class<?>, String[]> map2 = new HashMap<Class<?>, String[]>(map);
		// map2.put(Object.class, new String[] { "fieldFilterMap1"});
		//
		// return map2.get(Object.class)[0];
	}

	public static void main(String[] args) {
		System.out.println(test_HashMap());
		System.out.println(test_HashMapComplicated());
		System.out.println(test_HashMapInHashMap());
	}
}
