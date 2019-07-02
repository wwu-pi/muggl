package de.wwu.muggl.vm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;
import java.util.stream.Collectors;

import de.wwu.muggl.vm.execution.NativeJavaLangSystem;
import de.wwu.muggl.vm.execution.NativeSunMiscVM;
import org.apache.log4j.Level;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.MethodResolutionError;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.vm.JavaClasses.java_lang_thread;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.Limitations;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.classfile.structures.UndefinedValue;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.execution.MugglToJavaConversion;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializationException;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.PrimitiveWrappingImpossibleException;
import de.wwu.muggl.vm.initialization.ThrowableGenerator;
import de.wwu.muggl.vm.initialization.strings.StringCache;
import de.wwu.muggl.vm.loading.MugglClassLoader;
import de.wwu.muggl.vm.threading.Monitor;

/**
 * This abstract class represents a virtual machine for the execution of java bytecode.
 * It has methods offering general virtual machine functionality. They can be utilized
 * by concrete virtual machine implementations.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-07-15
 */
public abstract class VirtualMachine extends Thread {
	// Fields.
	/**
	 * Mark whether this SymbolicalVirtualMachine has been finalized, yet.
	 */
	protected boolean finalized;

	/**
	 * The application this vm belongs to.
	 */
	protected Application application;
	/**
	 * The initial class loader used.
	 */
	protected MugglClassLoader classLoader;
	/**
	 * The class file the initial method belongs to.
	 */
	private ClassFile classFile;
	/**
	 * The initially executed method.
	 */
	protected Method initialMethod;
	/**
	 * Indicates whether the vm runs in step-by-step mode.
	 */
	private boolean stepByStepMode;
	/**
	 * Indicates whether the next execution step is ready.
	 */
	protected boolean nextStepReady;
	/**
	 * Indicates whether execution has been paused.
	 */
	private boolean pauseExecution;
	/**
	 * A reference to the string cache.
	 */
	private StringCache stringCache;
	/**
	 * The generator of objects of type java.lang.Throwable.
	 */
	private ThrowableGenerator throwableGenerator;

	// Additional fields.
	/**
	 * The vm stack.
	 */
	protected Stack<Object> stack;
	private boolean errorOccured = false;
	private String errorMessage;
	/**
	 * Reference to an object returned from execution.
	 */
	protected Object returnedObject;
	/**
	 * Indicates whether execution has returned an value.
	 */
	protected boolean hasAReturnValue = false;
	/**
	 * Indicates whether execution lead to an uncaught exception being thrown.
	 */
	protected boolean threwAnUncaughtException;
	/**
	 * The currently executed frame.
	 */
	protected Frame currentFrame;
	/**
	 * The pc value of the currently executed frame.
	 */
	protected int pc;
	/**
	 * Indicates whether the vm is supposed to return from the current execution of a frame.
	 */
	protected boolean returnFromCurrentExecution = false;
	/**
	 * Indicates whether the next frame is already loaded for execution.
	 */
	protected boolean nextFrameIsAlreadyLoaded;
	private long nextInitializedClassId;
	private int ignoreInterruption;
	private boolean runUntilNoOfInstructionsReached;
	private long executedInstructionsTarget;
	
	/**
	 * Whether to enable assertions. Only one general switch for all.
	 */
	private boolean assertionEnabled = true;

	// Statistical Fields.
	/**
	 * The number of frames so far executed.
	 */
	protected long executedFrames;
	/**
	 * The number of instructions executed.
	 */
	protected long executedInstructions;

	protected boolean universeSetupFinished = false;
	// Helpers for debugging. See method fillDebugStackTraces
	public String debugStackTraceJavaVM;
	public String debugStackTraceMugglVM;
	
	/**
	 * Handle the VM's thread as the "main"-thread's "OS"-container
	 */
	private static Objectref threadObj = null;

	/**
	 * System properties for the Muggl VM. Extracted from the mandatory properties in the Java API
	 */
	// FIXME: correctly implement these
	public Hashtable<String, String> systemProperties = new Hashtable<String, String>() {
		private static final long serialVersionUID = 3991769537861501908L;
		{
			put("java.vendor", "Muggl Team");
			put("java.vendor.url", "http://wwu.de");
			put("java.vm.vendor", "Muggl Team");
			put("java.class.version", "52.0");
			put("java.home", System.getProperty("java.home"));
			put("java.class.path", System.getProperty("java.class.path"));
			put("os.name", System.getProperty("os.name"));
			put("os.arch", System.getProperty("os.arch"));
			put("os.version", System.getProperty("os.version"));
			put("file.separator", System.getProperty("file.separator"));
			put("path.separator", System.getProperty("path.separator"));
			put("line.separator", System.getProperty("line.separator"));
			put("user.name", System.getProperty("user.name"));
			put("user.home", System.getProperty("user.home"));
			put("user.dir", System.getProperty("user.dir"));
		}
	};
	
	/**
	 * Basic Constructor. Beside setting the specified parameters as local values and setting some
	 * internal fields to initial their values, it will reset the class loader's number of already
	 * instantiated classes and make sure that statically initialized classes of prior executions
	 * are dropped.
	 *
	 * @param application The application this virtual machine is used by.
	 * @param classLoader The main classLoader to use.
	 * @param classFile The classFile to start execution with.
	 * @param initialMethod The Method to start execution with. This Method has to be a method of
	 *        the supplied classFile.
	 * @throws InitializationException If initialization of auxiliary classes fails.
	 */
	public VirtualMachine(Application application, MugglClassLoader classLoader,
			ClassFile classFile, Method initialMethod) throws InitializationException {
		this.finalized = false;
		this.application = application;
		classLoader.resetInitializedClassFileCache();
		classLoader.resetInstantiationNumber();
		this.classLoader = classLoader;
		this.classFile = classFile;
		this.initialMethod = initialMethod;
		this.stepByStepMode = false;
		this.nextStepReady = true;
		this.pauseExecution = false;
		this.nextFrameIsAlreadyLoaded = false;
		this.nextInitializedClassId = -1;
		this.executedFrames = 0;
		this.executedInstructions = 0;
		this.ignoreInterruption = 0;
		this.runUntilNoOfInstructionsReached = false;
		this.executedInstructionsTarget = -1L;
		
		this.stringCache = new StringCache(this);
		this.throwableGenerator = new ThrowableGenerator(this);			
		this.currentFrame = null;
		this.stack = null;		
	}

	/**
	 * Getter for the virtual machine stack.
	 * @return The virtual machine stack
	 */
	public Stack<Object> getStack() {
		return this.stack;
	}

	/**
	 * Main Method: Start the execution in this virtual machine, using an own thread.
	 */
	@Override
	public void run() {
		Globals.getInst().execLogger.info("Starting a virtual machine (thread #" + this.getId() + ")");
		try {
			Globals.getInst().vmIsInitialized = true;
			Globals.getInst().execLogger.debug("Terminated VM initialization. Loading initMethod and initial Frame");
			// Preparations for the initial frame.
			Object[] predefinedParameters = this.initialMethod.getPredefinedParameters();
			Object[] arguments = null;

			int addOne = 0;
			if (!this.initialMethod.isAccStatic()) addOne++;

			if (predefinedParameters != null) {
				arguments = new Object[predefinedParameters.length + addOne];
				for (int a = 0; a < predefinedParameters.length; a++) {
					// Check if predefined parameters have to be modified.
					if (predefinedParameters[a] != null && predefinedParameters[a].getClass().isArray()) {
						// If predefined parameters are arrays, they have to be converted to Arrayref objects.
						// First we need to check whether the array wraps a primitive type or a reference type.
						boolean isPrimitive;
						try {
							ClassFile c = this.getClassLoader().getClassAsClassFile(
									predefinedParameters[a].getClass());
							c.getAPrimitiveWrapperObjectref(this);
							// Success, so type is primitive;
							isPrimitive = true;
						} catch (PrimitiveWrappingImpossibleException e) {
							isPrimitive = false;
						}
						MugglToJavaConversion conversion = new MugglToJavaConversion(this);
						Arrayref arrayref = (Arrayref) conversion.toMuggl(predefinedParameters[a], isPrimitive);
						arguments[a + addOne] = arrayref;
					} else if (this.initialMethod.isAccVarargs()
							&& a == predefinedParameters.length - 1
							&& predefinedParameters[a] != null
							&& predefinedParameters[a] instanceof UndefinedValue) {
						/*
						 * The method may take variable arguments. If its last parameter is
						 * undefined, pass an array of zero length.
						 */
						Objectref objectref = getAnObjectref(this.classLoader.getClassAsClassFile(
										this.initialMethod.getParameterTypeAtIndex(a)));
						Arrayref arrayref = new Arrayref(objectref, 0);
						arguments[a + addOne] = arrayref;
					} else {
						// Just store the object.
						arguments[a + addOne] = predefinedParameters[a];
					}
				}
			} else {
				arguments = new Object[1];
			}
			if (!this.initialMethod.isAccStatic()) arguments[0] = getAnObjectref(this.classFile);

			// Create initial frame.
			createAndPushFrame(null, this.initialMethod, arguments);
			Frame visualStartingFrame = (Frame) this.stack.peek();

			// class initialization nedded?, so you can conveniently call instance methods from MugglGUI and tests
//			if (!this.initialMethod.getName().equals(VmSymbols.OBJECT_INITIALIZER_NAME) && !this.initialMethod.isAccStatic()) {			
//				try {
//					createAndPushFrame(null, this.classFile.getMethodByNameAndDescriptor(VmSymbols.OBJECT_INITIALIZER_NAME, "()V"), new Object[]{arguments[0]});
//					
//					if (Globals.getInst().execLogger.isDebugEnabled()) Globals.getInst().execLogger.debug("A class initializer (<init>-method) has been found for class " + this.classFile.getName() + ". It will be executed next.");
//				} catch (MethodResolutionError e) {
//					// This exception is expected, it just symbolizes there is no static initializer for this class.
//					if (Globals.getInst().execLogger.isDebugEnabled()) Globals.getInst().execLogger.debug("There is no class initializer (<init>-method) for class " + this.classFile.getName() + ". Execution will start with method " + this.initialMethod.getName() + ".");
//				}
//			}

			// Is there a static initializer for this class? If yes, it has to be pushed as a Frame either. Of course, do not do this if the method to be executed first is the static initializer itself.
			if (!this.initialMethod.getName().equals(VmSymbols.CLASS_INITIALIZER_NAME)) {
				try {
					createAndPushFrame(null, this.classFile.getClinitMethod(), null);
					if (Globals.getInst().execLogger.isDebugEnabled()) Globals.getInst().execLogger.debug("A static initializer (<clinit>-method) has been found for class " + this.classFile.getName() + ". It will be executed first.");
				} catch (MethodResolutionError e) {
					// This exception is expected, it just symbolizes there is no static initializer for this class.
					if (Globals.getInst().execLogger.isDebugEnabled()) Globals.getInst().execLogger.debug("There is no static initializer (<clinit>-method) for class " + this.classFile.getName() + ". Execution will start with method " + this.initialMethod.getName() + ".");
				}
			}
						
			// Notify the Application.
			this.application.newVMHasBeenInitialized();
			// Start the execution
			runMainLoop(visualStartingFrame);
		// Error and exception handling
		} catch (NoExceptionHandlerFoundException e) {
			// Store the uncaught Exception.
			this.returnedObject = e.getUncaughtThrowable();
			this.hasAReturnValue = false;
			this.threwAnUncaughtException = true;
			this.returnedObject = e.getUncaughtThrowable();

			// Halt the virtual machine. Do not mark that an error has occurred, though.
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			Globals.getInst().execLogger.error("An runtime exception was not caught by the executed application. Halting the virtual machine.");
		} catch (StackOverflowError e) {
			this.errorMessage = "Stack overflow error: " + e.getMessage();
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Fatal error during execution, halting the virtual machine: " + this.errorMessage);
		} catch (OutOfMemoryError e) {
			this.errorMessage = "Out of memory error: " + e.getMessage();
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Fatal error during execution, halting the virtual machine: " + this.errorMessage);
		} catch (InterruptedException e) {
			/*
			 * This is an intended exception, signaling the manual end of the step by step
			 * execution. Therefore, no error information is saved.
			 */
			this.nextStepReady = false;
			if (Globals.getInst().execLogger.isEnabledFor(Level.INFO))
				Globals.getInst().execLogger.info("The virtual machine was halted with an InterruptionException, since the execution was aborted.");
			if (!this.finalized) this.application.executionHasFinished();
			// Return, so no more logging is done.
			return;
		} catch (InvalidInstructionInitialisationException e) {
			this.errorMessage = "InvalidInstructionInitialisationException: " + e.getMessage();
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Error during the execution while parsing instructions, halting the virtual machine: " + this.errorMessage);
		} catch (SymbolicExecutionException e) {
			this.errorMessage = "Symbolic execution exception: " + e.getMessage();
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Error during the symbolic execution, halting the virtual machine: " + this.errorMessage);
		} catch (ExecutionException e) {
			this.errorMessage = "Execution exception: " + e.getMessage();
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Error during the execution, halting the virtual machine: " + this.errorMessage);
            Globals.getInst().execLogger.error(this.currentStackTrace());
		} catch (Exception e) {
			this.errorMessage = "General exception of type " + e.getClass().getName() + ": " + e.getMessage();
			// {{ add stack trace to string
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			this.errorMessage += "\n" + sw.toString();
			// }}
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			Globals constants = Globals.getInst();
			if (constants.execLogger.isEnabledFor(Level.ERROR))
				constants.execLogger.error("General exception during the execution, halting the virtual machine: " + this.errorMessage);
			/**
			 * If Logging is at least in debug mode, log the full stack trace. Unlike most other catch-clauses
			 * here, the generic Exception hints to general bugs in the application. Logging the full stack
			 * trace should help to find and fix them.
			 */
			if (constants.execLogger.isDebugEnabled()) {
				String toLog = "The exception that haltet the virtual machine:<br />\n"
								+ e.getClass().getName();
				if (e.getMessage() != null) toLog += "(" + e.getMessage() + ")";
				toLog += "<br>\n";
				StackTraceElement[] elements = e.getStackTrace();
				for (StackTraceElement element : elements) {
					toLog += "&nbsp;&nbsp;&nbsp;" + element.toString() + "<br />\n";
				}
				synchronized (constants) {
					constants.setLoggingMessagesEscaping(false);
					constants.guiLogger.debug(toLog);
					constants.setLoggingMessagesEscaping(true);
				}
			}
		} catch (ExceptionInInitializerError e) {
			this.errorMessage = "Fatal initialization error: " + e.getMessage();
			if (e.getMessage().contains("not be found")) this.errorMessage += "\n\nYou should check if the expected class can be accessed with the current class path settings.";
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Fatal error during the execution, halting the virtual machine: " + this.errorMessage);
		} catch (Error e) {
			e.printStackTrace();
			this.errorMessage = "Fatal error of type " + e.getClass().getName() + ": " + e.getMessage();
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Fatal error during the execution, halting the virtual machine: " + this.errorMessage);
		} catch (Throwable t) {
			this.errorMessage = "Fatal throwable of type " + t.getClass().getName() + ": " + t.getMessage();
			this.errorOccured = true;
			this.nextStepReady = false;
			if (!this.finalized) this.application.finalizeApplication();
			if (Globals.getInst().execLogger.isEnabledFor(Level.ERROR))
				Globals.getInst().execLogger.error("Fatal error during the execution, halting the virtual machine: " + this.errorMessage);
		}
		if (!this.finalized) this.application.executionHasFinished();
		if (!this.errorOccured && Globals.getInst().execLogger.isInfoEnabled())
			Globals.getInst().execLogger.info("Execution in the virtual machine finished successfully.");
	}

	/**
	 * Set up the system thread group and threads. Copied over from openjdk/hotspot/src/share/vm/runtime/thread.cpp
	 * 
	 * This is quasi Threads::create_vm
	 * 
	 * @throws InterruptedException
	 * @throws InvalidInstructionInitialisationException
	 * @throws ExecutionException
	 * @throws ClassFileException
	 */
	private void setUpThreads() throws ClassFileException, ExecutionException,
			InvalidInstructionInitialisationException, InterruptedException {

		// here you are at openjdk/hotspot/src/share/vm/runtime/thread.cpp:3497

		// instead of using our vmSybols Literals, we can refer to the class from the RuntimeClassLib
		initialize_class(java.lang.String.class.getCanonicalName());

		// Initialize java_lang.System (needed before creating the thread)
		initialize_class(java.lang.System.class.getCanonicalName());
		initialize_class(java.lang.ThreadGroup.class.getCanonicalName());

		Objectref thread_group = create_initial_thread_group();

		Universe.set_main_thread_group(thread_group);

		initialize_class(java.lang.Thread.class.getCanonicalName());

		Objectref thread_object = create_initial_thread(thread_group);
		java_lang_thread.set_thread_status(thread_object, java.lang.Thread.State.RUNNABLE);

		// The VM creates & returns objects of this class. Make sure it's initialized.
		initialize_class(java.lang.Class.class.getCanonicalName());
		
		// The VM preresolves methods to these classes. Make sure that they get initialized
		initialize_class(java.lang.reflect.Method.class.getCanonicalName());
		initialize_class("java.lang.ref.Finalizer"); // java.lang.ref.Finalizer.class.getCanonicalName() does not work
														// because its not visible

		call_initializeSystemClass();

		// // get the Java runtime name after java.lang.System is initialized
		// JDK_Version::set_runtime_name(get_java_runtime_name(THREAD));
		// JDK_Version::set_runtime_version(get_java_runtime_version(THREAD));
		//
	}

	/**
	 * Set up the system and main Thread-Group. equivalent source file is openjdk/jdk/share/native/java/lang/thread.cpp
	 * 
	 * @return
	 * @throws ClassFileException
	 * @throws ExecutionException
	 * @throws InvalidInstructionInitialisationException
	 * @throws InterruptedException
	 */
	private Objectref create_initial_thread_group() throws ClassFileException, ExecutionException,
			InvalidInstructionInitialisationException, InterruptedException {
		ClassFile initClassF = this.classLoader.getClassAsClassFile(java.lang.ThreadGroup.class.getCanonicalName());
		initClassF.getTheInitializedClass(this);
		Objectref system_instance = this.getAnObjectref(initClassF);

		java_call_special(system_instance, initClassF, VmSymbols.OBJECT_INITIALIZER_NAME,
				MethodType.methodType(void.class));
		Universe.set_system_thread_group(system_instance);

		Objectref main_instance = this.getAnObjectref(initClassF);
		Objectref name = this.getStringCache().getStringObjectref("main");

		java_call_special(main_instance, initClassF, VmSymbols.OBJECT_INITIALIZER_NAME,
				MethodType.methodType(void.class, ThreadGroup.class, String.class), system_instance, name);

		return main_instance;
	}

	/**
	 * Creates the Java Object for the main thread. On "OS"-layer, this is the main thread.
	 * 
	 * @param thread_group
	 * @return
	 * @throws ClassFileException
	 * @throws InterruptedException
	 * @throws InvalidInstructionInitialisationException
	 * @throws ExecutionException
	 */
	private Objectref create_initial_thread(Objectref thread_group) throws ClassFileException, ExecutionException,
			InvalidInstructionInitialisationException, InterruptedException {
		// openjdk/hotspot/src/share/vm/runtime/os.hpp: NormPriority = 5,
		// Normal (non-daemon) priority
		final int NORM_PRIORITY = 5;
		ClassFile initClassF = this.classLoader.getClassAsClassFile(java.lang.Thread.class.getCanonicalName());
		initClassF.getTheInitializedClass(this);
		Objectref thread_objref = this.getAnObjectref(initClassF);

		java_lang_thread.set_thread(thread_objref, Thread.currentThread());

		java_lang_thread.set_priority(thread_objref, NORM_PRIORITY);

		VirtualMachine.threadObj = thread_objref;

		Objectref name = this.getStringCache().getStringObjectref("main");

		java_call_special(thread_objref, initClassF, VmSymbols.OBJECT_INITIALIZER_NAME,
				MethodType.methodType(void.class, ThreadGroup.class, String.class), thread_group, name);

		return thread_objref;
	}

	private void call_initializeSystemClass() throws ClassFileException, ExecutionException,
			InvalidInstructionInitialisationException, InterruptedException {

		ClassFile initClassF = this.classLoader.getClassAsClassFile(java.lang.System.class.getCanonicalName());
		Method initMethod = initClassF.getMethodByNameAndDescriptor("initializeSystemClass", MethodType.methodType(void.class).toMethodDescriptorString());
		// method is static - no parameters
		Object[] arguments = new Object[0];
				
		Frame systemStartupFrame = createFrame(null, initMethod, arguments);
		this.stack.push(systemStartupFrame);
		systemStartupFrame.setHiddenFrame(true);
		Boolean stepByStep = this.stepByStepMode;
		this.stepByStepMode = false;
		runMainLoop(systemStartupFrame);
		this.stepByStepMode = stepByStep;
		Globals.getInst().execLogger.debug("test");

	}

	/**
	 * Call a java method on an objectref Contract for this method is caller knows what he does, so passing on every
	 * exception
	 * 
	 * @param objectref
	 * @param klass
	 * @param methodName
	 * @throws ExecutionException
	 * @throws InvalidInstructionInitialisationException
	 * @throws InterruptedException
	 */
	private void java_call_special(Objectref objectref, ClassFile klass, String methodName, MethodType methodType)
			throws ExecutionException, InvalidInstructionInitialisationException, InterruptedException {
		Method initMethod = klass.getMethodByNameAndDescriptorOrNull(methodName, MethodType.methodType(void.class));
		if (initMethod != null){
		Object[] arguments = new Object[1];
		arguments[0] = objectref;
		Frame systemStartupFrame = createFrame(null, initMethod, arguments);
		this.stack.push(systemStartupFrame);
		systemStartupFrame.setHiddenFrame(true);
		Boolean stepByStep = this.stepByStepMode;
		this.stepByStepMode = false;
		runMainLoop(systemStartupFrame);		
		this.stepByStepMode = stepByStep;
		}
	}

	// FIXME mxs: silly but quick copy
	private void java_call_special(Objectref objectref, ClassFile klass, String methodName, MethodType methodType,
			Objectref arg1, Objectref arg2)
			throws ExecutionException, InvalidInstructionInitialisationException, InterruptedException {
		Method initMethod = klass.getMethodByNameAndDescriptorOrNull(methodName, methodType);

		Object[] arguments = new Object[3];
		arguments[0] = objectref;
		arguments[1] = arg1;
		arguments[2] = arg2;
		Frame systemStartupFrame = createFrame(null, initMethod, arguments);
		this.stack.push(systemStartupFrame);
		systemStartupFrame.setHiddenFrame(true);
		Boolean stepByStep = this.stepByStepMode;
		this.stepByStepMode = false;
		runMainLoop(systemStartupFrame);
		this.stepByStepMode = stepByStep;
	}				

	// Equivalence to a method in OpenJDK thread.cpp. When we know that the class should be there, spare us the
	// try_catches...
	private void initialize_class(String class_name) {
		ClassFile initClassF;
		try {
			initClassF = this.classLoader.getClassAsClassFile(class_name);
			initClassF.getTheInitializedClass(this);

		} catch (ClassFileException e) {
			// catch is pretty silly, we should know which classes we want to initialize...
			e.printStackTrace();
		}
	}

	/**
	 * While the virtual machine stack is not empty, objects are popped from it. If the proper
	 * object is a frame, it is executed. If not, it has to be a returned object from a method. If
	 * the stack is not empty, the next element has to be a frame and it is popped off it; then the
	 * object is pushed onto its operand stack and the frame executed. If the stack however is
	 * empty, the object is the applications return value and threatened as such. The execution then
	 * ends.
	 *
	 * @param visualStartingFrame If step by step execution is enabled, there will be no stepping
	 *        until this frame has been reached.
	 * @throws ExecutionException Thrown on any fatal error that happens during execution and is not
	 *         coped by one of the other Exceptions.
	 * @throws InterruptedException Thrown to signal the manual end of the step by step execution.
	 * @throws InvalidInstructionInitialisationException Any fatal problems with the parsing and the
	 *         initialization will lead to this exception.
	 * @throws OutOfMemoryError If the virtual machine runs out for memory, this error is thrown.
	 * @throws StackOverflowError If the operand stack of a frame or the virtual machine stack
	 *         exceeds its element limit, this Error is thrown.
	 */
	protected void runMainLoop(Frame visualStartingFrame) throws ExecutionException,
			InterruptedException, InvalidInstructionInitialisationException {
		boolean allowStepping = false;
		// Main execution loop.
		while (!this.stack.isEmpty() || this.nextFrameIsAlreadyLoaded) {
			Frame frame = null;
			/**
			 * Due to exception handling an earlier frame might have been loaded by the exception
			 * handler already. In such a case, no loaded is needed. Otherwise, proceed with the
			 * normal popping of the next virtual machine stack entry. The same mechanism is used
			 * for symbolic execution when backtracking is done.
			 */
			if (this.nextFrameIsAlreadyLoaded) {
				// Reset to false - the next time the normal popping of a frame is needed again.
				this.nextFrameIsAlreadyLoaded = false;
				this.returnFromCurrentExecution = false;
				this.threwAnUncaughtException = false;
				frame = this.currentFrame;
				if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Resuming operation with the frame of " + frame.getMethod().getPackageAndName() + ".");
			} else {
				Object object = this.stack.pop();
				// Is the object a frame?
				if (!(object instanceof Frame)) {
					// It might be the last object at all!
					if (this.stack.isEmpty()) {
						// We got the return value of the whole application - end execution!
						this.threwAnUncaughtException = false;
						finishExecution(object);
						return;
					}
					frame = (Frame) this.stack.pop();
					frame.getOperandStack().push(object);
				} else {
					frame = (Frame) object;
				}
                if (Globals.getInst().execLogger.isTraceEnabled()) {
                    if (!frame.isHiddenFrame() && Globals.getInst()
                            .logBasedOnWhiteBlacklist(frame.getMethod().getPackageAndName()).orElse(true))
                        Globals.getInst().execLogger.trace("Continuing operation with the next frame ("
                                + frame.getMethod().getPackageAndName() + "(" + frame.getMethod().getParameterTypesAndNames() + ")).");
                }
			}

			// Enable stepping once the frame of the initially invoked method is reached.
			if (!allowStepping && this.stepByStepMode && frame == visualStartingFrame) {
				allowStepping = true;
				if (Globals.getInst().execLogger.isTraceEnabled()) Globals.getInst().execLogger.trace("Skipping of the static initializer of class " + this.classFile.getName() + " finished. Step by step mode is enabled now.");
			}

			// Start execution of this frame.
			changeCurrentFrame(frame);
			this.pc = this.currentFrame.getPc();
			executeFrame(allowStepping);
			this.returnFromCurrentExecution = false;
		}
	}

	/**
	 * Execute the current frame. First the Method and its instructions are loaded, then the frame is set active.
	 * The main execution loop runs as long as it is active, the pc is not beyond the last instruction and it is
	 * not explicitly left.
	 * @param allowStepping If set to false there will not be no stepping even if step by step mode is enabled. This is used to skip static initializers.
	 * @throws ExecutionException An exception is thrown on any fatal errors during execution.
	 * @throws InterruptedException Thrown to signal the manual end of the step by step execution.
	 * @throws InvalidInstructionInitialisationException Any fatal problems with the parsing and the initialization will lead to this exception.
	 */
	protected void executeFrame(boolean allowStepping) throws ExecutionException, InterruptedException, InvalidInstructionInitialisationException {
		this.executedFrames++;
		Method method = this.currentFrame.getMethod();


        if (Globals.getInst().executionInstructionLogger.isDebugEnabled()) {
            if (!this.currentFrame.isHiddenFrame()
                    && Globals.getInst().logBasedOnWhiteBlacklist(method.getPackageAndName()).orElse(true))
                Globals.getInst().executionInstructionLogger.debug(
                        method.getFullNameWithParameterTypesAndNames() + ":" + method.getReturnType() + " (op: " + this.currentFrame.getOperandStack()
                                + ", localvar: [" + Arrays.stream(this.currentFrame.getLocalVariables())
                                .map(x -> (x == null) ? "null" : x.toString()).collect(Collectors.joining(", "))
                                + "] pc: " + this.pc + ")");
        }

		Instruction[] instructions = method.getInstructionsAndOtherBytes();
		this.currentFrame.setActive(true);
		while (this.currentFrame.isActive() && this.pc < instructions.length) {
			// Step by step mode?
			if (allowStepping && this.stepByStepMode) {
				// Only stop if not aiming for a specified number of steps.
				if (this.runUntilNoOfInstructionsReached) {
					if (this.executedInstructionsTarget <= this.executedInstructions) {
						// Reached the desired number of instructions.
						this.runUntilNoOfInstructionsReached = false;
						this.nextStepReady = false;
					}
				} else {
					this.nextStepReady = false;
				}

				// Polling: When to continue?
				try {
					synchronized (this) {
						while (!this.nextStepReady) {
							wait();
						}
					}
				}  catch (InterruptedException e) {
					throw new InterruptedException("The step by step execution ended with an InterruptedException.");
				}
			}

			// Pause execution?
			if (this.pauseExecution) {
				// Polling: When to continue?
				try {
					synchronized (this) {
						while (this.pauseExecution) {
							wait();
						}
					}
				}  catch (InterruptedException e) {
					throw new InterruptedException("Pausing the execution ended with an InterruptedException.");
				}
			}

			// Interrupted?
			if (isInterrupted() && this.ignoreInterruption == 0) {
				/*
				 * Make sure execution is aborted. An interruption always means that the virtual machine is
				 * supposed to stop.
				 */
				throw new InterruptedException("Execution got interrupted.");
			}

			// Save the pc.
			int pc = this.pc;

			// avoid log pollution with certain filters
            if (Globals.getInst().executionInstructionLogger.isTraceEnabled()) {
                if (!currentFrame.isHiddenFrame() && Globals.getInst()
                        .logBasedOnWhiteBlacklist(this.currentFrame.method.getPackageAndName()).orElse(true))
                    Globals.getInst().executionInstructionLogger.trace(this.currentFrame.method.getPackageAndName()
                            + " " + String.format("%1$2s", this.pc) + ": Executing " + instructions[this.pc].getNameWithOtherBytes());
            }

			// Execute the instruction.
			executeInstruction(instructions[pc]);

			// Jumped too far?
			if (this.pc >= Limitations.MAX_CODE_LENGTH) {
				this.pc -= Limitations.MAX_CODE_LENGTH;
			}

			// Move to next instruction only if we did not jump further (jump instructions do not require this stepping further).
			if (this.pc == pc) {
				this.pc += 1 + instructions[pc].getNumberOfOtherBytes();
			}

			// If we are now supposed to stop, we will do so.
			if (this.returnFromCurrentExecution) {
				return;
			}
		}
	}

	/**
	 * Abstract method for the actual instruction Execution.
	 * @param instruction The instruction that is to be executed.
	 * @throws ExecutionException An exception is thrown on any fatal errors during execution.
	 */
	protected abstract void executeInstruction(Instruction instruction) throws ExecutionException;

	/**
	 * Interrupt the execution.
	 *
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		// Invoke the interruption implementation of java.lang.Thread.
		super.interrupt();
	}

	/**
	 * Finish execution by marking that there was an return value and storing it into the designated field.
	 * @param object The returned object.
	 */
	private void finishExecution(Object object) {
		if (Globals.getInst().execLogger.isDebugEnabled()) Globals.getInst().execLogger.debug("Execution finished with a return value");
		this.hasAReturnValue = true;
		this.returnedObject = object;
		this.threwAnUncaughtException = false;
	}

	/**
	 * Getter for the Application.
	 * @return The Application this VirtualMachine belongs to.
	 */
	public Application getApplication() {
		return this.application;
	}

	/**
	 * Getter for the returned object.
	 * @return The returned object.
	 */
	public Object getReturnedObject() {
		return this.returnedObject;
	}

	/**
	 * Getter for the information whether a value was returned, or not.
	 * @return true, if a value was returned, false otherwise.
	 */
	public boolean getHasAReturnValue() {
		return this.hasAReturnValue;
	}

	/**
	 * Getter for the information whether the return value can be treated
	 * as the actual return value of the method's execution (false), or if
	 * execution the method has resulted in an uncaught Exception beeing thrown (true).
	 * @return true, if the method threw an uncaught exception, false otherwise.
	 */
	public boolean getThrewAnUncaughtException() {
		return this.threwAnUncaughtException;
	}

	/**
	 * Getter for the Classfile.
	 * @return The ClassFile.
	 */
	public ClassFile getClassFile() {
		return this.classFile;
	}

	/**
	 * Getter for the initial Method.
	 * @return The initial Method.
	 */
	public Method getInitialMethod() {
		return this.initialMethod;
	}

	/**
	 * Getter for the errorOccured field.
	 * @return true, if an error has occured during execution, false otherwise.
	 */
	public boolean errorOccured() {
		return this.errorOccured;
	}

	/**
	 * Fetch the error message generated by the execution, if there was any.
	 * @return The error message.
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * Getter for the MugglClassLoader.
	 *
	 * @return The MugglClassLoader.
	 */
	public MugglClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * Getter for the String cache.
	 *
	 * @return The string cache.
	 */
	public StringCache getStringCache() {
		return this.stringCache;
	}
	
	/**
	 * Get an exception from the exception generator.
	 *
	 * @param typeString The type of the exception to create.
	 * @return An exception to to used in the runtime system.
	 */
	public Objectref generateExc(String typeString) {
		return this.throwableGenerator.getException(typeString);
	}
	
	/**
	 * Get an exception from the exception generator.
	 *
	 * @param typeString The type of the exception to create.
	 * @param message The detail message of the exception.
	 * @return An exception to to used in the runtime system.
	 */
	public Objectref generateExc(String typeString, String message) {
		// marker for debug logs for easier finding of where an exception originated		
		Globals.getInst().execLogger.info("generating a new exception " + typeString + "(" + message + ") in muggl at: " + Thread.currentThread().getStackTrace()[0].toString());
		
		Globals.getInst().execLogger.debug(this.currentStackTrace());			

		return this.throwableGenerator.getException(typeString, message);
	}

	/**
	 * Getter for the current Frame.
	 * @return The currently active Frame.
	 */
	public Frame getCurrentFrame() {
		return this.currentFrame;
	}

	/**
	 * Setter for the current Frame. This method bypasses changeCurrentFrame(Frame).<br />
	 * <br />
	 * WARNING: This method should only be used if you know what you are doing.
	 *
	 * @see #changeCurrentFrame
	 * @param frame The Frame to become the current Frame.
	 */
	public void setCurrentFrame(Frame frame) {
		this.currentFrame = frame;
	}

	/**
	 * Getter for the pc.
	 * @return The current pc.
	 */
	public int getPc() {
		return this.pc;
	}

	/**
	 * Setter for the pc. This method bypasses changeCurrentPC(int).<br />
	 * <br />
	 * WARNING: This method should only be used if you know what you are doing.
	 * @see #changeCurrentPC
	 * @param pc The new pc value as an int.
	 */
	public void setPC(int pc) {
		this.pc = pc;
	}

	/**
	 * Additional setter for the pc.
	 * @param pc The value to be added to the current pc.
	 */
	public void addToPC(int pc) {
		this.pc += pc;
	}

	/**
	 * Getter for the step by step mode.
	 * @return true, if step by step mode is enabled false otherwise.
	 */
	public boolean getStepByStepMode() {
		return this.stepByStepMode;
	}

	/**
	 * Setter for the step by step mode.
	 * @param stepByStepMode The new value for stepByStepMode.
	 */
	public void setStepByStepMode(boolean stepByStepMode) {
		this.stepByStepMode = stepByStepMode;
	}

	/**
	 * Setter for the field  returnFromCurrentExecution.
	 * @param returnFromCurrentExecution A boolean value
	 */
	public void setReturnFromCurrentExecution(boolean returnFromCurrentExecution) {
		this.returnFromCurrentExecution = returnFromCurrentExecution;
	}

	/**
	 * Sets the field nextFrameIsAlreadyLoaded to true, indicating that when
	 * looping through runMainLoop() for the next time, no Frame has to be popped.
	 *
	 */
	public void enableNextFrameIsAlreadyLoaded() {
		this.nextFrameIsAlreadyLoaded = true;
	}

	/**
	 * Getter for nextStepReady.
	 * @return True, if the virtual machine is ready to execute the next step, false otherwise.
	 */
	public synchronized boolean getNextStepReady() {
		return this.nextStepReady;
	}

	/**
	 * Set nextStepReady to true, signaling the execution engine to
	 * proceed with the next instruction.
	 *
	 */
	public synchronized void executeNextStep() {
		this.nextStepReady = true;
		notify();
	}

	/**
	 * Create a new frame and push it onto the virtual machine stack.
	 * 
	 * @param invokedBy The frame this frame is invoked by. Might by null.
	 * @param method The Method that this frame holds.
	 * @param arguments The arguments that will be stored in the local variables prior to execution.
	 * @throws ExecutionException On any fatal error that happens during execution and is not coped
	 *         by one of the other Exceptions.
	 */
	public void createAndPushFrame(Frame invokedBy, Method method, Object[] arguments)
			throws ExecutionException {
		Frame frame = createFrame(invokedBy, method, arguments);
		this.stack.push(frame);
	}

	/**
	 * Create a new frame.
	 * 
	 * @param invokedBy The frame this frame is invoked by. Might by null.
	 * @param method The Method that this frame holds.
	 * @param arguments The arguments that will be stored in the local variables prior to execution.
	 * @return The new frame.
	 * @throws ExecutionException On any fatal error that happens during execution and is not coped
	 *         by one of the other Exceptions.
	 */
	protected Frame createFrame(Frame invokedBy, Method method, Object[] arguments) throws ExecutionException {
		Frame frame = new Frame(invokedBy, this, method, method.getClassFile().getConstantPool(), arguments);
		return frame;
	}

	/**
	 * Increase the nextInitializedClassId by one and return it.
	 * @return The next id for an initialized class file.
	 */
	public long getNextInitializedClassId() {
		this.nextInitializedClassId++;
		return this.nextInitializedClassId;
	}

	/**
	 * Get the Monitor associated with the supplied object. Create a new monitor if there currently
	 * is no monitor associated with it.
	 * 
	 * @param objectref The Object to get the monitor for.
	 * @return The associated Monitor.
	 * @throws ExecutionException If <code>objectref</code> is null.
	 */
	public Monitor getMonitorForObject(Objectref objectref) throws ExecutionException {
		if (objectref == null) throw new ExecutionException("null");
		
		// TODO
		if (Globals.getInst().execLogger.isEnabledFor(Level.WARN))
			Globals.getInst().execLogger.warn("Monitor-Support is not yet implemented!");
		
		// TODO: Implement this method.
		return new Monitor();
	}
	
	/**
	 * Get the Monitor associated with the supplied class file. Create a new monitor if there currently
	 * is no monitor associated with it. This is used for static invocation.
	 * 
	 * @param classFile The class file associated.
	 * @return The associated Monitor.
	 * @throws NullPointerException If <code>classFile</code> is null.
	 */
	public Monitor getMonitorForStaticInvocation(ClassFile classFile) {
		if (classFile == null) throw new NullPointerException();
		// TODO: Implement this method.
		return new Monitor();
	}

	/**
	 * Create a new frame, invoke executeFrame() and catch any exception thrown, throwing
	 * an ExceptionInInitializerError in such a case. Restore the virtual machines state afterwards.
	 *
	 * DO NOT INVOKE THIS METHOD UNLESS YOU ARE ABSOLUTELY SURE WHAT YOU ARE DOING AND UNDERSTAND
	 * THE CONTROL FLOW.
	 *
	 * Warning: This method is only meant to be called by the class InitializedClass as a way of
	 * initializing a class. Invoking it for any other reason or in any other case might lead to
	 * a fatal execution error in the virtual machine and will at least result in an completely
	 * unexpected behaviour of the virtual machine.
	 *
	 * @param method The method to be used for initialization. Must have a signature of <clinit>
	 * @throws ExceptionInInitializerError If the methods' is not <clinit> or if any Exception is returned.
	 */
	public void executeTheCurrentFrameForClassInitialization(Method method) {
		try {
			// Method allowed?
			if (!method.getName().equals(VmSymbols.CLASS_INITIALIZER_NAME)) throw new ExceptionInInitializerError("Only a method with signature <clinit> might be used for class initialization.");

            Boolean parentFrameIsHidden = currentFrame != null && currentFrame.isHiddenFrame();
            if (Globals.getInst().execLogger.isTraceEnabled()) {
                if (!parentFrameIsHidden
                        && Globals.getInst().logBasedOnWhiteBlacklist(method.getClassFile().getName()).orElse(true))
                    Globals.getInst().execLogger
                            .trace("Now executing the static initializer of " + method.getClassFile().getName() + ".");
            }

			// Save the current data.
			int savedPC = getPc();
			Frame savedFrame = getCurrentFrame();

			// For exception handling, push this frame onto the vm stack.
			this.stack.push(savedFrame);

			// Create a new frame
			createAndPushFrame(savedFrame, method, null);

			// If static initialization should be skipped visually when running the step by step mode, log this.
			if (this.stepByStepMode && Options.getInst().visuallySkipStaticInit) {
				if (Globals.getInst().execLogger.isTraceEnabled())
					Globals.getInst().execLogger
							.trace("Visually skipping the static initializer of class "
									+ method.getClassFile().getName() + ".");
			}

			// Execute the frame and frames generated by it!
			while (!this.stack.isEmpty() && !this.nextFrameIsAlreadyLoaded) {
				Frame frame = null;
				Object object = this.stack.pop();
				// Is the object a frame?
				if (!(object instanceof Frame)) {
					// it might be the last object at all!
					if (this.stack.isEmpty()) {
						// we got the return value of the whole application - end execution!
						finishExecution(object);
						return;
					}
					frame = (Frame) this.stack.pop();
					frame.getOperandStack().push(object);
				} else {
					frame = (Frame) object;
				}
				// Stop it here, we are back!
				if (frame.equals(savedFrame)) break;

                if (Globals.getInst().execLogger.isTraceEnabled()) {
                    if (!frame.getMethod().equals(method) && !frame.isHiddenFrame() && Globals.getInst()
                            .logBasedOnWhiteBlacklist(frame.getMethod().getPackageAndName()).orElse(true))
                        // if getParameterTypesAndNames outputs parameters null, it might also be that we're only re-entering a frame
                        // and parameter resolution isn't accurate
                        Globals.getInst().execLogger.trace("Continuing operation with the next frame ("
                                + frame.getMethod().getPackageAndName() + "). Invoked by the static initializer of "
                                + method.getClassFile().getName() + ".");
                }

				frame.setHiddenFrame(parentFrameIsHidden);
				// start execution of this frame
				changeCurrentFrame(frame);
				this.pc = this.currentFrame.getPc();
				this.ignoreInterruption++;
				executeFrame(!Options.getInst().visuallySkipStaticInit);
				this.ignoreInterruption--;
				this.returnFromCurrentExecution = false;
			}


			// Restore the data if no exception has occurred in the meantime.
			if (!this.nextFrameIsAlreadyLoaded) {
				setPC(savedPC);
				setCurrentFrame(savedFrame);
			} else {
				this.returnFromCurrentExecution = true;
			}

			if (!currentFrame.isHiddenFrame()) 
				Globals.getInst().execLogger.trace("Execution of the static initializer of " + method.getClassFile().getName() + " finished. Returning to the normal program flow.");
			
			//FIXME mxs: remove this hack
			if(method.getClassFile().getName().equals("sun.reflect.generics.parser.SignatureParser")) {
			method.getClassFile().getInitializedClass().putField(method.getClassFile().getFieldByName("DEBUG"), 1);
			}
		} catch (NoExceptionHandlerFoundException e) {
			Objectref objectref = e.getUncaughtThrowable();
			String type = objectref.getInitializedClass().getClassFile().getClassName();
			String message;
			try {
				message = this.stringCache.getStringFieldValue(objectref, "detailMessage");
			} catch (ExecutionException e2) {
				// There is no reason why this should happen.
				message = "";
			}
			throw new ExceptionInInitializerError(
					"Class (" + method.getClassFile().getName() + ") initialization failed since an uncaught exception was thrown: " + type
							+ " (" + message + ")");
		} catch (ExecutionException e) {
			throw new ExceptionInInitializerError(
					"Class initialization failed with an ExecutionException with message: "
							+ e.getMessage() + ".");
		} catch (InvalidInstructionInitialisationException e) {
			throw new ExceptionInInitializerError(
					"Class initialization failed with an InvalidInstructionInitialisationException with message: "
							+ e.getMessage() + ".");
		} catch (InterruptedException e) {
			throw new ExceptionInInitializerError(
					"Class initialization failed with an InterruptedException with message: "
							+ e.getMessage() + ".");
		}
	}

	/**
	 * Getter for the number of frames executed.
	 * @return The number of frames executed.
	 */
	public long getExecutedFrames() {
		return this.executedFrames;
	}

	/**
	 * Getter for the number of instructions executed.
	 * @return The number of instructions executed.
	 */
	public long getExecutedInstructions() {
		return this.executedInstructions;
	}

	/**
	 * Flag the the execution should be paused at the next possibility.
	 */
	public synchronized void pauseExecution() {
		this.pauseExecution = true;
	}

	/**
	 * Flag the the execution should be resumed at the next possibility.
	 */
	public synchronized void resumeExecution() {
		this.pauseExecution = false;
		notify();
	}

	/**
	 * Change the current frame. Never change this.currentFrame/setFrame() directly,
	 * as concrete vm implementations might override this method in order
	 * to keep track of current frame changes.
	 *
	 * @param frame The frame to become the current frame.
	 */
	public void changeCurrentFrame(Frame frame) {
		this.currentFrame = frame;
	}

	/**
	 * Change the current PC. Never change this.pc/setPC() directly,
	 * as concrete vm implementations might override this method in order
	 * to keep track of current PC changes.
	 *
	 * @param pc The PC to become the current PC.
	 */
	public void changeCurrentPC(int pc) {
		this.pc = pc;
	}

	/**
	 * If executing in step by step mode, a number of instructions can be supplied that will be
	 * executed without stepping. While doing so, there is no control about the execution. However,
	 * it runs as fast as possible. Hence, this can be used to inspect the behavior of applications
	 * after an arbitrary number of instructions has already been executed.
	 *
	 * Should the execution already run in this mode, the target number of instruction is updated.
	 *
	 * @param numberOfInstructions The number of instructions to executed without a stop.
	 * @throws IllegalArgumentException If the number of instructions is less than one.
	 * @throws IllegalStateException If step by step mode is disabled.
	 */
	public synchronized void setRunUntilNoOfInstructionsReached(long numberOfInstructions) {
		// This is only allowed for step by step mode.
		if (!this.stepByStepMode)
			throw new IllegalStateException(
					"Setting the number of instructions to be reached before going back to step by step "
							+ "mode can only be set if actualy running step by step mode.");
		// A positive number of instructions is needed.
		if (numberOfInstructions <= 0)
			throw new IllegalArgumentException("The number of instrction must be greater than zero.");

		// Set fast execution mode.
		this.runUntilNoOfInstructionsReached = true;
		this.executedInstructionsTarget = this.executedInstructions + numberOfInstructions;

		// Start the fast execution.
		this.nextStepReady = true;
		notify();
	}

	/**
	 * Stop the fast execution of a number of instructions while being in step by step mode. If
	 * {@link #setRunUntilNoOfInstructionsReached(long)} has not been called earlier, the number of
	 * instructions has already been processed or step by step mode is disabled, this method has no
	 * effect.
	 */
	public void stopRunUntilNoOfInstructionsReached() {
		this.runUntilNoOfInstructionsReached = false;
		this.executedInstructionsTarget = -1L;
	}

	/**
	 * Get the number of instructions left for fast execution. If fast execution is not run, 0 will
	 * be returned.
	 *
	 * @return The number of instructions left for fast execution.
	 */
	public long getRunUntilNoOfInstructionsReached() {
		if (this.runUntilNoOfInstructionsReached && !this.application.getExecutionFinished())
			return this.executedInstructionsTarget - this.executedInstructions;
		return 0L;
	}

	/**
	 * Finalize the VirtualMachine.
	 */
	@Override
	public void finalize() {
		try {
			super.finalize();
		} catch (Throwable t) {
			// Log it, but do nothing.
			if (Globals.getInst().execLogger.isEnabledFor(Level.WARN))
				Globals.getInst().execLogger.warn("Finalizing the virtual machine failed.");
		}
	}

	/**
	 * Generate an instance of Objectref for the specified ClassFile. This will invoke the static
	 * initializers, if that has not been done, yet.
	 * 
	 * @param classFile The class file to get an object reference for.
	 * @return A new instance of objectref for this ClassFile.
	 * @throws ExceptionInInitializerError If class initialization fails.
	 */
	public Objectref getAnObjectref(ClassFile classFile) {
		InitializedClass initializedClass = classFile.getInitializedClass();
		if (initializedClass == null) {
			initializedClass = new InitializedClass(classFile, this);
		}
		return initializedClass.getANewInstance();
	}

	/**
	 * Return the (muggl-java) objectref of the thread object belonging to this thread.
	 * @return
	 */
	public Objectref get_threadObj() {
		return VirtualMachine.threadObj;
	}

	public Objectref getAndInitializeObjectref(InitializedClass class_klass) {
		Objectref ret = getAnObjectref(class_klass.getClassFile());
		try {
			java_call_special(ret, class_klass.getClassFile(), VmSymbols.OBJECT_INITIALIZER_NAME,
					MethodType.methodType(void.class));
			return ret;
		} catch (ExecutionException | InvalidInstructionInitialisationException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isAssertionEnabled() {
		return assertionEnabled;
	}

	/**
	 * Return a String representation of the current MugglVM StackTrace
	 * @return
	 */
	public String currentStackTrace() {
		// within the Muggl VM
		StringBuilder stackT = new StringBuilder();

		stackT.append("at " + this.currentFrame.method.getPackageAndName() + " pc:" + currentFrame.getPc() + " line:"
				+ currentFrame.method.getLineNumberForPC(currentFrame.getPc()).orElse(-1) + System.lineSeparator());
		Frame curF = currentFrame;
		while (curF.invokedBy != null) {
			curF = curF.invokedBy;
			if(curF.method != null)
			stackT.append("\t at " + curF.method.getPackageAndName() + "(" + curF.method.getClassFile().getClassName()
					+ ".java:" + curF.method.getLineNumberForPC(curF.getPcDone()).orElse(-1) + ")" + " CurPc:"
					+ curF.getPc() + System.lineSeparator());
		}

		return stackT.toString();
	}

	public void fillDebugStackTraces() {
		// within the JavaVM
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		new Throwable().printStackTrace(pw);
		this.debugStackTraceJavaVM = sw.toString();

		this.debugStackTraceMugglVM = currentStackTrace();
	}

	public void set_property(Objectref props, String k, String v) throws ExecutionException,
			InvalidInstructionInitialisationException, InterruptedException, ClassFileException {
		// Objectref key = getStringCache().getStringObjectref(k);
		// Objectref value = getStringCache().getStringObjectref((v != null) ? v : "");
		// java_call_virtual(classLoader.getClassAsClassFile("java.util.Hashtable"), VmSymbols.PUT_NAME,
		// VmSymbols.OBJECT_OBJECT_OBJECT_SIGNATURE,
		// new Object[] { props, key, value });

	}

	public void performUniverseGenesis() throws ClassFileException, ExecutionException,
			InvalidInstructionInitialisationException, InterruptedException {
		if (this.universeSetupFinished)
			return;

		// Register native mocks that require a classloader
		NativeSunMiscVM.initialiseAndRegister(this.getClassLoader());
		NativeJavaLangSystem.initialiseAndRegister(this.getClassLoader());

		// prepare a "parent" null frame for the universe genesis
		if (!Options.getInst().symbolicMode) {
			if (this.stack == null) {
				this.stack = new Stack<>();
			}
			Frame frame = new Frame(this);
			this.currentFrame = frame;
			this.stack.push(frame);
			Universe.genesis(this);
			this.stack.clear();
			setUpThreads();
		}else {
//			this.stack = new Stack<Object>();
//			Frame frame = new Frame(this);
//			this.currentFrame = frame;
//			this.stack.push(frame);
//			Universe.genesis(this);
//			this.stack.clear();
//			if(this instanceof SymbolicVirtualMachine){
//				SymbolicVirtualMachine tsVM = (SymbolicVirtualMachine) this;
//				tsVM.doNotProcessSolutions();
//				tsVM.getSolutionProcessor().setDoNotSaveTheNextSolution(true);
//			}
//			setUpThreads();
			Globals.getInst().execLogger.warn("Thread set-up deactivated in symbolic vm");
			
		}
		this.universeSetupFinished = true;
	}
}
