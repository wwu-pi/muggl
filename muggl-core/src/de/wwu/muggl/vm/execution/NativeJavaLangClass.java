package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SystemDictionary;
import de.wwu.muggl.vm.Universe;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * Implementations for native methods in java.lang.Class
 * 
 * @author Max Schulze
 *
 */
public class NativeJavaLangClass implements NativeMethodProvider {
	public static String pkg = "java.lang.Class";

	public static boolean isPrimitive(Frame frame, Objectref invokingObjectref) {
		boolean isPrimitive = invokingObjectref.getMirrorMuggl() == null;
		return isPrimitive;
	}

	public static Objectref getSuperclass(Frame frame, Objectref invokingObjectref) {
		if (invokingObjectref.isMirroredMugglIsArray()) {
			// arrays have java.lang.Object as Superclass
			return SystemDictionary.gI().Object_klass.getClassFile().getMirrorJava();
		}
		ClassFile mirror = invokingObjectref.getMirrorMuggl();
		try {
			if (mirror.getSuperClassFile() != null) {
				// ensure superClassFile is statically initialized
				mirror.getSuperClassFile().getTheInitializedClass(frame.getVm());
				return mirror.getSuperClassFile().getMirrorJava();
			}
		} catch (ClassFileException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Objectref getComponentType(Frame frame, Objectref invokingObjectref) {
		return invokingObjectref.asClass().getComponentType();
	}

	public static Objectref getName0(Frame frame, Objectref invokingObjectref) {
		Object ret = invokingObjectref.asClass().getName0();
		Objectref objref = null;
		if (ret instanceof String) {
			objref = frame.getVm().getStringCache().getStringObjectref((String) ret);
		} else
			objref = (Objectref) ret;
		return objref;
	}

	public static Objectref forName0(Frame frame, Object p1, Object p2, Object p3, Object p4)
			throws VmRuntimeException {
		// currently ignores the other two parameters
		try {
			String arg = (String) new MugglToJavaConversion(frame.getVm()).toJava(p1);
			ClassFile classf;
			classf = frame.getVm().getClassLoader().getClassAsClassFile(arg);
			return classf.getTheInitializedClass(frame.getVm(), true).getClassFile().getMirrorJava();
		} catch (ClassFileException | ConversionException e) {
			throw new VmRuntimeException(
					frame.getVm().generateExc("java.lang.ClassNotFoundException", "forName0 failed..."));
		}
	}

	public static Objectref getPrimitiveClass(Frame frame, Object p1) {
		Objectref mirror = null;
		String arg;
		try {
			arg = (String) new MugglToJavaConversion(frame.getVm()).toJava(p1);
			BasicType t = VmSymbols.name2type(arg);
			if (t != BasicType.T_ILLEGAL && t != BasicType.T_OBJECT && t != BasicType.T_ARRAY) {
				mirror = Universe.java_mirror(t);
			}
			if (mirror == null) {
				throw new ForwardingUnsuccessfulException(
						"ClassNotFoundException, getPrimitiveClass failed on: " + arg);
			} else {

				return mirror;
			}
		} catch (ConversionException | ForwardingUnsuccessfulException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Arrayref getDeclaredFields0(Frame frame, Objectref invokingObjectref, Object p1) {
		Globals.getInst().execLogger.debug("entering getDeclaredFields0");
		// equivalent of jvm.cpp:1776
		if (invokingObjectref.asClass().isPrimitive() || invokingObjectref.asClass().isArray()) {
			// Return empty array
			frame.getOperandStack().push(new Arrayref(SystemDictionary.gI().reflect_Field_klass.getANewInstance(), 0));
		}

		boolean publicOnly = (p1 instanceof Integer) ? ((Integer) p1 == 0 ? false : true) : (boolean) p1;
		return invokingObjectref.asClass().getDeclaredFields0(publicOnly);
	}

	public static Object getDeclaredMethods0(Frame frame, Objectref invokingObjectref, Object p1) {
		Globals.getInst().execLogger.debug("entering getDeclaredMethods0");
		boolean wasHidden = frame.isHiddenFrame();
		frame.setHiddenFrame(true);
		boolean publicOnly = (p1 instanceof Integer) ? ((Integer) p1 == 0 ? false : true) : (boolean) p1;

		frame.setHiddenFrame(wasHidden);
		return invokingObjectref.asClass().getDeclaredMethods0(publicOnly);
	}

	public static Arrayref getInterfaces0(Frame frame, Objectref invokingObjectref) {
		Class<?>[] ret = invokingObjectref.asClass().getInterfaces0();
		Arrayref res = new Arrayref(SystemDictionary.gI().Class_klass.getANewInstance(), ret.length);
		if (ret.length != 0) {
			// convert to arrayref of class<?>
			int i = 0;
			for (Class<?> class1 : ret) {
				try {
					res.putElement(i, SystemDictionary.gI().getVm().getClassLoader().getClassAsClassFile(class1)
							.getTheInitializedClass(SystemDictionary.gI().getVm()).getClassFile().getMirrorJava());
				} catch (ClassFileException e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return res;
	}

	public static Object getDeclaredConstructors0(Frame frame, Objectref invokingObjectref, Object p1) {
		boolean publicOnly = (p1 instanceof Integer) ? ((Integer) p1 == 0 ? false : true) : (boolean) p1;
		return invokingObjectref.asClass().getDeclaredConstructors0(publicOnly);
	}

	public static Integer desiredAssertionStatus0(Frame frame) {
		return ((Integer) VmSymbols.wideningPrimConversion(frame.getVm().isAssertionEnabled(), Integer.class));
	}

	public static Object getDeclaringClass0(Frame frame, Objectref invokingObjectref) {
		Object ret = invokingObjectref.asClass().getDeclaringClass0();
		if (ret != null) {
			try {
				ret = SystemDictionary.gI().getVm().getClassLoader().getClassAsClassFile((Class<?>) ret)
						.getTheInitializedClass(SystemDictionary.gI().getVm()).getClassFile().getMirrorJava();
			} catch (ClassFileException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static Object getDeclaredClasses0(Frame frame, Objectref invokingObjectref) {
		Class<?>[] ret = invokingObjectref.asClass().getDeclaredClasses0();
		Arrayref res = new Arrayref(SystemDictionary.gI().Class_klass.getANewInstance(), ret.length);
		if (ret.length != 0) {
			// convert to arrayref of class<?>
			int i = 0;
			for (Class<?> class1 : ret) {
				try {
					res.putElement(i, SystemDictionary.gI().getVm().getClassLoader().getClassAsClassFile(class1)
							.getTheInitializedClass(SystemDictionary.gI().getVm()).getClassFile().getMirrorJava());
				} catch (ClassFileException e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return res;

	}

	public static int getModifiers(Frame frame, Objectref invokingObjectref) {
		return (invokingObjectref.asClass().getModifiers());
	}

	public static boolean isArray(Frame frame, Objectref invokingObjectref) {
		return (invokingObjectref.asClass().isArray());
	}

	public static boolean isInterface(Frame frame, Objectref invokingObjectref) {
		return (invokingObjectref.asClass().isInterface());
	}

	// FIXME: missiing: isInstance

	public void registerNatives() {
		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "desiredAssertionStatus0",
				MethodType.methodType(Integer.class, Frame.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "forName0", MethodType.methodType(Objectref.class,
				Frame.class, Object.class, Object.class, Object.class, Object.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getComponentType",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getDeclaredClasses0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getDeclaredConstructors0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class, Object.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getDeclaredFields0",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class, Object.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getDeclaredMethods0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class, Object.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getDeclaringClass0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getInterfaces0",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getModifiers",
				MethodType.methodType(int.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getName0",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getPrimitiveClass",
				MethodType.methodType(Objectref.class, Frame.class, Object.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "getSuperclass",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "isArray",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "isInterface",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class));

		NativeWrapper.registerNativeMethod(this.getClass(), pkg, "isPrimitive",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class));
	}

}
