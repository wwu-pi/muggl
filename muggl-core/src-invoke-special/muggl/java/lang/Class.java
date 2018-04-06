package muggl.java.lang;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="java.lang.Class")
public class Class {

	@InvokeSpecialMethod(name="getPrimitiveClass", signature="(Ljava/lang/String;)Ljava/lang/Class;")
	public static void getPrimitiveClass(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		String type = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[0]);
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine) frame.getVm();
		if(    type.equals("boolean") || type.equals("byte")
			|| type.equals("char") || type.equals("short")
			|| type.equals("int") || type.equals("long")
			|| type.equals("float") || type.equals("double") 
			|| type.equals("void")) {
			
			ClassFile classFile = null;
			try {
				classFile = vm.getClassLoader().getClassAsClassFile("java.lang.Class");
			} catch (ClassFileException e) {
				throw new SpecialMethodInvokeException("Cannot load class", e);
			}
			Objectref classRef = frame.getVm().getAnObjectref(classFile);
			classRef.putField(classFile.getFieldByName("name"), 
					SpecialMethodHelper.getStringObjectref(vm, type));
			frame.getOperandStack().push(classRef);
		} else {
			throw new SpecialMethodInvokeException("Type must be primitive");
		}
	}
	
	@InvokeSpecialMethod(name="forName", signature="(Ljava/lang/String;)Ljava/lang/Class;")
	public static void forName(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		String type = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[0]);
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		try {
			ClassFile classFile = vm.getClassLoader().getClassAsClassFile("java.lang.Class");
			Objectref classRef = vm.getAnObjectref(classFile);
			Field nameField = classFile.getFieldByName("name");
			classRef.putField(nameField, type);
			frame.getOperandStack().push(classRef);
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Cannot generate class reference", e);
		}
	}
	
	@InvokeSpecialMethod(name="getDeclaredField", signature="(Ljava/lang/String;)Ljava/lang/reflect/Field;")
	public static void getDeclaredField(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		try {
			ClassFile classFile = vm.getClassLoader().getClassAsClassFile("java.lang.reflect.Field");
			Objectref fieldRef = vm.getAnObjectref(classFile);
			Field nameField = classFile.getFieldByName("name");
			fieldRef.putField(nameField, parameters[1]);
			frame.getOperandStack().push(fieldRef);
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Cannot execute getDeclaredField", e);
		}
	}
	
	@InvokeSpecialMethod(name="getCanonicalName", signature="()Ljava/lang/String;")
	public static void getCanonicalName(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		SpecialMethodHelper.getStringObjectref(vm, "java.lang.Class");
	}
	
}
