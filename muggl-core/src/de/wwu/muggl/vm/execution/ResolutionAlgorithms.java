package de.wwu.muggl.vm.execution;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.FieldResolutionError;
import de.wwu.muggl.instructions.MethodResolutionError;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * This class provided basic algorithms specified by the java virtual machine specification. They
 * are needed for the resolution of classes, methods etc. See chapter 5 of the jvm specification
 * (http://java.sun.com/docs/books/jvms/second_edition/html/ConstantPool.doc.html).
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-26
 */
public class ResolutionAlgorithms {
	private MugglClassLoader classLoader;

	/**
	 * Basic constructor to set the class loader.
	 * @param classLoader The class loader.
	 */
	public ResolutionAlgorithms(MugglClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Resolve the class denoted by String n and check if it may be access by the Class represented by
	 * ClassFile d. Return the corresponding ClassFile.
	 * @param d The class file representing the accessing class.
	 * @param n The name of the class to resolve.
	 * @return The resolved ClassFile.
	 * @throws IllegalAccessError If access for d to the class resolved is illegal.
	 * @throws NoClassDefFoundError If no class could be found for the name n.
	 */
	public ClassFile resolveClassAsClassFile(ClassFile d, String n) {
		ClassFile c;
		try {
			c = this.classLoader.getClassAsClassFile(n);
		} catch (ClassFileException e) {
			throw new NoClassDefFoundError("Class loading failed due to a ClassFileException with message: " + e.getMessage() + ".");
		}

		// Permission check.
		if (!checkClassAccessPermission(d, c)) throw new IllegalAccessError("Illegal access from " + d.getName() + " to " + c.getName() + ".");

		return c;
	}

	/**
	 * Check if the Class represented by ClassFile d may access the Class represented by ClassFile c.
	 * This is possible if either c is public, or c and d share the same package.
	 * @param d The Classfile representing the accessing class.
	 * @param c The Classfile representing the accessed class.
	 * @return true, if access is permitted, false otherwise.
	 */
	private boolean checkClassAccessPermission(ClassFile d, ClassFile c) {
		if (c.isAccPublic()) return true;
		if (d.getPackageName().equals(c.getPackageName())) return true;
		return false;
	}

	/**
	 * Resolve a Field as described in the java virtual machine manual. Throw a FieldResolutionError
	 * if the Field cannot be found, otherwise return the fully parsed Field as an instance of Field.
	 *
	 * @param classFile The classFile the Fields is expected to be found in.
	 * @param nameAndType The name and type descriptor of the Field.
	 * @return A instance of Field.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws NoSuchFieldError If the Field could not be found.
	 */
	public Field resolveField(
			ClassFile classFile,
			String[] nameAndType
	) throws ClassFileException {
		Field field = null;
		try {
			// 1. If C declares a field with the name and descriptor specified by the field reference, field lookup succeeds. The declared field is the result of the field lookup.
			field = classFile.getFieldByNameAndDescriptor(nameAndType[0], nameAndType[1]);
		} catch (FieldResolutionError e) {
			Globals.getInst().execLogger.trace("Lookup of field " + nameAndType[0] + ":" +  nameAndType[1] + " in class " + classFile.getName() + " unsuccessfull. Trying its super classes.");
			// unsuccessful - trying the super classes recursively
			while (classFile.getSuperClass() != 0) {
				classFile = this.classLoader.getClassAsClassFile(classFile.getConstantPool()[classFile.getSuperClass()].getStringValue());
				try {
					field = classFile.getFieldByNameAndDescriptor(nameAndType[0], nameAndType[1]);
					// if the Field could be resolved, quit the loop
					Globals.getInst().execLogger.trace("Lookup of " + nameAndType[0] + " in super class " + classFile.getName() + " succeeded.");
					break;
				} catch (FieldResolutionError e2) {
					// basically do nothing, but log on deep log levels
					Globals.getInst().execLogger.trace("Lookup of " + nameAndType[0] + " in super class " + classFile.getName() + " unsuccessfull. Trying its super classes.");
				}
			}

			// Successful lookup?
			if (field == null) {
				if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Lookup of " + nameAndType[1] + " failed.");
				throw new NoSuchFieldError("Field " + nameAndType[1] + " could not be resolved for class " + nameAndType[0] + " or any of its superclasses.");
			}
		}
		return field;
	}

	/**
	 * Resolve a methods as described in the java virtual machine manual. Throw a MethodResolutionError
	 * if the method cannot be found, otherwise return the fully parsed method as an instance of Method.
	 * 
	 * If the method is not found in the class, first look up its superclasses, then lookup its superinterfaces.
	 * @param classFile The classFile the methods is expected to be found in.
	 * @param nameAndType The name and type descriptor of the method.
	 * @return A instance of Method.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws NoSuchMethodError If the Method could not be found-
	 */
	public Method resolveMethod(final ClassFile classFile, final String[] nameAndType)
		throws ClassFileException {
		final String lookingFor = nameAndType[0] + ":" + nameAndType[1];
		
		// According to JVMs8 § 5.4.3.3
		// Step 1
		if (classFile.isAccInterface())
			throw new IncompatibleClassChangeError("Cannot call resolveMethod on an Interface");

		// Step 2: in C or its superclasses
		Method method = null;

		// Signature polymorphic?
		{
			Stream<Method> candidates = Arrays.stream(classFile.getMethods())
					.filter(i -> i.getName().equals(nameAndType[0]));
			Method inspectMethod = candidates.findFirst().orElse(null);
			if (inspectMethod != null) {
				if (inspectMethod.isSignaturePolymorphic()){
					Globals.getInst().execLogger.trace("Found a polymorphic method for " + nameAndType[0] + " in class "
							+ classFile.getClassName() + " ");
					return inspectMethod;
				}
			}
		}
		// In C
		method = classFile.getMethodByNameAndDescriptorOrNull(nameAndType[0], nameAndType[1]);
		if (method != null)
			return method;

		// ...or its superclasses
		method = resolveMethodInSuperclass(classFile, nameAndType[0], nameAndType[1]);

		if (method != null)
			return method;

		// Superinterfaces of C
		
		// Now on to interfaces
		LinkedList<String> superInterfaces = new LinkedList<>();
		LinkedList<String> exploreSuperClasses = new LinkedList<>();

		// add self as a starting class
		exploreSuperClasses.add(classFile.getName());

		// Trying the super interfaces recursively
		// wanting to find the maximally-specific superinterface methods
		// that match name and descriptor and that has neither its
		// ACC_PRIVATE flag nor its ACC_STATIC flag set

		while (!superInterfaces.isEmpty() || !exploreSuperClasses.isEmpty()) {

			final int ifaces = superInterfaces.size();
			for (int i = 0; i < ifaces; i++) {
				ClassFile classFile1 = null;
				try {
					classFile1 = this.classLoader.getClassAsClassFile(superInterfaces.pop());
					method = classFile1.getMethodByNameAndDescriptor(nameAndType[0], nameAndType[1]);
						Globals.getInst().execLogger
								.trace("Lookup of " + lookingFor + " in class " + classFile1.getName() + " succeeded.");
					break;
				} catch (MethodResolutionError e1) {
						Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " in interface class "
								+ classFile1.getClassName() + " unsuccessfull. Enqueueing its super class.");
					if (classFile1.getSuperClass() != 0)
						exploreSuperClasses
								.add(classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());
					for (int iface : classFile1.getInterfaces()) {
						superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
					}
				}
			}
			if (method != null)
				return method;

			final int sClasses = exploreSuperClasses.size();
			for (int i = 0; i < sClasses; i++) {
				ClassFile classFile1 = null;
				try {
					classFile1 = this.classLoader.getClassAsClassFile(exploreSuperClasses.pop());
					method = classFile1.getMethodByNameAndDescriptor(nameAndType[0], nameAndType[1]);
						Globals.getInst().execLogger
								.trace("Lookup of " + lookingFor + " in class " + classFile1.getName() + " succeeded.");
					break;
				} catch (MethodResolutionError e1) {
						Globals.getInst().execLogger
								.trace("Lookup of " + lookingFor + " in class " + classFile1.getClassName()
										+ " unsuccessfull. Enqueueing its super class and interfaces.");
					if (classFile1.getSuperClass() != 0)
						exploreSuperClasses
								.add(classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());
					for (int iface : classFile1.getInterfaces()) {
						superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
					}
				}
			}

			if (method != null)
				return method;
		}
									
		// illegalAccessError is being taken care of by the Instruction's checkAccess

			Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " failed.");
		throw new NoSuchMethodError("Method " + lookingFor + " could not be resolved for class " + classFile.getName()
				+ " or any of its superclasses or superinterfaces.");

	}
	
	public Method resolveMethodInSuperclass(ClassFile classFile, final String name, final String descriptor) throws ClassFileException {
		Method method = null;
		final String lookingFor = name + ":" + descriptor;

		while (classFile.getSuperClass() != 0) {

			classFile = this.classLoader
					.getClassAsClassFile(classFile.getConstantPool()[classFile.getSuperClass()].getStringValue());
			try {
				method = classFile.getMethodByNameAndDescriptor(name, descriptor);
				// if the method could be resolved, quit the loop

				Globals.getInst().execLogger
						.trace("Lookup of " + lookingFor + " in super class " + classFile.getName() + " succeeded.");
				break;
			} catch (MethodResolutionError e2) {
				// basically do nothing, but log on deep log levels
				Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " in super class " + classFile.getName()
						+ " unsuccessfull. Trying its super classes.");
			}
		}
		return method;
	}
	

	/**
	 * §5.4.3.4 Interface Method Resolution This is separate from resolveMethod to make it easier to comply with the
	 * differences in JVMS. (e.g. resolveMethod has to take care of polymorphic methods,too)
	 * 
	 * @param classFile
	 * @param nameAndType
	 * @return
	 * @throws ClassFileException
	 */
	public Method resolveMethodInterface(final ClassFile classFile, final String[] nameAndType)
			throws ClassFileException {
		final String lookingFor = nameAndType[0] + ":" + nameAndType[1];

		// According to JVMs8 § 5.4.3.3
		// Step 1
		if (!classFile.isAccInterface())
			throw new IncompatibleClassChangeError("Can only call resolveMethodInterface on an Interface");

		// Step 2: in C
		Method method = classFile.getMethodByNameAndDescriptorOrNull(nameAndType[0], nameAndType[1]);

		if (method != null)
			return method;

		// Step 3: Otherwise, if the class Object declares a method with the name and descriptor
		// specified by the interface method reference, which has its ACC_PUBLIC flag set
		// and does not have its ACC_STATIC flag set, method lookup succeeds.
		{
			ClassFile classFile2 = this.classLoader.getClassAsClassFile("java.lang.Object");
			method = classFile2.getMethodByNameAndDescriptorOrNull(nameAndType[0], nameAndType[1]);

			if (method != null && method.isAccPublic() && !method.isAccStatic())
				return method;
		}

		// Step 4

		// Now on to interfaces
		LinkedList<String> superInterfaces = new LinkedList<>();
		LinkedList<Method> tentativeMethods = new LinkedList<>();
		LinkedList<String> exploreSuperClasses = new LinkedList<>();

		// add self as a starting class
		exploreSuperClasses.add(classFile.getName());
		
		// Trying the super interfaces recursively
		// wanting to find the maximally-specific superinterface methods
		// that match name and descriptor and that has neither its
		// ACC_PRIVATE flag nor its ACC_STATIC flag set

		while (!superInterfaces.isEmpty() || !exploreSuperClasses.isEmpty()) {

			final int ifaces = superInterfaces.size();
			for (int i = 0; i < ifaces; i++) {
				ClassFile classFile1 = null;
				classFile1 = this.classLoader.getClassAsClassFile(superInterfaces.pop());
				Method method1 = classFile1.getMethodByNameAndDescriptorOrNull(nameAndType[0], nameAndType[1]);

				if (method1 != null && !method1.isAccPrivate() && !method1.isAccStatic()) {
					if (!method1.isAccAbstract()) {
						Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " in interfaceclass "
								+ classFile1.getName() + " succeeded.");
						return method1;
					} else {
						Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " in interfaceclass "
								+ classFile1.getName() + " : tentative method found.");
						tentativeMethods.add(method1);
					}

				} else {

					Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " in interfaceclass "
							+ classFile1.getClassName() + " unsuccessfull. Enqueueing its super class.");
					if (classFile1.getSuperClass() != 0)
						exploreSuperClasses
								.add(classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());

					for (int iface : classFile1.getInterfaces()) {
						superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
					}
				}
			}

			final int sClasses = exploreSuperClasses.size();
			for (int i = 0; i < sClasses; i++) {
				ClassFile classFile1 = null;
				classFile1 = this.classLoader.getClassAsClassFile(exploreSuperClasses.pop());
				if (classFile1.getSuperClass() != 0)
					exploreSuperClasses.add(classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());
				for (int iface : classFile1.getInterfaces()) {
					superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
				}
			}

		}

		if (!tentativeMethods.isEmpty()) {
			// arbitrarily chosen. Conditions should have been explored in the first place.
			return tentativeMethods.getFirst();
		}
		
		// illegalAccessError is being taken care of by the Instruction's checkAccess

		Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " failed.");
		throw new NoSuchMethodError("Method " + lookingFor + " could not be resolved for interfaceclass " + classFile.getName()
				+ " or any of its superinterfaces.");

	}

}
