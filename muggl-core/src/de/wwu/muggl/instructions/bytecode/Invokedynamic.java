package de.wwu.muggl.instructions.bytecode;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.general.Invoke;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileConstants;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.BootstrapMethod;
import de.wwu.muggl.vm.classfile.structures.constants.*;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.execution.ResolutionAlgorithms;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import org.apache.bcel.Const;
import org.apache.bcel.generic.*;

/**
 * Implementation of the instruction <code>invokedynamic</code>.
 *
 * @author Max Schulze
 */
public class Invokedynamic extends Invoke implements Instruction {
    private static final int BOOTSTRAP_MH_STANDARD_ARG_FOR_SAM_MT = 0;
    private static final int BOOTSTRAP_MH_STANDARD_ARG_FOR_TARGET_HANDLE = 1;
    private static final int BOOTSTRAP_MH_STANDARD_ARG_FOR_INSTANTIATED_MT = 2;

    /**
     * Increases with every generated object.
     */
    private static int staticLambdaCounter = 0;

    /**
     * Linkage: Caches a once-generated class for later reuse.
     */
    private ClassFile generatedJavaClass = null;

    /**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method
	 * that the instruction belongs to is supplied as an argument.
	 *
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized
	 *         successfully, most likely due to missing additional bytes. This might be caused by a
	 *         corrupt class file, or a class file of a more recent version than what can be handled.
	 */
	public Invokedynamic(AttributeCode code) throws InvalidInstructionInitialisationException {
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
	 */
	@Override
	protected ClassFile checkStaticMethod(Frame frame, String[] nameAndType,
			Method method, Object[] parameters) {
	    throw new UnsupportedOperationException("checkStaticMethod(...) not supported by Invokedynamic.");
	}

	/**
	 * Do nothing. (not supported by Indy.)
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method on.
	 */
	@Override
	protected void checkAccess(Frame frame, Method method, ClassFile objectrefClassFile) {
        throw new UnsupportedOperationException("checkAccess(...) not supported by Invokedynamic.");

    }

	/**
	 * Do nothing. (not supported by Indy.)
	 *
	 * @param frame The currently executed frame.
	 * @param method The resolved method.
	 * @param methodClassFile The method's class file according to its name and descriptor.
	 * @param objectrefClassFile The {@link ClassFile} of the object reference to invoke the method
	 *        on.
	 * @return The selected method. Might be the method specified.
	 */
	@Override
	protected Method selectMethod(Frame frame, Method method, ClassFile methodClassFile,
			ClassFile objectrefClassFile) {
        throw new UnsupportedOperationException("selectMethod(...) not supported by Invokedynamic.");
    }

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "invokedynamic";
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
					"3Error while executing instruction " + getName()
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
		return resoluton.resolveMethod(methodClassFile, nameAndType);
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		// UnsatisfiedLinkError is not thrown by this vm implementation.
        return new String[]{ "java.lang.AbstractMethodError",
                                    "java.lang.ExceptionInInitializerError",
                                    "java.lang.IllegalAccessError",
                                    "java.lang.IncompatibleClassChangeError",
                                    "java.lang.InstantiationException",
                                    "java.lang.InvocationTargetException",
                                    "java.lang.NoClassDefFoundError",
                                    "java.lang.NoSuchMethodError",
                                    "java.lang.NullPointerException"};
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

	 * @param frame The currently executed frame.
	 * @param symbolic Toggles whether the execution is symbolic, or not.
	 * @throws ClassFileException On fatal errors loading or parsing a class file.
	 * @throws ExecutionException In case of fatal problems during the execution.
	 * @throws VmRuntimeException If runtime exceptions occur.
	 */
	@SuppressWarnings("unused")
	protected void invoke(Frame frame, boolean symbolic) throws ClassFileException,
			ExecutionException, VmRuntimeException {
		// Preparations.
		final Stack<Object> stack = frame.getOperandStack();
		final int index = this.otherBytes[0] << ONE_BYTE | this.otherBytes[1];
		final Constant constant = frame.getConstantPool()[index];

		// check validity of constant pool entry
		if (!(constant instanceof ConstantInvokeDynamic)) {
			throw new ExecutionException(
					"1Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index " + constant.getStringValue()
						+ "to be a symbolic reference to a call site specifier.");
		}
        final ConstantInvokeDynamic constID = (ConstantInvokeDynamic) constant;

		// Get adapter from cache, or
        if (this.generatedJavaClass == null) {
            // Generate bytecode for the adapter and load it right away.
            this.generatedJavaClass = generateAndLoadAdapterClass(frame, constID);
        }

        // Instantiate the adapter and push it to the operand stack.
        Objectref anObjectref = frame.getVm().getAnObjectref(this.generatedJavaClass);
        stack.push(anObjectref);

    }

    /**
     * Generate and load an adapter class for invokedynamic.
     *
     * The following outlines the generally correct procedure. This method implements a heavily simplified version that
     * may only be correct in the default case of bootstrapMH that is asserted below.
     *
     * GENERAL PROCEDURE
     * (See also: http://cr.openjdk.java.net/~vlivanov/talks/2015-Indy_Deep_Dive.pdf).
     *
     * - construct a java.lang.invoke.MethodHandle from the methodHandle and call invoke on it:
     * "For invokedynamic, the bootstrap specifier is resolved into a method handle and zero or more extra constant
     * arguments. (These are all drawn from the constant pool.) The name and signature are pushed on the stack,
     * along with the extra arguments and a MethodHandles.Lookup parameter to reify the requesting class,
     * and the bootstrap method handle is invoked." (https://wiki.openjdk.java.net/display/HotSpot/Method+handles+and+invokedynamic)
     * result shall be a reference to an object (of class or subclass .CallSite) <- The Call Site Object
     * CallSite can be cached!
     *
     * - then get the target of the callSiteObject (callSiteObject.getTarget())
     *
     * - and execute invokevirtual java.lang.invoke.MethodHandle.invokeExact on it (descriptor of call Site specifier)
     * - a) (according to mxs) call invokeExact (however, maybe not here? maybe from within lambda...)
     * - b) result here should be the instantiated Lambda class that is pushed to the stack ("linkage"). That result will be popped
     *      from the stack for the subsequent invocation that uses this lambda.
     *
     * @param frame currently executed frame
     * @param constant constant describing the current instruction
     * @return a generated ClassFile that has been loaded by the class loader.
     * @throws VmRuntimeException if targets cannot be resolved
     * @throws ClassFileException if classes cannot be loaded
     */
    private ClassFile generateAndLoadAdapterClass(Frame frame, ConstantInvokeDynamic constant) throws VmRuntimeException, ClassFileException {
        // resolve reference to MethodHandle, MethodType and arguments from the bootstrap section
        final BootstrapMethod bootstrapMethod = frame.getMethod().getClassFile().getBootstrapMethods().getBootstrapMethods()[constant.getBootstrapMethodAttrIndex()];
        // on error with bootstrapMethod resolution -> BootstrapMethodError wrapping E

        final ConstantMethodHandle bootstrapMH = (ConstantMethodHandle) frame.getConstantPool()[bootstrapMethod.getBootstrapMethodRef()];
        // We only consider standard Java 8 programs until now, which seem to generate a constant bootstrapMH signature for all programs.
        assert(bootstrapMH.getValue().equals(ConstantMethodHandle.BOOTSTRAP_MH_STANDARD_METAFACTORY_SIGNATURE));
        final Constant[] bootstrapArgConst = new Constant[bootstrapMethod.getNumBootstrapArguments()];
        for (int i = 0; i < bootstrapMethod.getNumBootstrapArguments(); i++) {
            bootstrapArgConst[i] = frame.getConstantPool()[bootstrapMethod.getBootstrapArguments()[i]];
        }

        // First, try to resolve the target method that needs to be encapsulated into a Functional Interface (adapter pattern).
        assert(bootstrapArgConst.length > BOOTSTRAP_MH_STANDARD_ARG_FOR_INSTANTIATED_MT);
        assert(bootstrapArgConst[BOOTSTRAP_MH_STANDARD_ARG_FOR_TARGET_HANDLE] instanceof ConstantMethodHandle);
        assert(bootstrapArgConst[BOOTSTRAP_MH_STANDARD_ARG_FOR_SAM_MT] instanceof ConstantMethodType);
        assert(bootstrapArgConst[BOOTSTRAP_MH_STANDARD_ARG_FOR_INSTANTIATED_MT] instanceof ConstantMethodType);
        // Method handle contains verb (e.g. invokestatic) and class+method that is to be invoked.
        final ConstantMethodHandle targetMethodHandle = (ConstantMethodHandle) bootstrapArgConst[BOOTSTRAP_MH_STANDARD_ARG_FOR_TARGET_HANDLE];
        // Methodref contains class+method that is to be invoked.
        final ConstantFieldInterfaceMethod targetMethodref = (ConstantFieldInterfaceMethod) targetMethodHandle.getReferencedConstant();


        final ResolutionAlgorithms resolve = new ResolutionAlgorithms(frame.getVm().getClassLoader());
        final Method targetMethod;
        try {
            ClassFile targetMethodrefClassFile = frame.getVm().getClassLoader().getClassAsClassFile(targetMethodref.getClassName());
            if (targetMethodHandle.getReferenceKind() == ClassFileConstants.ReferenceKind.REF_invokeInterface) {
                targetMethod = resolve.resolveMethodInterface(targetMethodrefClassFile, targetMethodref.getNameAndTypeInfo());
            } else {
                assert(targetMethodHandle.getReferenceKind() == ClassFileConstants.ReferenceKind.REF_invokeStatic);
                targetMethod = resolve.resolveMethod(targetMethodrefClassFile, targetMethodref.getNameAndTypeInfo());
            }
        } catch (ClassFileException e) {
            throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoClassDefFoundError", "Could not resolve class of target method in invokedynamic. " +
                    e.getMessage()));
        } catch (NoSuchMethodError e) {
            throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoSuchMethodError", "Could not resolve target method in invokedynamic. " +
                    e.getMessage()));
        }

        final String[] lambdaMethodNameAndLambdaInterfaceType = getNameAndType(constant);
        final String generatedClassName =  frame.getMethod().getClassFile().getName() + "$$Lambda$" + (staticLambdaCounter++);

        final ClassFile generatedJavaClass;
        try {
            final File tempFile = File.createTempFile(generatedClassName, ".class");
            tempFile.deleteOnExit();

            generateAdapterClass((ConstantMethodType) bootstrapArgConst[BOOTSTRAP_MH_STANDARD_ARG_FOR_SAM_MT],
                    (ConstantMethodType) bootstrapArgConst[BOOTSTRAP_MH_STANDARD_ARG_FOR_INSTANTIATED_MT],
                    targetMethod, lambdaMethodNameAndLambdaInterfaceType[0], lambdaMethodNameAndLambdaInterfaceType[1],
                    generatedClassName, tempFile);

            generatedJavaClass = ClassFile.loadFromGeneratedClass(frame.getVm().getClassLoader(), tempFile);
            frame.getVm().getClassLoader().addToClassCache(generatedJavaClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoClassDefFoundError e) {
            throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NoClassDefFoundError", "Could not resolve class of target method in invokedynamic. " +
                    e.getMessage()));
        }
        return generatedJavaClass;
    }

    /**
     * Generate bytecode for a class implementing the declared functional interface.
     *
     * @param samMethodType Parameter types of the adapter method (cf. https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/LambdaMetafactory.html#metafactory-java.lang.invoke.MethodHandles.Lookup-java.lang.String-java.lang.invoke.MethodType-java.lang.invoke.MethodType-java.lang.invoke.MethodHandle-java.lang.invoke.MethodType-)
     * @param instantiatedMethodType Expected parameter types of calls to the adapter method (cf. https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/LambdaMetafactory.html#metafactory-java.lang.invoke.MethodHandles.Lookup-java.lang.String-java.lang.invoke.MethodType-java.lang.invoke.MethodType-java.lang.invoke.MethodHandle-java.lang.invoke.MethodType-)
     * @param targetMethod Method that is invoked by the adapter
     * @param lambdaMethodName Name of the adapter method
     * @param lambdaMethodSignature Signature of the implemented functional interface
     * @param generatedClassName Generated class name
     * @param tempFile File where class will be dumped to
     * @throws IOException If the temporary file cannot be written
     */
    private static void generateAdapterClass(ConstantMethodType samMethodType, ConstantMethodType instantiatedMethodType, Method targetMethod,
                                             String lambdaMethodName, String lambdaMethodSignature, String generatedClassName, File tempFile)
            throws IOException {
        /* Assume that signature is always of a form similar to "()Ljava/util/function/ToLongFunction;";
         * i.e. specifying parameters of [0], together with the expected functional interface type.
         * ("The parameter types represent the types of capture variables; the return type is the interface to implement.") */
        // Extract the interface name.
        final int left = lambdaMethodSignature.lastIndexOf(")L");
        final int right = lambdaMethodSignature.lastIndexOf(";");
        final String lambdaInterfaceName = lambdaMethodSignature.substring(left+2, right);

        // Signature of the method from the functional interface that must be implemented.
        final Type[] samArgTypes = Type.getArgumentTypes(samMethodType.getValue());
        final Type samReturnType = Type.getReturnType(samMethodType.getValue());
        // Expected types on invocation of that method.

        // Create method lambdaMethodNameAndLambdaInterfaceType[0] in lambdaObject, wrapping targetMethod.
        // Generate a class name that will not collide ever, while being meaningful.
        final ClassGen classgen = new ClassGen(generatedClassName, "java.lang.Object", "<generated>",
                Const.ACC_PUBLIC | Const.ACC_SUPER | Const.ACC_SYNTHETIC,
                new String[] { lambdaInterfaceName });
        classgen.setMinor(Const.MINOR_1_8);
        classgen.setMajor(Const.MAJOR_1_8);
        final InstructionFactory insf = new InstructionFactory(classgen);
        final InstructionList insl = new InstructionList();

        // Prepare invoke: Push args.
        int idx = 1; // 0 is this, therefore skip 0.
        for (Type t : samArgTypes) {
            insl.append(InstructionFactory.createLoad(t, idx));
            // Increment idx according to the size occupied by t.
            idx += t.getSize();
        }
        // Invoke.
        if (targetMethod.getName().equals(VmSymbols.OBJECT_INITIALIZER_NAME)) {
            insl.append(insf.createNew(targetMethod.getClassFile().getName()));
            insl.append(insf.createDup(1));
            insl.append(insf.createInvoke(targetMethod.getClassFile().getName(), targetMethod.getName(),
                    Type.getReturnType(targetMethod.getDescriptor()),
                    Type.getArgumentTypes(targetMethod.getDescriptor()), Const.INVOKESPECIAL));
        } else {
            short kind;
            if (targetMethod.getClassFile().isAccInterface()) {
                kind = Const.INVOKEINTERFACE;
            } else {
                kind = Const.INVOKESTATIC;
            }
            insl.append(insf.createInvoke(targetMethod.getClassFile().getName(), targetMethod.getName(),
                    Type.getReturnType(targetMethod.getDescriptor()),
                    Type.getArgumentTypes(targetMethod.getDescriptor()), kind));
        }
        // Add return to insl.
        if (!samReturnType.equals(Type.VOID)) {
            insl.append(InstructionFactory.createReturn(samReturnType));
        }
        // Put insl into method.
        final MethodGen methodgen = new MethodGen(Const.ACC_PUBLIC | Const.ACC_SYNTHETIC,
                samReturnType, samArgTypes,
                null, lambdaMethodName, generatedClassName, insl, classgen.getConstantPool());

        // Dump class.
        classgen.addMethod(methodgen.getMethod());
        classgen.getJavaClass().dump(tempFile);
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
					"2Error while executing instruction " + getName()
						+ ": Expected runtime constant pool item at index " + constant.getValue()
						+ "to be a symbolic reference to a method.");
		}
		
		return classLoader.getClassAsClassFileOrArrays(
				((ConstantInterfaceMethodref) constant).getClassName());
	}

	/**
	 * Get name and descriptor from a constant_methodref.
	 *
	 * @param constant A constant_methodref.
	 * @return Name and descriptor for the constant_methodref.
	 */
    private String[] getNameAndType(ConstantInvokeDynamic constant)  {
		return constant.getNameAndTypeInfo();
	}
}
