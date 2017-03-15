package de.wwu.muggl.vm;

import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.InitializedClass;

/**
 * Equivalence of openjdk/hotspot/src/share/vm/classfile/systemDictionary.{c|h}pp Singleton
 * 
 * You could see this as dictionary for initialized classes that are often used and that you don't want to get each time
 * by the class loader
 * 
 * @author max
 *
 */
public class SystemDictionary {

	// Singleton
	private static SystemDictionary instance = null;

	private VirtualMachine vm;

	public InitializedClass reflect_Field_klass;

	public InitializedClass Object_klass;
	public InitializedClass Class_klass;
	public InitializedClass reflect_Method_klass;
	public InitializedClass reflect_Constructor_klass;
	public InitializedClass Throwable_klass;

	public InitializedClass boolean_klass;
	public InitializedClass char_klass;
	public InitializedClass string_klass;

	public InitializedClass MethodType_klass;

	protected SystemDictionary(VirtualMachine vm) {
		// = SystemDictionary::initialize(TRAPS)
		if (instance != null) {
			if (instance.vm != vm) {
				// throw new UnsupportedOperationException("hah! VM changed");
			}
			// throw new UnsupportedOperationException("initialize systemDict only once!");
		}
		instance = this;
		instance.vm = vm;

		initializePreloadedClasses();
	}

	/**
	 * Separate creation and initialization.
	 */
	public void initialize() {
	}

	public static SystemDictionary gI() {
		if (instance == null) {
			throw new UnsupportedOperationException("SystemDict not initialized");
		}
		return instance;

	}

	public static boolean isInitialized() {
		return instance != null;
	}

	public boolean Class_klass_loaded() {
		return Class_klass != null;
	}

	@SuppressWarnings("unused")
	private SystemDictionary() {
		// Exists only to defeat instantiation.
	}

	private void initializePreloadedClasses() {

		try {
			ClassFile objCF = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_Object);

			reflect_Field_klass = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_reflect_Field)
					.getTheInitializedClass(vm, true);

			// Anm1: in the static initializer of classloader, the CheckForAssignmentCompat.. will alredy create
			// instances of java/lang/Integer.class, but which are not correctly initialized, i.e.
			// their type not pointing to the right thing. Because when they have been called, the java/lang/class was
			// not initialized, yet.
			Class_klass = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_Class).getTheInitializedClass(vm,
					true);
			// do it twice, because in the first try we might not allocate its mirror (because class_klass is not fully
			// loaded, yet)
			Class_klass = new InitializedClass(Class_klass.getClassFile(), vm);

			// do this after Class
			Universe.initialize_basic_type_mirrors();
			// now that we have the mirrors, we need to re-initialize the basic types, see Anm1 above

			Object_klass = new InitializedClass(objCF, vm, true);

			reflect_Method_klass = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_reflect_Method)
					.getTheInitializedClass(vm, true);
			reflect_Constructor_klass = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_reflect_Constructor)
					.getTheInitializedClass(vm, true);

			Throwable_klass = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_Throwable)
					.getTheInitializedClass(vm, true);

			for (int i = BasicType.T_BOOLEAN.value; i <= BasicType.T_VOID.value; i++) {
				if (VmSymbols.basicType2JavaClassName(VmSymbols.BasicTypeArr[i]) != VmSymbols.ILLEGAL_TYPE) {
					ClassFile cf = vm.classLoader
							.getClassAsClassFile(VmSymbols.basicType2JavaClassName(VmSymbols.BasicTypeArr[i]));
					// In some cases, a particular class is *already initialised* before this happens,
					// e.g. java.lang.Character because of StringCache. This creates problems with class comparisons,
					// e.g. for arraycopy type checks. Therefore, create a new instance only if required.
					cf.getTheInitializedClass(vm, true);
				}
			}

			// char and String had been initialized for the StringCache, but their static initializer not executed. Do
			// this now
			// The above comment is wrong, at least for j.l.Character (indirectly via StringCache::provideStringReference).
			// Anyway, given that this *could be* true sometimes, using getTheInitializedClass gives the opportunity to
			// execute it, if this has not happened yet.
			ClassFile charCF = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_Char);
			char_klass = charCF.getTheInitializedClass(vm, true);

			ClassFile stringCF = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_String);
			string_klass = stringCF.getTheInitializedClass(vm, true);

			ClassFile integerCacheCF = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_Integer_IntegerCache);
			InitializedClass integerCache_klass = integerCacheCF.getTheInitializedClass(vm, true);

					// do not do this here because this will trigger static initialization!
			// ClassFile MethodTypeCF = vm.classLoader.getClassAsClassFile(VmSymbols.java_lang_invoke_MethodType);
			// MethodType_klass = new InitializedClass(MethodTypeCF, vm, true);

		} catch (ClassFileException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public VirtualMachine getVm() {
		return vm;
	}

	public ClassFile resolveOrFail(String signature) {
		// systemDictionary.cpp:171
		try {
			return this.vm.getClassLoader().getClassAsClassFile(signature);
		} catch (ClassFileException e) {
			e.printStackTrace();
		}
		return null;
	}

}
