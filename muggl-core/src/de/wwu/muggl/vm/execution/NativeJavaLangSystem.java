package de.wwu.muggl.vm.execution;

import java.lang.invoke.MethodType;
import java.util.Properties;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;

public class NativeJavaLangSystem extends NativeMethodProvider {
	public static String handledClassFQ = "java.lang.System";
	private static ClassFile CLASS_VM = null;
	private static ClassFile CLASS_VMPROPERTIESWRAPPER = null;

	public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
		CLASS_VM = classLoader.getClassAsClassFile(handledClassFQ);
		CLASS_VMPROPERTIESWRAPPER = classLoader.getClassAsClassFile(
				de.wwu.muggl.vm.execution.nativeWrapping.VMPropertiesWrapper.class.getCanonicalName());
		registerNatives();
	}

	public static void arraycopy(Frame frame, Object p0, Object p1, Object p2, Object p3, Object p4)
			throws VmRuntimeException {
		// Possible exceptions for null parameters
		if (p0 == null || p2 == null) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException", "null"));
		}

		// Possible exceptions with regard to types.
		if (!(p0 instanceof Arrayref)) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayStoreException", "null"));
		}
		if (!(p2 instanceof Arrayref)) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayStoreException", "null"));
		}

		// Get the five parameters.
		Integer srcPos;
		Integer destPos;
		Integer length;
		Arrayref src = (Arrayref) p0;
		Object srcPosObject = p1;
		Arrayref dest = (Arrayref) p2;
		Object destPosObject = p3;
		Object lengthObject = p4;

		if (srcPosObject instanceof IntConstant) {
			srcPos = ((IntConstant) srcPosObject).getIntValue();
		} else {
			srcPos = (Integer) srcPosObject;
		}
		if (destPosObject instanceof IntConstant) {
			destPos = ((IntConstant) destPosObject).getIntValue();
		} else {
			destPos = (Integer) destPosObject;
		}
		if (lengthObject instanceof IntConstant) {
			length = ((IntConstant) lengthObject).getIntValue();
		} else {
			length = (Integer) lengthObject;
		}

		// Further possible exceptions with regard to types.
		if (src.isPrimitive()) {
			if (dest.isPrimitive()) {
				if (src.getInitializedClass() != dest.getInitializedClass()) {
					throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayStoreException", "null"));
				}
			} else {
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayStoreException", "null"));
			}
		} else {
			if (dest.isPrimitive()) {
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayStoreException", "null"));
			}
		}

		// Exceptions that regard the arrays' bounds.
		if (srcPos < 0) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IndexOutOfBoundsException", "null"));
		}
		if (destPos < 0) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IndexOutOfBoundsException", "null"));
		}
		if (length < 0) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IndexOutOfBoundsException", "null"));
		}
		if (srcPos + length > src.length) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IndexOutOfBoundsException", "null"));
		}
		if (destPos + length > dest.length) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IndexOutOfBoundsException", "null"));
		}

		// Are both arrays the equal object?
		if (p0 == p2) {
			src = new Arrayref(dest.getReferenceValue(), length);
			for (int a = srcPos; a < srcPos + length; a++) {
				src.putElement(a - srcPos, dest.getElement(a));
			}
			for (int a = 0; a < length; a++) {
				dest.putElement(destPos + a, src.getElement(a));
			}
		} else {
			int copied = 0;
			for (int a = srcPos; a < srcPos + length; a++) {
				try {
					dest.putElement(destPos + copied, src.getElement(a));
					copied++;
				} catch (ArrayStoreException e) {
					// Wrap the exception.
					throw new VmRuntimeException(
							frame.getVm().generateExc("java.lang.ArrayStoreException", e.getMessage()));
				}
			}
		}
	}

	
	public static Objectref initProperties(Frame frame, Objectref arg1) {
		InitializedClass sunMiscVm = CLASS_VM.getTheInitializedClass(frame.getVm());
		Field savedProps = CLASS_VM.getFieldByName("props");
		sunMiscVm.putField(savedProps, frame.getVm().getAnObjectref(CLASS_VMPROPERTIESWRAPPER));
		frame.getVm().systemProperties.forEach((String k,String v)->{
			try {
				frame.getVm().set_property(arg1,k,v);
			} catch (ExecutionException | InvalidInstructionInitialisationException | InterruptedException | ClassFileException e) {
				e.printStackTrace();
			}
		});		
		return arg1;
	}
	public static void registerNatives() {
		NativeWrapper.registerNativeMethod(NativeJavaLangSystem.class, handledClassFQ, "arraycopy",
				MethodType.methodType(void.class, Frame.class, Object.class, Object.class, Object.class, Object.class,
						Object.class),
				MethodType.methodType(void.class, Object.class, int.class, Object.class, int.class, int.class));

		NativeWrapper.registerNativeMethod(NativeJavaLangSystem.class, handledClassFQ, "initProperties",
				MethodType.methodType(Objectref.class, Frame.class, Objectref.class),
				MethodType.methodType(Properties.class, Properties.class));
	}

}
