package de.wwu.muggl.vm;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.List;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.UndefinedValue;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.PrimitiveWrappingImpossibleException;

/**
 * Helpers for Reflection functionality that mostly create appropriate objectrefs
 * 
 * @author Max Schulze
 *
 */
public class Reflection {
	public static Objectref newField(Field field) {

		// name_oop
		Objectref name = SystemDictionary.gI().getVm().getStringCache().getStringObjectref(field.getName());

		ClassFile holder = field.getClassFile();

		Objectref rh = SystemDictionary.gI().getVm()
				.getAndInitializeObjectref(SystemDictionary.gI().reflect_Field_klass);

		rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("clazz"),
				field.getClassFile().getMirrorJava());
		// slot is burky. Fake with what?
		rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("slot"), -1);
		rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("name"), name);
		rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("type"),
				newType(field.getDescriptor(), holder));
		// Note the ACC_ANNOTATION bit, which is a per-class access flag, is never set here.
		rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("modifiers"),
				field.getAccessFlags() & Modifier.fieldModifiers());
		rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("override", true), false);

		if (field.getDescriptor().length() > 0) {
			Objectref sig = SystemDictionary.gI().getVm().getStringCache().getStringObjectref(field.getDescriptor());
			rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("signature", false), sig);
		}

		VirtualMachine vm = SystemDictionary.gI().getVm();

		try {
			Objectref refval = vm.getClassLoader()
					.getClassAsClassFile(VmSymbols.basicType2JavaClassName(VmSymbols.signature2BasicType("B")))
					.getAPrimitiveWrapperObjectref(vm);
			Arrayref arrayref = new Arrayref(refval, 0);
			rh.putField(rh.getInitializedClass().getClassFile().getFieldByName("annotations", false), arrayref);
		} catch (PrimitiveWrappingImpossibleException e) {
			e.printStackTrace();
		} catch (ClassFileException e) {
			e.printStackTrace();
		}
		// FIXME mxs: finish field reflection
		// if (java_lang_reflect_Field::has_annotations_field()) {
		// typeArrayOop an_oop = Annotations::make_java_array(fd->annotations(), CHECK_NULL);
		// java_lang_reflect_Field::set_annotations(rh(), an_oop);
		// }
		// if (java_lang_reflect_Field::has_type_annotations_field()) {
		// typeArrayOop an_oop = Annotations::make_java_array(fd->type_annotations(), CHECK_NULL);
		// java_lang_reflect_Field::set_type_annotations(rh(), an_oop);
		// }
		return rh;
	}

	private static Objectref newType(String signature, ClassFile holder) {

		// Basic types
		BasicType type = VmSymbols.signature2BasicType(signature);
		if (type != VmSymbols.BasicType.T_OBJECT) {
			return Universe.java_mirror(type);
		}

		// oop loader = InstanceKlass::cast(k())->class_loader();
		// oop protection_domain = k()->protection_domain();
		// Klass* result = SystemDictionary::resolve_or_fail(signature,
		// Handle(THREAD, loader),
		// Handle(THREAD, protection_domain),
		// true, CHECK_(Handle()));

		ClassFile res = SystemDictionary.gI().resolveOrFail(signature);
		// this makes sure the java mirror is there (after static initialization!)
		return res.getTheInitializedClass(SystemDictionary.gI().getVm()).getClassFile().getMirrorJava();
	}

	public static Objectref newMethod(Method method, boolean for_constant_pool_access) {
		// boolean intern_name = TRUE !!

		// instanceKlassHandle holder (THREAD, method->method_holder());
		// int slot = method->method_idnum();
		//
		// Symbol* signature = method->signature();
		String signature = method.getDescriptor();

		// use a more thoroughly tested helper for parsing return type and parameters
		MethodType mt = MethodType.fromMethodDescriptorString(signature,
				SystemDictionary.gI().getVm().getClassLoader());
		@SuppressWarnings("unused")
		int parameterCount = mt.parameterCount();
		List<Class<?>> parameterTypes = mt.parameterList();
		Class<?> returnType = mt.returnType();
		if (returnType == null)
			return null;

		// convert parameter types to objectrefs representing class - instances
		Arrayref paramTypesObjref = new Arrayref(SystemDictionary.gI().Class_klass.getANewInstance(),
				parameterTypes.size());
		{
			int i = 0;
			for (Class<?> class1 : parameterTypes) {
				paramTypesObjref.putElement(i, clazzForPrimitiveOrClassName(class1.getName()));
				i++;
			}
		}

		Arrayref exceptionTypesObjref = new Arrayref(SystemDictionary.gI().Class_klass.getANewInstance(),
				method.getDeclaredExceptions().length);
		{
			int i = 0;
			for (String ex : method.getDeclaredExceptions()) {
				exceptionTypesObjref.putElement(i, clazzForPrimitiveOrClassName(ex));
				i++;
			}
		}
		
		Objectref nameObjref = SystemDictionary.gI().getVm().getStringCache().getStringObjectref(method.getName());
		int modifiers = method.getAccessFlags() & Modifier.methodModifiers();

		Objectref mh = SystemDictionary.gI().getVm()
				.getAndInitializeObjectref(SystemDictionary.gI().reflect_Method_klass);
		mh.setDebugHelperString(method.getName());

		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("clazz"),
				method.getClassFile().getMirrorJava());
		// FIXME mxs: slot?
		// java_lang_reflect_Method::set_slot(mh(), slot);
		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("name"), nameObjref);
		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("returnType"),
				clazzForPrimitiveOrClassName(mt.returnType().getName()));
		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("parameterTypes"), paramTypesObjref);
		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("exceptionTypes"), exceptionTypesObjref);

		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("modifiers"), modifiers);
		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("override", true), false);
		mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("signature"),
				SystemDictionary.gI().getVm().getStringCache().getStringObjectref(signature));
		// FIXME mxs: annotations
		// if (java_lang_reflect_Method::has_annotations_field()) {
		// typeArrayOop an_oop = Annotations::make_java_array(method->annotations(), CHECK_NULL);
		// java_lang_reflect_Method::set_annotations(mh(), an_oop);
		// }

		VirtualMachine vm = SystemDictionary.gI().getVm();
		Objectref refval;
		try {
			refval = vm.getClassLoader()
					.getClassAsClassFile(VmSymbols.basicType2JavaClassName(VmSymbols.signature2BasicType("B")))
					.getAPrimitiveWrapperObjectref(vm);
			Arrayref arrayref = new Arrayref(refval, 0);
			mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("annotations", false), arrayref);
			arrayref = new Arrayref(refval, 0);
			mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("parameterAnnotations", false),
					arrayref);
			arrayref = new Arrayref(refval, 0);
			mh.putField(mh.getInitializedClass().getClassFile().getFieldByName("annotationDefault", false), arrayref);
		} catch (PrimitiveWrappingImpossibleException | ClassFileException e) {
			e.printStackTrace();
		}

		// if (java_lang_reflect_Method::has_parameter_annotations_field()) {
		// typeArrayOop an_oop = Annotations::make_java_array(method->parameter_annotations(), CHECK_NULL);
		// java_lang_reflect_Method::set_parameter_annotations(mh(), an_oop);
		// }
		// if (java_lang_reflect_Method::has_annotation_default_field()) {
		// typeArrayOop an_oop = Annotations::make_java_array(method->annotation_default(), CHECK_NULL);
		// java_lang_reflect_Method::set_annotation_default(mh(), an_oop);
		// }
		// if (java_lang_reflect_Method::has_type_annotations_field()) {
		// typeArrayOop an_oop = Annotations::make_java_array(method->type_annotations(), CHECK_NULL);
		// java_lang_reflect_Method::set_type_annotations(mh(), an_oop);
		// }

		return mh;
	}

	public static void invokeMethod(Frame frame, Objectref methodObjref, Object obj, Arrayref args)
			throws ExecutionException {
		Objectref MugglClass = (Objectref) methodObjref
				.getField(methodObjref.getInitializedClass().getClassFile().getFieldByName("clazz"));
		ClassFile cf = MugglClass.getMirrorMuggl();
		String methodName = "";
		String methodSig = "";
		methodName = frame.getVm().getStringCache().getStringFieldValue(methodObjref, "name");
		methodSig = frame.getVm().getStringCache().getStringFieldValue(methodObjref, "signature");

		Method method = cf.getMethodByNameAndDescriptor(methodName, methodSig);

		// prepare Arguments
		int addOne = 0;
		if (!method.isAccStatic())
			addOne++;
		Object[] arguments = null;

		if (args != null) {
			arguments = new Object[args.length + addOne];
			for (int a = 0; a < args.length; a++) {
				if (method.isAccVarargs() && a == args.length - 1 && args.getElement(a) != null
						&& args.getElement(a) instanceof UndefinedValue) {
					/*
					 * The method may take variable arguments. If its last parameter is undefined, pass an array of zero
					 * length.
					 */
					Objectref objectref = null;
					try {
						objectref = frame.getVm().getAnObjectref(
								frame.getVm().classLoader.getClassAsClassFile(method.getParameterTypeAtIndex(a)));
					} catch (ClassFileException e) {
						e.printStackTrace();
					}
					Arrayref arrayref = new Arrayref(objectref, 0);
					arguments[a + addOne] = arrayref;
				} else {
					// Just store the object.
					Object elem = args.getElement(a);
					if (elem instanceof Objectref) {
						if (((Objectref) elem).getInitializedClass().getClassFile().getName()
								.equals("java.lang.Integer")) {
							arguments[a + addOne] = (int) ((Objectref) elem).getField(
									((Objectref) elem).getInitializedClass().getClassFile().getFieldByName("value"));
						}else{							
							arguments[a + addOne] = elem;
						}
					}
				}
			}
		} else {
			arguments = new Object[1];
		}
		if (!method.isAccStatic())
			arguments[0] = obj;

		Globals.getInst().execLogger.debug("allocating new frame from reflective invocation of method");
		// Save current frame...
		// frame.setPc(frame.getVm().getPc() + 1 + Invokestatic.getNumberOfOtherBytes()); // is not static... invoke0 is
		// static
		frame.setPc(frame.getVm().getPc() + 1 + 2);
		frame.getVm().getStack().push(frame);

		// Push new one.
		frame.getVm().createAndPushFrame(frame, method, arguments);

		// Finish.
		frame.getVm().setReturnFromCurrentExecution(true);
	}

	public static Objectref newConstructor(Method method) {
		
		return null;
	}

	/**
	 * return the clazz-objectref for a primitive "int",... or ClassName like "java.lang.Integer"
	 * 
	 * @param arg
	 * @return
	 */
	private static Objectref clazzForPrimitiveOrClassName(String arg) {
		Objectref mirror = null;
		BasicType t = VmSymbols.primitiveName2BasicType(arg);
		if (t != BasicType.T_ILLEGAL && t != BasicType.T_OBJECT && t != BasicType.T_ARRAY) {
			mirror = Universe.java_mirror(t);
		} else {
			ClassFile classF;
			try {
				classF = SystemDictionary.gI().getVm().getClassLoader().getClassAsClassFile(arg);
				mirror = classF.getTheInitializedClass(SystemDictionary.gI().getVm()).getClassFile().getMirrorJava();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return mirror;
	}
}
