package de.wwu.muggl.vm;

import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * Provides methods to manipulate objectrefs from the Muggl-VM ("outside", not from within the (bytecode) execution)
 * equivalence of openjdk/jdk/src/share/native/java/lang/javaClasses
 * 
 * @author max
 *
 */
public final class JavaClasses {

	public static class java_lang_thread {
		public static void set_thread(Objectref java_thread, Object Thread) {
			// java_thread->address_field_put(_eetop_offset, (address)thread);
			// There is no field in the java object were we could possibly store the reference.
			// In java, we can't get memory adresses of objects.
		}

		public static void set_priority(Objectref java_thread, int prio) {
			ClassFile methodClassFile = java_thread.getInitializedClass().getClassFile();
			Field field = methodClassFile.getFieldByName(VmSymbols.PRIORITY_NAME);
			java_thread.getInitializedClass().putField(field, prio);
		}

		public static void set_thread_status(Objectref java_thread, java.lang.Thread.State runnable) {
			ClassFile methodClassFile = java_thread.getInitializedClass().getClassFile();
			Field field = methodClassFile.getFieldByName(VmSymbols.THREADSTATUS_NAME);
			java_thread.getInitializedClass().putField(field, runnable.ordinal());
		}
	}

}
