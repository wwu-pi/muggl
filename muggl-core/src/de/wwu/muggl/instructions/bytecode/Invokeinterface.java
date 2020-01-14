package de.wwu.muggl.instructions.bytecode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.general.Invoke;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantInterfaceMethodref;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;
import de.wwu.muggl.vm.loading.MugglClassLoader;

/**
 * Implementation of the instruction <code>invokeinterface</code>.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-12-15
 */
public class Invokeinterface extends Invoke implements Instruction {

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method
	 * that the instruction belongs to is supplied as an argument.
	 *
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized
	 *         successfully, most likely due to missing additional bytes. This might be caused by a
	 *         corrupt class file, or a class file of a more recent version than what can be handled.
	 */
	public Invokeinterface(AttributeCode code) throws InvalidInstructionInitialisationException {
		super(code);
	}

	/**
	 * Make sure the method is neither the instance initializer nor the static initializer. Check
	 * that the third additional byte is zero. Set the object reference to invoke the method on as
	 * the first parameter and return its class file.
	 *
	 * @param frame The currently executed frame.
	 * @param nameAndType The name and the descriptor of the method.
	 * @param method The resolved method.
	 * @param parameters The (yet unfilled) array of parameters.
	 * @return The {@link ClassFile} of the object reference to invoke the method on.
	 * @throws ExecutionException If an unexpected problem is found that does not throw a runtime
	 *         exception but forces the virtual machine to halt.
	 * @throws VmRuntimeException If an unexpected condition it met and a runtime exception is
	 *         thrown.
	 */
	@Override
	protected ClassFile checkStaticMethod(Frame frame, String[] nameAndType,
			Method method, Object[] parameters) throws ExecutionException, VmRuntimeException {
		
		checkNoInstanceInit(method);

		/*
		 * The third additional byte is ignored (redundant count information). Unexpected exception:
		 * The fourth additional byte must be zero.
		 */
		if (this.otherBytes[3] != 0)
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The fourth operand byte must be zero.");

		// Fetch the object reference to invoke the method on.
		ReferenceValue objectref = (ReferenceValue) frame.getOperandStack().pop();
		parameters[0] = objectref;

		// Runtime exception: objectref is null.
		if (objectref == null) throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException", "checkstatic Invokeinterface" +  nameAndType[0]+" "+nameAndType[1]));

		// Unexpected exception: objectref is not a constant_class.
		if (!(objectref instanceof Objectref)) throw new ExecutionException("Objectref must be a reference to a Class.");

		// Fetch the class of objectref and return it.
		return objectref.getInitializedClass().getClassFile();
	}

	/**
	 * Do nothing.
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method on.
	 */
	@Override
	protected void checkAccess(Frame frame, Method method, ClassFile objectrefClassFile) { }

	/**
	 * Select the actual method for invocation, make sure it is not abstract and check that is is
	 * public.
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param methodClassFile The method's class file according to its name and descriptor.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method
	 *        on.
	 * @return The selected method. Might be the method specified.
	 * @throws ClassFileException If a required class file cannot be loaded.
	 * @throws VmRuntimeException If an unexpected condition it met and a runtime exception is
	 *         thrown.
	 */
	@Override
	protected Method selectMethod(Frame frame, Method method, ClassFile methodClassFile, ClassFile objectrefClassFile)
			throws ClassFileException, VmRuntimeException {
        Method selMethod = selectMostSpecificImplementation(frame, method, objectrefClassFile);

        // Has the method been selected?
		if (selMethod == null)
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoSuchMethodError",
					"The method " + method.getName() + " to be invoked with " + getName() + " was not found."));

		if (selMethod.isAccStatic() || selMethod.isAccPrivate())
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IncompatibleClassChangeError",
					"The method " + selMethod.getName() + " to be invoked with " + getName()
							+ " must not be static or private."));

		// Is it abstract?
		if (selMethod.isAccAbstract())
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.AbstractMethodError",
					"The method " + method.getFullNameWithParameterTypesAndNames() + " to be invoked with " + getName() + " must not be abstract."));

		// Is it public?
		if (!selMethod.isAccPublic())
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.IllegalAccessError",
					"The method " + method.getName() + " to be invoked with " + getName() + " must be public"));

		// Return the selected method.
		return selMethod;
	}

    private Method selectMostSpecificImplementation(Frame frame, Method method, ClassFile objectrefClassFile) throws ClassFileException {
        boolean methodSelected = false;

        // According to JVMs8 invokeinterface
        // Step 1: in C
        Method selMethod = objectrefClassFile.getMethodByNameAndDescriptorOrNull(method.getName(), method.getDescriptor());

        if (selMethod != null)
            methodSelected = true;

        // Step 2: Superclasses of C
        if (!methodSelected) {
            selMethod = new ResolutionAlgorithms(frame.getVm().getClassLoader())
                    .resolveMethodInSuperclass(objectrefClassFile, method.getName(), method.getDescriptor());
            if (selMethod != null)
                methodSelected = true;

        }

        if (!methodSelected) {

            // Now on to interfaces
            LinkedList<String> superInterfaces = new LinkedList<>();
            LinkedList<Method> tentativeMethods = new LinkedList<>();
            LinkedList<String> exploreSuperClasses = new LinkedList<>();

            // add self as a starting class
            exploreSuperClasses.add(objectrefClassFile.getName());

            // Trying the super interfaces recursively
            // wanting to find the maximally-specific superinterface methods
            // that match name and descriptor and that has neither its
            // ACC_PRIVATE flag nor its ACC_STATIC flag set

            while (!superInterfaces.isEmpty() || !exploreSuperClasses.isEmpty()) {
                final int ifaces = superInterfaces.size();
                for (int i = 0; i < ifaces; i++) {
                    ClassFile classFile1 = null;
                    classFile1 = frame.getVm().getClassLoader().getClassAsClassFile(superInterfaces.pop());
                    Method method1 = classFile1.getMethodByNameAndDescriptorOrNull(method.getName(),
                            method.getDescriptor());

                    if (method1 != null && !method1.isAccPrivate() && !method1.isAccStatic()) {
                        selMethod = method1;
                        methodSelected = true;
                        Globals.getInst().execLogger.trace("Lookup of " + method.getName() + " in interfaceclass "
                                + classFile1.getName() + " succeeded.");

                    } else {
                        for (int iface : classFile1.getInterfaces()) {
                            superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
                        }
                    }
                }

                final int sClasses = exploreSuperClasses.size();
                for (int i = 0; i < sClasses; i++) {
                    ClassFile classFile1 = null;
                    classFile1 = frame.getVm().getClassLoader().getClassAsClassFile(exploreSuperClasses.pop());
                    if (classFile1.getSuperClass() != 0)
                        exploreSuperClasses.add(classFile1.getConstantPool()[classFile1.getSuperClass()].getStringValue());
                    for (int iface : classFile1.getInterfaces()) {
                        superInterfaces.add(classFile1.getConstantPool()[iface].getStringValue());
                    }
                }

            }
        }
        return selMethod;
    }

    protected List<Method> selectMethodsForNondeterministicInvocation(Frame frame, Method method, ClassFile methodClassFile, ClassFile objectrefClassFile)
        throws ClassFileException {
        ArrayList<Method> implementations = new ArrayList<>();
        Method mostSpecificFromSupertypes = selectMostSpecificImplementation(frame, method, objectrefClassFile);
        if (mostSpecificFromSupertypes != null) {
            implementations.add(mostSpecificFromSupertypes);
        }

        // In class path, find all classes that provide an implementation; add them.
        MugglClassLoader classLoader = frame.getVm().getClassLoader();
        // Extract classes because iterating over them will modify classloader state.
        List<ClassFile> loadedClasses = new ArrayList<>(classLoader.getLoadedClasses().values());
        loadedClasses.forEach(type -> {
            if (type.isSubtypeOf(objectrefClassFile)) {
                // TODO Check for own method implementation. If `type' is abstract, add the direct subtype.
                Method implementationOrNull = type.getMethodByNameAndDescriptorOrNull(method.getName(), method.getDescriptor());
                if (implementationOrNull != null && !implementationOrNull.isAccAbstract()) {
                    if (type.isAccAbstract() || type.isAccInterface()) {
                        // TODO add direct subtypes.
                    } else {
                        implementations.add(implementationOrNull);
                    }
                }
            }
        });

        // Filter the list of implementations w. r. t. additional criteria.
        return implementations.stream().filter(impl -> {
            // The method to be invoked with Invokeinterface must not be static or private.
            if (impl.isAccStatic() || impl.isAccPrivate())
                return false;

            // The method to be invoked with Invokeinterface must not be abstract.
            if (impl.isAccAbstract())
                return false;

            // The method to be invoked with Invokeinterface must be public.
            if (!impl.isAccPublic())
                return false;

            // Non-determinism and Native do not go very well, let's exclude them.
            if (impl.isAccNative())
                return false;

            return true;
        }).collect(Collectors.toList());
    }

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "invokeinterface";
	}

	/**
	 * Get the number of other bytes for this instruction.
	 * @return The number of other bytes.
	 */
	@Override
	public int getNumberOfOtherBytes() {
		return 4;
	}

	/**
	 * Get a reference to the Method invoked by this instruction.
	 *
	 * @param constantPool The constant pool.
	 * @param classLoader The current class loader.
	 * @return The resolved Method.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException If the method is resolved to be the static initializer or the
	 *         fourth additional byte is not zero or the initializer or the constant pool does not
	 *         have a symbolic reference to a method at the position encoded in the bytecode.
	 * @throws NoSuchMethodError If the Method could not be found.
	 */
	public Method getInvokedMethod(Constant[] constantPool, MugglClassLoader classLoader)
		throws ClassFileException, ExecutionException {
		int index = this.otherBytes[0] << ONE_BYTE | this.otherBytes[1];
		Constant constant = constantPool[index];
		if (!(constant instanceof ConstantInterfaceMethodref))
			throw new ExecutionException(
					"Error while executing instruction " + getName()
							+ ": Expected runtime constant pool item at index to be a symbolic reference to a method.");

		/*
		 * The third additional byte is ignored (redundant count information). Unexpected exception:
		 * The fourth additional byte must be zero.
		 */
		if (this.otherBytes[3] != 0)
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The fourth operand byte must be zero.");

		// Get the name and the descriptor.
		String[] nameAndType = ((ConstantInterfaceMethodref) constant).getNameAndTypeInfo();
		ClassFile methodClassFile = classLoader.getClassAsClassFileOrArrays(((ConstantInterfaceMethodref) constant).getClassName());
		if (nameAndType[0].equals(VmSymbols.OBJECT_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the instance initialization method.");
		if (nameAndType[0].equals(VmSymbols.CLASS_INITIALIZER_NAME))
			throw new ExecutionException("Error while executing instruction " + getName()
					+ ": The Method must not be the class or interface initialization method.");

		// Try to resolve method from this class.
		ResolutionAlgorithms resoluton = new ResolutionAlgorithms(classLoader);
		return resoluton.resolveMethodInterface(methodClassFile, nameAndType);
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		// UnsatisfiedLinkError is not thrown by this vm implementation.
		String[] exceptionTypes = { "java.lang.AbstractMethodError",
									"java.lang.ExceptionInInitializerError",
									"java.lang.IllegalAccessError",
									"java.lang.IncompatibleClassChangeError",
									"java.lang.InstantiationException",
									"java.lang.InvocationTargetException",
									"java.lang.NoClassDefFoundError",
									"java.lang.NoSuchMethodError",
									"java.lang.NullPointerException"};
		return exceptionTypes;
	}

	/**
	 * Get the number of elements that will be popped from the stack when this instruction is
	 * executed.
	 *
	 * @return The number of elements that will be popped from the stack.
	 */
	public int getNumberOfPoppedElements() {
		return 1;
	}
	
	/**
	 * Get name and descriptor from a constant_interfacemethodref.
	 *
	 * @param constant A constant_interfacemethodref.
	 * @return Name and descriptor for the constant_interfacemethodref.
	 * @throws ExecutionException In case of any other problems.
	 */
	@Override
	protected String[] getNameAndType(Constant constant) throws ExecutionException {
		if (!(constant instanceof ConstantInterfaceMethodref)) {
			throw new ExecutionException(
					"Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index "
						+ "to be a symbolic reference to a method.");
		}
		
		return ((ConstantInterfaceMethodref) constant).getNameAndTypeInfo();
	}
	
	/**
	 * Get the corresponding class file for a constant_interfacemethodref.
	 *
	 * @param constant A constant_interfacemethodref.
	 * @param classLoader The class loader.
	 * @return The corresponding class file for a constant_interfacemethodref.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of any other problems.
	 */
	@Override
	protected ClassFile getMethodClassFile(Constant constant, MugglClassLoader classLoader) throws ClassFileException, ExecutionException {
		if (!(constant instanceof ConstantInterfaceMethodref)) {
			throw new ExecutionException(
					"Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index "
						+ "to be a symbolic reference to a method.");
		}
		
		return classLoader.getClassAsClassFileOrArrays(
				((ConstantInterfaceMethodref) constant).getClassName());
	}

}
