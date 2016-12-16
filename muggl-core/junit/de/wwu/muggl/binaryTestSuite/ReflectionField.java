package de.wwu.muggl.binaryTestSuite;

import java.lang.reflect.Field;

/**
 * "Simple"example for reflective Field access**
 * 
 * @author max
 *
 */
public class ReflectionField {

	public int maxtest = 3;
	public Object maxtest2 = Integer.valueOf(5);

	public static String METHOD_test_getFieldValueReflectiveIntPrim = "test_getFieldValueReflectiveIntPrim";

	@SuppressWarnings("null")
	public static int test_getFieldValueReflectiveIntPrim() {
		ReflectionField inst = new ReflectionField();
		try {
			Field f = inst.getClass().getField("maxtest");
			f.setAccessible(true);
			return (int) f.get(inst);

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return (Integer) null;
	}

	public static String METHOD_test_getFieldValueReflectiveObj = "test_getFieldValueReflectiveObj";

	public static Object test_getFieldValueReflectiveObj() {
		ReflectionField inst = new ReflectionField();
		try {
			Field f = inst.getClass().getField("maxtest2");
			f.setAccessible(true);
			return f.get(inst);

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(test_getFieldValueReflectiveIntPrim());
		System.out.println(test_getFieldValueReflectiveObj());
	}
}
