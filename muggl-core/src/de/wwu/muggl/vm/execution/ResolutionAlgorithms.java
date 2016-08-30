package de.wwu.muggl.vm.execution;

import java.util.LinkedList;

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
			if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Lookup of field " + nameAndType[1] + " in class " + nameAndType[0] + " unsuccessfull. Trying its super classes.");
			// unsuccessful - trying the super classes recursively
			while (classFile.getSuperClass() != 0) {
				classFile = this.classLoader.getClassAsClassFile(classFile.getConstantPool()[classFile.getSuperClass()].getStringValue());
				try {
					field = classFile.getFieldByNameAndDescriptor(nameAndType[0], nameAndType[1]);
					// if the Field could be resolved, quit the loop
					if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Lookup of " + nameAndType[1] + " in super class " + classFile.getName() + " succeeded.");
					break;
				} catch (FieldResolutionError e2) {
					// basically do nothing, but log on deep log levels
					if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Lookup of " + nameAndType[1] + " in super class " + classFile.getName() + " unsuccessfull. Trying its super classes.");
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
		// FIXME: this could be called on interface or method
		
		// FIXME: each class can have *at most one superclass* and mutliple interfaces (see class file definition!)
		
//		if (classFile.isAccInterface()) throw new IncompatibleClassChangeError("Cannot call resolveMethod on an Interface");
		
		Method method = null;
		try {
			method = classFile.getMethodByNameAndDescriptor(nameAndType[0], nameAndType[1]);
		} catch (MethodResolutionError e) {
			final String lookingFor = nameAndType[0] + ":" + nameAndType[1];			
			if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("1Lookup of " + lookingFor + " in class " + classFile.getClassName() + " unsuccessfull. Trying its super class.");

			// Unsuccessful - trying the super classes recursively.
			method = resolveMethodInSuperclass(classFile, nameAndType);

			// Now on to interfaces
			if (method == null) {
				LinkedList<String> superInterfaces = new LinkedList<>();
				LinkedList<String> exploreSuperClasses = new LinkedList<>();

				// add self as a starting class
				exploreSuperClasses.add(classFile.getName());

				// Trying the super interfaces recursively
				// wanting to find the maximally-specific superinterface methods
				// that match name and descriptor and that has neither its
				// ACC_PRIVATE
				// flag nor its ACC_STATIC flag set

				while (!superInterfaces.isEmpty()
						|| !exploreSuperClasses.isEmpty()) {

					final int ifaces = superInterfaces.size();
					for (int i = 0; i < ifaces; i++) {
						ClassFile classFile1 = null;
						try {
							classFile1 = this.classLoader
									.getClassAsClassFile(superInterfaces.pop());
							method = classFile1.getMethodByNameAndDescriptor(
									nameAndType[0], nameAndType[1]);
							if (Globals.getInst().execLogger.isTraceEnabled())
								Globals.getInst().execLogger.trace("Lookup of "
										+ lookingFor + " in class "
										+ classFile1.getName() + " succeeded.");
							break;
						} catch (MethodResolutionError e1) {
							if (Globals.getInst().execLogger.isTraceEnabled())
								Globals.getInst().execLogger.trace("Lookup of "
										+ lookingFor + " in interface class "
										+ classFile1.getClassName()
										+ " unsuccessfull. Enqueueing its super class.");
							if (classFile1.getSuperClass() != 0)
								exploreSuperClasses.add(
										classFile1.getConstantPool()[classFile1
												.getSuperClass()]
														.getStringValue());
							for (int iface : classFile1.getInterfaces()) {
								superInterfaces
										.add(classFile1.getConstantPool()[iface]
												.getStringValue());
							}
						}
					}
					if (method != null)
						break;

					final int sClasses = exploreSuperClasses.size();
					for (int i = 0; i < sClasses; i++) {
						ClassFile classFile1 = null;
						try {
							classFile1 = this.classLoader.getClassAsClassFile(
									exploreSuperClasses.pop());
							method = classFile1.getMethodByNameAndDescriptor(
									nameAndType[0], nameAndType[1]);
							if (Globals.getInst().execLogger.isTraceEnabled())
								Globals.getInst().execLogger.trace("Lookup of "
										+ lookingFor + " in class "
										+ classFile1.getName() + " succeeded.");
							break;
						} catch (MethodResolutionError e1) {
							if (Globals.getInst().execLogger.isTraceEnabled())
								Globals.getInst().execLogger.trace("Lookup of "
										+ lookingFor + " in class "
										+ classFile1.getClassName()
										+ " unsuccessfull. Enqueueing its super class and interfaces.");
							if (classFile1.getSuperClass() != 0)
								exploreSuperClasses.add(
										classFile1.getConstantPool()[classFile1
												.getSuperClass()]
														.getStringValue());
							for (int iface : classFile1.getInterfaces()) {
								superInterfaces
										.add(classFile1.getConstantPool()[iface]
												.getStringValue());
							}
						}
					}

					if (method != null)
						break;
				}
			}

			// Successful lookup?.
			if (method == null) {
				if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " failed.");
				throw new NoSuchMethodError("Method " + lookingFor + " could not be resolved for class " + classFile.getName() + " or any of its superclasses or superinterfaces.");
			}
		}
		return method;
	}
	
	private Method resolveMethodInSuperclass(ClassFile classFile, String[] nameAndType) throws ClassFileException {
		Method method = null;
		final String lookingFor = nameAndType[0] + ":" + nameAndType[1];
		
		while (classFile.getSuperClass() != 0) {
			
			classFile = this.classLoader.getClassAsClassFile(classFile.getConstantPool()[classFile.getSuperClass()].getStringValue()) ;
			try {
				method = classFile.getMethodByNameAndDescriptor(nameAndType[0], nameAndType[1]);
				// if the method could be resolved, quit the loop
				if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " in super class " + classFile.getName() + " succeeded.");
				break;
			} catch (MethodResolutionError e2) {
				// basically do nothing, but log on deep log levels
				if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Lookup of " + lookingFor + " in super class " + classFile.getName() + " unsuccessfull. Trying its super classes.");
			}
		}
		return method;
	}
	

}
