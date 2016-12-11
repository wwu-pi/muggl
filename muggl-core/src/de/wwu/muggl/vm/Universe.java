package de.wwu.muggl.vm;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.JavaClasses.java_lang_Class;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * Universe is a name space holding known system classes and objects in the VM.
 * 
 * Loaded classes are accessible through the SystemDictionary.
 * 
 * The object heap is allocated and accessed through Universe, and various allocation support is provided.
 * 
 * @see universe.hpp in OpenJDK
 **/
public class Universe {
	/**
	 * The Main Thread Group. Openjdk stores this in Universe.
	 */
	private static Objectref main_thread_group = null;
	private static Objectref system_thread_group = null;

	private static Objectref _int_mirror;
	private static Objectref _float_mirror;
	private static Objectref _double_mirror;
	private static Objectref _byte_mirror;
	private static Objectref _bool_mirror;
	private static Objectref _char_mirror;
	private static Objectref _long_mirror;
	private static Objectref _short_mirror;
	private static Objectref _void_mirror;
	private static Objectref[] _mirrors = new Objectref[de.wwu.muggl.vm.VmSymbols.BasicType.T_VOID.value + 1];

	private static ClassFile[] _typeArrayKlassObjs = new ClassFile[de.wwu.muggl.vm.VmSymbols.BasicType.T_VOID.value
			+ 1];
//	private static ClassFile _boolArrayKlassObj;
//	private static ClassFile _charArrayKlassObj;
//	private static ClassFile _singleArrayKlassObj;
//	private static ClassFile _doubleArrayKlassObj;
//	private static ClassFile _byteArrayKlassObj;
//	private static ClassFile _shortArrayKlassObj;
//	private static ClassFile _intArrayKlassObj;
//	private static ClassFile _longArrayKlassObj;

	public static void genesis(VirtualMachine vm) {

		// _boolArrayKlassObj = TypeArrayKlass.create_klass(T_BOOLEAN, sizeof(jboolean));
		// _charArrayKlassObj = TypeArrayKlass.create_klass(T_CHAR, sizeof(jchar));
		// _singleArrayKlassObj = TypeArrayKlass.create_klass(T_FLOAT, sizeof(jfloat));
		// _doubleArrayKlassObj = TypeArrayKlass.create_klass(T_DOUBLE, sizeof(jdouble));
		// _byteArrayKlassObj = TypeArrayKlass.create_klass(T_BYTE, sizeof(jbyte));
		// _shortArrayKlassObj = TypeArrayKlass.create_klass(T_SHORT, sizeof(jshort));
		// _intArrayKlassObj = TypeArrayKlass.create_klass(T_INT, sizeof(jint));
		// _longArrayKlassObj = TypeArrayKlass.create_klass(T_LONG, sizeof(jlong));
		//
		// _typeArrayKlassObjs[BasicType.T_BOOLEAN.value] = _boolArrayKlassObj;
		// _typeArrayKlassObjs[BasicType.T_CHAR.value] = _charArrayKlassObj;
		// _typeArrayKlassObjs[BasicType.T_FLOAT.value] = _singleArrayKlassObj;
		// _typeArrayKlassObjs[BasicType.T_DOUBLE.value] = _doubleArrayKlassObj;
		// _typeArrayKlassObjs[BasicType.T_BYTE.value] = _byteArrayKlassObj;
		// _typeArrayKlassObjs[BasicType.T_SHORT.value] = _shortArrayKlassObj;
		// _typeArrayKlassObjs[BasicType.T_INT.value] = _intArrayKlassObj;
		// _typeArrayKlassObjs[BasicType.T_LONG.value] = _longArrayKlassObj;
		//
		// ClassLoaderData* null_cld = ClassLoaderData.the_null_class_loader_data();
		//
		// _the_array_interfaces_array = MetadataFactory.new_array<Klass*>(null_cld, 2, NULL);
		// _the_empty_int_array = MetadataFactory.new_array<int>(null_cld, 0);
		// _the_empty_short_array = MetadataFactory.new_array<u2>(null_cld, 0);
		// _the_empty_method_array = MetadataFactory.new_array<Method*>(null_cld, 0);
		// _the_empty_klass_array = MetadataFactory.new_array<Klass*>(null_cld, 0);

		VmSymbols.initialize();

		new SystemDictionary(vm);

		// Klass* ok = SystemDictionary.Object_klass();
		//
		// _the_null_string = StringTable.intern("null");
		// _the_min_jint_string = StringTable.intern("-2147483648");
		//
		// if (UseSharedSpaces) {
		// // Verify shared interfaces array.
		// assert(_the_array_interfaces_array->at(0) ==
		// SystemDictionary.Cloneable_klass(), "u3");
		// assert(_the_array_interfaces_array->at(1) ==
		// SystemDictionary.Serializable_klass(), "u3");
		// } else {
		// // Set up shared interfaces array. (Do this before supers are set up.)
		// _the_array_interfaces_array->at_put(0, SystemDictionary.Cloneable_klass());
		// _the_array_interfaces_array->at_put(1, SystemDictionary.Serializable_klass());
		// }
		//
		// initialize_basic_type_klass(_boolArrayKlassObj);
		// initialize_basic_type_klass(_charArrayKlassObj);
		// initialize_basic_type_klass(_singleArrayKlassObj);
		// initialize_basic_type_klass(_doubleArrayKlassObj);
		// initialize_basic_type_klass(_byteArrayKlassObj);
		// initialize_basic_type_klass(_shortArrayKlassObj);
		// initialize_basic_type_klass(_intArrayKlassObj);
		// initialize_basic_type_klass(_longArrayKlassObj);
	}

	// private static void initialize_basic_type_klass(ClassFile classFile) {
	// SystemDictionary.gI().Object_klass
	// Klass* ok = SystemDictionary::Object_klass();
	// if (UseSharedSpaces) {
	// assert(k->super() == ok, "u3");
	// k->restore_unshareable_info(CHECK);
	// } else {
	// k->initialize_supers(ok, CHECK);
	// }
	// k->append_to_sibling_list();
	//
	// }

	public static Objectref get_main_thread_group() {
		return main_thread_group;
	}

	public static void set_main_thread_group(Objectref main_thread_group) {
		Universe.main_thread_group = main_thread_group;
	}

	public static Objectref get_system_thread_group() {
		return system_thread_group;
	}

	public static void set_system_thread_group(Objectref system_thread_group) {
		Universe.system_thread_group = system_thread_group;
	}

	public static ClassFile checkMirror(ClassFile m) {
		if (m == null) {
			Globals.getInst().execLogger.warn("m === null");
		}
		return m;
	}

	public static void initialize_basic_type_mirrors() {
		// do not assert anymore - we will just re-create the whole universe
		// if SystemDict asks us - might be that we are now living with another VM, e.g. if you execute multiple tests
		// in a row
		// FIXME: make Universe living in VM instead of being static?
		// assert (_int_mirror == null);// "basic type mirrors already initialized");
		_int_mirror = java_lang_Class.createBasicTypeMirror("int", VmSymbols.BasicType.T_INT);

		_float_mirror = java_lang_Class.createBasicTypeMirror("float", VmSymbols.BasicType.T_FLOAT);
		_double_mirror = java_lang_Class.createBasicTypeMirror("double", VmSymbols.BasicType.T_DOUBLE);
		_byte_mirror = java_lang_Class.createBasicTypeMirror("byte", VmSymbols.BasicType.T_BYTE);
		_bool_mirror = java_lang_Class.createBasicTypeMirror("boolean", VmSymbols.BasicType.T_BOOLEAN);
		_char_mirror = java_lang_Class.createBasicTypeMirror("char", VmSymbols.BasicType.T_CHAR);
		_long_mirror = java_lang_Class.createBasicTypeMirror("long", VmSymbols.BasicType.T_LONG);
		_short_mirror = java_lang_Class.createBasicTypeMirror("short", VmSymbols.BasicType.T_SHORT);
		_void_mirror = java_lang_Class.createBasicTypeMirror("void", VmSymbols.BasicType.T_VOID);

		_mirrors[VmSymbols.BasicType.T_INT.value] = _int_mirror;
		_mirrors[VmSymbols.BasicType.T_FLOAT.value] = _float_mirror;
		_mirrors[VmSymbols.BasicType.T_DOUBLE.value] = _double_mirror;
		_mirrors[VmSymbols.BasicType.T_BYTE.value] = _byte_mirror;
		_mirrors[VmSymbols.BasicType.T_BOOLEAN.value] = _bool_mirror;
		_mirrors[VmSymbols.BasicType.T_CHAR.value] = _char_mirror;
		_mirrors[VmSymbols.BasicType.T_LONG.value] = _long_mirror;
		_mirrors[VmSymbols.BasicType.T_SHORT.value] = _short_mirror;
		_mirrors[VmSymbols.BasicType.T_VOID.value] = _void_mirror;
		// _mirrors[T_OBJECT] = InstanceKlass.cast(_object_klass)->java_mirror();
		// _mirrors[T_ARRAY] = InstanceKlass.cast(_object_klass)->java_mirror();
	}

	public static Objectref java_mirror(BasicType type) {
		if (_int_mirror == null)
			Globals.getInst().execLogger.warn("mirrors not initialized!");
		return _mirrors[type.value];
	}

	public static ClassFile typeArrayKlassObj(BasicType type) {
		return _typeArrayKlassObjs[type.value];
	}

}
