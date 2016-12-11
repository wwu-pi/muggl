package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SystemDictionary;
import de.wwu.muggl.vm.Universe;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.JavaClasses.java_lang_Class;
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
public class NativeJavaLangClass extends NativeMethodProvider {
	public static String pkg = "java.lang.Class";

	public static boolean isPrimitive(Frame frame, Objectref invokingObjectref) {
		boolean isPrimitive = invokingObjectref.getMirrorMuggl() == null;
		return isPrimitive;
	}

	public static boolean isInstance(Frame frame, Objectref invokingObjectref, Objectref obj) {
		assert (java_lang_Class.is_instance(invokingObjectref));
		ClassFile classF = invokingObjectref.getMirrorMuggl();

		ExecutionAlgorithms ea = new ExecutionAlgorithms(frame.getVm().getClassLoader());
		try {
			return ea.checkForAssignmentCompatibility(obj, classF.getName(), frame.getVm(), false);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isAssignableFrom(Frame frame, Objectref invokingObjectref, Objectref obj) {
		try {
			assert (java_lang_Class.is_instance(invokingObjectref));
			assert (java_lang_Class.is_instance(obj));

			ClassFile classFThis = invokingObjectref.getMirrorMuggl();
			if (classFThis == null) {
				BasicType typeThis = java_lang_Class.primitive_type(invokingObjectref);
				classFThis = frame.getVm().getClassLoader()
						.getClassAsClassFile(VmSymbols.basicType2JavaClassName(typeThis));
			}

			ClassFile classFThat = obj.getMirrorMuggl();
			if (classFThat == null) {
				BasicType typeThat = java_lang_Class.primitive_type(invokingObjectref);
				classFThat = frame.getVm().getClassLoader()
						.getClassAsClassFile(VmSymbols.basicType2JavaClassName(typeThat));
			}

			if (classFThis == classFThat)
				return true;

			Objectref that = classFThat.getTheInitializedClass(frame.getVm()).getANewInstance();

			ExecutionAlgorithms ea = new ExecutionAlgorithms(frame.getVm().getClassLoader());
			return ea.checkForAssignmentCompatibility(that, classFThis.getName(), frame.getVm(), false);
		} catch (ExecutionException | ClassFileException | AssertionError e) {
			e.printStackTrace();
		}
		return false;
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
			Globals.getInst().execLogger.debug("forName0 getting " + arg);
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
			BasicType t = VmSymbols.primitiveName2BasicType(arg);
			if (t != BasicType.T_ILLEGAL && t != BasicType.T_OBJECT && t != BasicType.T_ARRAY) {
				mirror = Universe.java_mirror(t);
			}
			if (mirror == null && t == BasicType.T_INT) {
				// allow. This happens when mirrors are not (yet) initialized
				return null;
			} else if (mirror == null) {
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

	public static Integer desiredAssertionStatus0(Frame frame, Objectref invokingObjectref) {
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

	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "registerNatives",
				MethodType.methodType(void.class), MethodType.methodType(void.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "desiredAssertionStatus0",
				MethodType.methodType(Integer.class, Frame.class, Objectref.class),
				MethodType.methodType(boolean.class, Class.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "forName0",
				MethodType.methodType(Objectref.class, Frame.class, Object.class, Object.class, Object.class,
						Object.class),
				MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getComponentType",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class),
				MethodType.methodType(Class.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getDeclaredClasses0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class),
				MethodType.methodType(Class[].class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getDeclaredConstructors0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class, Object.class),
				MethodType.methodType(Constructor[].class, boolean.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getDeclaredFields0",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class, Object.class),
				MethodType.methodType(Field[].class, boolean.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getDeclaredMethods0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class, Object.class),
				MethodType.methodType(Method[].class, boolean.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getDeclaringClass0",
				MethodType.methodType(Object.class, Frame.class, Objectref.class), MethodType.methodType(Class.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getInterfaces0",
				MethodType.methodType(Arrayref.class, Frame.class, Objectref.class),
				MethodType.methodType(Class[].class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getModifiers",
				MethodType.methodType(int.class, Frame.class, Objectref.class), MethodType.methodType(int.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getName0",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class),
				MethodType.methodType(String.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getPrimitiveClass",
				MethodType.methodType(Objectref.class, Frame.class, Object.class),
				MethodType.methodType(Class.class, String.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "getSuperclass",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class),
				MethodType.methodType(Class.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "isArray",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class),
				MethodType.methodType(boolean.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "isInterface",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class),
				MethodType.methodType(boolean.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "isPrimitive",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class),
				MethodType.methodType(boolean.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "isInstance",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class, Objectref.class),
				MethodType.methodType(boolean.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangClass.class, pkg, "isAssignableFrom",
				MethodType.methodType(boolean.class, Frame.class, Objectref.class, Objectref.class),
				MethodType.methodType(boolean.class, Class.class));
	}

}
