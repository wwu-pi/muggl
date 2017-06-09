package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.invoke.SpecialMethodUtil;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

public class ClassGetComponentType implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Object obj = frame.getOperandStack().pop();
		if(obj instanceof Objectref) {
			Objectref objRef = (Objectref)obj;
			Field nameField = objRef.getInitializedClass().getClassFile().getFieldByName("name");
			Objectref nameValueRef = (Objectref)objRef.getField(nameField);
			Field nameValueArrayField = nameValueRef.getInitializedClass().getClassFile().getFieldByName("value");
			Arrayref nameValueArray = (Arrayref)nameValueRef.getField(nameValueArrayField);
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<nameValueArray.length; i++) {
				Object element = nameValueArray.getElement(i);
				if(element instanceof Objectref) {
					// must be a character object reference
					Objectref eleObjRef = (Objectref)element;
					Field charValueField = eleObjRef.getInitializedClass().getClassFile().getFieldByName("value");
					Object charValue = eleObjRef.getField(charValueField);
					if(charValue instanceof Character) {
						Character c = (Character)charValue;
						sb.append(c);
					} else {
						throw new SpecialMethodInvokeException("Exepected Character, got: " + charValue);
					}
				} else {
					throw new SpecialMethodInvokeException("Currently only Object references of type character supported for the symbolic name");
				}
			}
			String name = sb.toString();
			if(name.startsWith("[")) {
				// its an array type
				if(name.startsWith("[[")) {
					throw new SpecialMethodInvokeException("Multidimensional arrays not handled yet");
				}
				String componentName = name.substring(2,name.length()-1);
				Objectref classRef = SpecialMethodUtil.getInstance().getClassObjectRef((SymbolicVirtualMachine)frame.getVm(), componentName);
				frame.getOperandStack().push(classRef);
			} else {
				// its not an array type -> no component type -> push null onto stack
				frame.getOperandStack().push(null);
			}
		}
	}


}
