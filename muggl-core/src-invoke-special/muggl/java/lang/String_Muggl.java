package muggl.java.lang;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="java.lang.String")
public class String_Muggl {

	@InvokeSpecialMethod(name="toLowerCase", signature="()Ljava/lang/String;")
	public static void toLowerCase(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Objectref objRef = (Objectref)frame.getOperandStack().pop();
		Field valueField = objRef.getInitializedClass().getClassFile().getFieldByName("value");
		Arrayref value = (Arrayref)objRef.getField(valueField);
		for(int i=0; i<value.length; i++) {
			Object ele = value.getElement(i);
			if(ele instanceof IntConstant) {
				IntConstant ic = (IntConstant)ele;
				int asciiValue = ic.getIntValue();
				if(asciiValue >= 65 && asciiValue <= 90) {
					asciiValue += 32;
					value.putElement(i, IntConstant.getInstance(asciiValue));
				}
			} else {
				throw new RuntimeException("Currently, only constants allowed in lower case conversion");
			}
		}
		frame.getOperandStack().push(objRef);
	}
	
	@InvokeSpecialMethod(name="substring", signature="(I)Ljava/lang/String;")
	public static void substring(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		Objectref stringRef = (Objectref)frame.getOperandStack().pop();
		ClassFile classFile = stringRef.getInitializedClass().getClassFile();
		Field valueField = classFile.getFieldByName("value");
		Arrayref value = (Arrayref)stringRef.getField(valueField);
		int end = value.length;
		int start = ((IntConstant)parameters[1]).getIntValue();
		Objectref subStringRef = getSubStringOjectRef(vm, stringRef, start, end);
		frame.getOperandStack().push(subStringRef);
	}
		
	@InvokeSpecialMethod(name="substring", signature="(II)Ljava/lang/String;")
	public static void substring2(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		Objectref stringRef = (Objectref)frame.getOperandStack().pop();
		if(parameters[1] instanceof IntConstant && parameters[2] instanceof IntConstant) {
			int start = ((IntConstant)parameters[1]).getIntValue();
			int end   = ((IntConstant)parameters[2]).getIntValue();
			Objectref subStringRef = getSubStringOjectRef(vm, stringRef, start, end);
			frame.getOperandStack().push(subStringRef);
		} else {
			throw new SpecialMethodInvokeException("Cannot handle non constant values for range in substring");
		}
	}
	
	protected static Objectref getSubStringOjectRef(SymbolicVirtualMachine vm, Objectref original, int start, int end) {
		ClassFile classFile = original.getInitializedClass().getClassFile();
		Field valueField = classFile.getFieldByName("value");
		Arrayref value = (Arrayref)original.getField(valueField);
		Objectref subStringRef = vm.getAnObjectref(classFile);
		
		Arrayref subStringValue = new Arrayref(value.getReferenceValue(), end-start);
		int idx = 0;
		for(int i=start; i<end; i++) {
			Object ele = value.getElement(i);
			subStringValue.putElement(idx++, ele);
		}
		subStringRef.putField(valueField, subStringValue);
		return subStringRef;
	}
}
