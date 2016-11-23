package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

public class NativeJavaLangReflectArray implements NativeMethodProvider {
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
				BasicType t = VmSymbols.name2type(name);
				ref = frame.getVm().getClassLoader().getClassAsClassFile(VmSymbols.PRIMITIVES_JAVA_CLASSES[t.value])
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
				BasicType t = VmSymbols.name2type(name);
				ref = frame.getVm().getClassLoader().getClassAsClassFile(VmSymbols.PRIMITIVES_JAVA_CLASSES[t.value])
						.getAPrimitiveWrapperObjectref(frame.getVm());
			} catch (ExecutionException | ClassFileException e) {
				e.printStackTrace();
			}
		} else {
			ref = compType.getMirrorMuggl().getInitializedClass().getANewInstance();
		}
				
		return new Arrayref(ref, dim.toPrimitiveIntFlat());
	}

	public void registerNatives() {
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "newArray",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "multiNewArray",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class, Arrayref.class));
		
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "get",
				MethodType.methodType(Object.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getBoolean",
				MethodType.methodType(boolean.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getByte",
				MethodType.methodType(byte.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getChar",
				MethodType.methodType(char.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getDouble",
				MethodType.methodType(double.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getFloat",
				MethodType.methodType(float.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getInt",
				MethodType.methodType(int.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getLong",
				MethodType.methodType(long.class, Frame.class, Arrayref.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getShort",
				MethodType.methodType(short.class, Frame.class, Arrayref.class, Integer.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "set",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Objectref.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setBoolean",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setByte",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setChar",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setDouble",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Double.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setFloat",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Float.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setInt",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setLong",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Long.class));
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "setShort",
				MethodType.methodType(void.class, Frame.class, Arrayref.class, Integer.class, Integer.class));

	}

}
