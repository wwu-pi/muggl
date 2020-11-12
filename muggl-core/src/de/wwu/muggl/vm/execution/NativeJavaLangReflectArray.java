package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

public class NativeJavaLangReflectArray extends NativeMethodProvider {
	public static String pkg = "java.lang.reflect.Array";

	/*
	 * Getting
	 */

	public static Object get(Frame frame, Arrayref arr, Integer idx) {
		return arr.getElement(idx);
	}

	public static boolean getBoolean(Frame frame, Arrayref arr, Integer idx) {
		return (boolean) arr.getElement(idx);
	}

	public static byte getByte(Frame frame, Arrayref arr, Integer idx) {
		return (byte) arr.getElement(idx);
	}

	public static char getChar(Frame frame, Arrayref arr, Integer idx) {
		return (char) arr.getElement(idx);
	}

	public static double getDouble(Frame frame, Arrayref arr, Integer idx) {
		return (double) arr.getElement(idx);
	}

	public static float getFloat(Frame frame, Arrayref arr, Integer idx) {
		return (float) arr.getElement(idx);
	}

	public static int getInt(Frame frame, Arrayref arr, Integer idx) {
		return (int) arr.getElement(idx);
	}

	public static long getLong(Frame frame, Arrayref arr, Integer idx) {
		return (long) arr.getElement(idx);
	}

	public static short getShort(Frame frame, Arrayref arr, Integer idx) {
		return (short) arr.getElement(idx);
	}

	public static int getLength(Frame frame, Arrayref arr) {
		return arr.getLength();
	}

	/*
	 * Setting
	 */
	public static void set(Frame frame, Arrayref arr, Integer idx, Objectref obj) {
		arr.putElement(idx, obj);
	}

	public static void setBoolean(Frame frame, Arrayref arr, Integer idx, Integer obj) {
		arr.putElement(idx, obj);
	}

	public static void setByte(Frame frame, Arrayref arr, Integer idx, Integer obj) {
		arr.putElement(idx, obj);
	}

	public static void setChar(Frame frame, Arrayref arr, Integer idx, Integer obj) {
		arr.putElement(idx, obj);
	}

	public static void setDouble(Frame frame, Arrayref arr, Integer idx, Double obj) {
		arr.putElement(idx, obj);
	}

	public static void setFloat(Frame frame, Arrayref arr, Integer idx, Float obj) {
		arr.putElement(idx, obj);
	}

	public static void setInt(Frame frame, Arrayref arr, Integer idx, Integer obj) {
		arr.putElement(idx, obj);
	}

	public static void setLong(Frame frame, Arrayref arr, Integer idx, Long obj) {
		arr.putElement(idx, obj);
	}

	public static void setShort(Frame frame, Arrayref arr, Integer idx, Integer obj) {
		arr.putElement(idx, obj);
	}

	/*
	 * New
	 */
	public static Arrayref newArray(Frame frame, Objectref compType, Integer dim) {

		ReferenceValue ref = null;
		// find out whether the compType needs a primitive wrapper
		if (compType.asClass().isPrimitive()) {
			// name field should be set, then
			String name = "";
			try {
				name = frame.getVm().getStringCache().getStringFieldValue(compType, "name");
				BasicType t = VmSymbols.primitiveName2BasicType(name);
				ref = frame.getVm().getClassLoader().getClassAsClassFile(VmSymbols.basicType2JavaClassName(t))
						.getAPrimitiveWrapperObjectref(frame.getVm());
			} catch (ExecutionException | ClassFileException e) {
				e.printStackTrace();
			}
		} else {
			ref = compType.getMirrorMuggl().getInitializedClass().getANewInstance();
		}
		return new Arrayref(ref, dim);
	}

	public static Arrayref multiNewArray(Frame frame, Objectref compType, Arrayref dim) {

		ReferenceValue ref = null;
		// find out whether the compType needs a primitive wrapper
		if (compType.asClass().isPrimitive()) {
			// name field should be set, then
			String name = "";
			try {
				name = frame.getVm().getStringCache().getStringFieldValue(compType, "name");
				BasicType t = VmSymbols.primitiveName2BasicType(name);
				ref = frame.getVm().getClassLoader().getClassAsClassFile(VmSymbols.basicType2JavaClassName(t))
						.getAPrimitiveWrapperObjectref(frame.getVm());
			} catch (ExecutionException | ClassFileException e) {
				e.printStackTrace();
			}
		} else {
			ref = compType.getMirrorMuggl().getInitializedClass().getANewInstance();
		}

		return new Arrayref(ref, dim.toPrimitiveIntFlat());
	}

	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "newArray",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class, Integer.class),
				MethodType.methodType(Object.class, Class.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "multiNewArray",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class, Arrayref.class),
				MethodType.methodType(Object.class, Class.class, int[].class));

		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "get",
				MethodType.methodType(Object.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(Object.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getBoolean",
				MethodType.methodType(boolean.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(boolean.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getByte",
				MethodType.methodType(byte.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(byte.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getChar",
				MethodType.methodType(char.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(char.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getDouble",
				MethodType.methodType(double.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(double.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getFloat",
				MethodType.methodType(float.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(float.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getInt",
				MethodType.methodType(int.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(int.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getLong",
				MethodType.methodType(long.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(long.class, Object.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getShort",
				MethodType.methodType(short.class, Frame.class, Arrayref.class, Integer.class),
				MethodType.methodType(short.class, Object.class, int.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "getLength",
				MethodType.methodType(int.class, Frame.class, Arrayref.class),
				MethodType.methodType(int.class, Object.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "set",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Objectref.class),
				MethodType.methodType(void.class, Object.class, int.class, Object.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setBoolean",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class),
				MethodType.methodType(void.class, Object.class, int.class, boolean.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setByte",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class),
				MethodType.methodType(void.class, Object.class, int.class, byte.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setChar",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class),
				MethodType.methodType(void.class, Object.class, int.class, char.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setDouble",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Double.class),
				MethodType.methodType(void.class, Object.class, int.class, double.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setFloat",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Float.class),
				MethodType.methodType(void.class, Object.class, int.class, float.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setInt",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class),
				MethodType.methodType(void.class, Object.class, int.class, int.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setLong",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Long.class),
				MethodType.methodType(void.class, Object.class, int.class, long.class));
		NativeWrapper.registerNativeMethod(NativeJavaLangReflectArray.class, pkg, "setShort",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class),
				MethodType.methodType(void.class, Object.class, int.class, short.class));

	}

}
