package de.wwu.muggl.vm.initialization;

import java.util.ArrayList;

import de.wwu.muggl.vm.Reflection;
import de.wwu.muggl.vm.SystemDictionary;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;

/**
 * This class collects all methods that implement the 'native' behaviour of java.lang.Class.
 * 
 * Do not add fields!
 * 
 * @author Max Schulze
 *
 */
public class ObjectrefAsClass {

	private Objectref thiss;

	// if ObjectrefAsClass would extend Objectref, that wouldn't help because we can't
	// do downcasting, hence this "trick"
	// alternative would be to have each method take an obj argument...not really happy about both
	public ObjectrefAsClass(Objectref clazz) {
		thiss = clazz;
	}

	// isInstance

	// isAssignableFrom

	public boolean isInterface() {
		if (this.isPrimitive()) {
			return false;
		} else {
			ClassFile orig_class = thiss.getMirrorMuggl();
			return orig_class.isAccInterface();
		}
	}

	/**
	 * @return whether the represented is Array
	 */
	public boolean isArray() {
		return thiss.isMirroredMugglIsArray();
	}

	/**
	 * Whether the represented class is primitive
	 * 
	 * @return true, if the represented class is primitive
	 */
	public boolean isPrimitive() {
		return thiss.getMirrorMuggl() == null;
	}

	/**
	 * 
	 * @return String or objref. Caller takes care.
	 */
	public Object getName0() {

		if (thiss.isMirroredMugglIsArray()) {
			return thiss.getMirroredMugglArray().getSignature();
		}

		ClassFile mirror = thiss.getMirrorMuggl();
		if (mirror == null) {
			// primitives don't have mirrors, so just take their name
			return (Objectref) thiss.getField(thiss.getInitializedClass().getClassFile().getFieldByName("name"));
		} else
			return mirror.getName();
	}

	// getSuperclass currently in NativeWrapper cause it needs VM to instantiate new objref

	public Class<?>[] getInterfaces0() {
		if (this.isPrimitive())
			return new Class[0];

		// let the ordinary java do the trick for us:
		return thiss.getMirrorMuggl().getInstanceOfClass().getInterfaces();
	}

	public Objectref getComponentType() {
		if (this.isArray()) {
			// directly "attached" to an arrayref
			return ((Objectref) thiss.getMirroredMugglArray().getReferenceValue()).getMirrorJava();
		} else if (thiss.getSysfields().containsKey(Objectref.SYSFIELDNAME_ARRAYCLASS)
				&& (boolean) thiss.getSysfields().get(Objectref.SYSFIELDNAME_ARRAYCLASS)) {
			// class representation (e.g. CONSTANT) for an array
			return (Objectref) thiss.getSysfields().get(Objectref.SYSFIELDNAME_ARRAYCLASS_COMPONENTTYPE);
		}
		return null;
	}

	public int getModifiers() {
		if (this.isPrimitive()) {
			return ClassFile.ACC_ABSTRACT | ClassFile.ACC_FINAL | ClassFile.ACC_PUBLIC;
		} else {
			// when we're here, this is the javaMirror (=class class) of an objectref we have to get
			ClassFile orig_class = thiss.getMirrorMuggl();

			return ((orig_class.isAccEnum() ? 1 : 0) * ClassFile.ACC_ENUM)
					| ((orig_class.isAccAnnotation() ? 1 : 0) * ClassFile.ACC_ANNOTATION)
					| ((orig_class.isAccSynthetic() ? 1 : 0) * ClassFile.ACC_SYNTHETIC)
					| ((orig_class.isAccAbstract() ? 1 : 0) * ClassFile.ACC_ABSTRACT)
					| ((orig_class.isAccInterface() ? 1 : 0) * ClassFile.ACC_INTERFACE)
					| ((orig_class.isAccSuper() ? 1 : 0) * ClassFile.ACC_SUPER)
					| ((orig_class.isAccFinal() ? 1 : 0) * ClassFile.ACC_FINAL)
					| ((orig_class.isAccPublic() ? 1 : 0) * ClassFile.ACC_PUBLIC);
		}
	}

	/**
	 * Someone should point out the differences between modifiers and access flags...
	 * 
	 * openjdk makes difference between klass->access_flags and klass->modifier_flags
	 * 
	 * @return
	 */
	public int getAccessFlags() {
		if (this.isPrimitive()) {
			return ClassFile.ACC_ABSTRACT | ClassFile.ACC_FINAL | ClassFile.ACC_PUBLIC;
		} else {
			// when we're here, this is the javaMirror (=class class) of an objectref we have to get
			ClassFile orig_class = thiss.getMirrorMuggl();

			return ((orig_class.isAccEnum() ? 1 : 0) * ClassFile.ACC_ENUM)
					| ((orig_class.isAccAnnotation() ? 1 : 0) * ClassFile.ACC_ANNOTATION)
					| ((orig_class.isAccSynthetic() ? 1 : 0) * ClassFile.ACC_SYNTHETIC)
					| ((orig_class.isAccAbstract() ? 1 : 0) * ClassFile.ACC_ABSTRACT)
					| ((orig_class.isAccInterface() ? 1 : 0) * ClassFile.ACC_INTERFACE)
					| ((orig_class.isAccSuper() ? 1 : 0) * ClassFile.ACC_SUPER)
					| ((orig_class.isAccFinal() ? 1 : 0) * ClassFile.ACC_FINAL)
					| ((orig_class.isAccPublic() ? 1 : 0) * ClassFile.ACC_PUBLIC) & VmSymbols.ACC_WRITTEN_FLAGS;
		}
	}

	// getSigners

	// setSigners

	// getEnclosingMethod0

	public Class<?> getDeclaringClass0() {
		if (this.isPrimitive())
			return null;

		return thiss.getMirrorMuggl().getInstanceOfClass().getDeclaringClass();
	}

	// getProtectionDomain

	// getPrimitiveClass handled by NativeWrapper for ClassLoading

	// getGenericSignature0

	// getRawAnnotations

	// getRawTypeAnnotations

	// getConstantPool

	private Arrayref getClassDeclaredFields0(boolean publicOnly) {
		// when we're here, this is the javaMirror (=class class) of an objectref we have to get
		ClassFile orig_class = thiss.getMirrorMuggl();
		Field[] fields = orig_class.getFields();

		boolean skip_backtrace = false;

		int num_fields = 0;
		if (publicOnly) {
			for (Field field : fields) {
				if (field.isAccPublic())
					num_fields++;
			}
		} else {
			num_fields = fields.length;
			if (thiss.getInitializedClass() == SystemDictionary.gI().Throwable_klass) {
				num_fields--;
				skip_backtrace = true;
			}
		}

		Objectref referenceValue = SystemDictionary.gI().getVm()
				.getAnObjectref(SystemDictionary.gI().reflect_Field_klass.getClassFile());
		Arrayref r = new Arrayref(referenceValue, num_fields);

		int out_idx = 0;

		for (Field field : fields) {
			if (skip_backtrace) {
				// 4496456 skip java.lang.Throwable.backtrace
				if (field.getName() == "backtrace")
					continue;
			}

			if (!publicOnly || field.isAccPublic()) {
				Objectref jfield = Reflection.newField(field);
				r.putElement(out_idx, jfield);
				out_idx++;
			}

		}
		assert (out_idx == num_fields);
		return r;
	}

	private Object getClassDeclaredMethodsHelper(boolean publicOnly, boolean wantConstructor,
			InitializedClass reflect_Method_klass) {
		if (thiss.getMirrorMuggl() == null || thiss.isMirroredMugglIsArray()) {
			return new Arrayref(reflect_Method_klass.getANewInstance(), 0);
		}
		ClassFile mirrored = thiss.getMirrorMuggl();
		mirrored.linkClass();
		Method[] methods = mirrored.getMethods();
		int methods_length = methods.length;
		ArrayList<Method> selMethods = new ArrayList<>();

		int num_methods = 0;

		for (int i = 0; i < methods_length; i++) {
			if (methods[i].selectMethod(wantConstructor)) {
				if (!publicOnly || methods[i].isAccPublic()) {
					selMethods.add(methods[i]);
					num_methods++;
				}
			}
		}
		// Allocate result
		Arrayref res = new Arrayref(reflect_Method_klass.getANewInstance(), num_methods);

		int i = 0;
		for (Method method : selMethods) {
			if (method == null) {
				res.elements[i] = null;
			} else {
				Objectref o;
				if (wantConstructor)
					o = Reflection.newConstructor(method);
				else
					o = Reflection.newMethod(method, false);
				res.elements[i] = o;
			}
			i++;
		}
		return res;
	}

	/**
	 * implementation for java/lang/Class.getDeclaredFields
	 * 
	 * @return array of fields
	 */
	public Arrayref getDeclaredFields0(boolean publicOnly) {
		if (this.isPrimitive() || this.isArray()) {
			// Return empty array
			Objectref referenceValue = SystemDictionary.gI().getVm()
					.getAnObjectref(SystemDictionary.gI().reflect_Field_klass.getClassFile());
			return new Arrayref(referenceValue, 0);
		} else
			return this.getClassDeclaredFields0(publicOnly);
	}

	public Object getDeclaredMethods0(boolean publicOnly) {
		return this.getClassDeclaredMethodsHelper(publicOnly, false, SystemDictionary.gI().reflect_Method_klass);
	}

	public Object getDeclaredConstructors0(boolean publicOnly) {
		return this.getClassDeclaredMethodsHelper(publicOnly, false, SystemDictionary.gI().reflect_Constructor_klass);
	}

	public Class<?>[] getDeclaredClasses0() {
		// let the ordinary java do the trick for us:
		return thiss.getMirrorMuggl().getInstanceOfClass().getDeclaredClasses();
	}

	public boolean isInstance() {
		return true;
	}

	// desiredAssertionStatus0

}
