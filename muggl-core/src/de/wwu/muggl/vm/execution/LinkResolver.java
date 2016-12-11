package de.wwu.muggl.vm.execution;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.JavaClasses.java_lang_invoke_MethodType;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.Objectref;


public class LinkResolver {
	public static CallInfo resolve_static_call(Frame frame, ClassFile defc, Objectref name, Objectref type, Objectref caller,
			boolean checkAccess, boolean initializeClass) {

		ResolutionAlgorithms resoluton = new ResolutionAlgorithms(frame.getVm().getClassLoader());
		String methodName = frame.getVm().getStringCache().getStringObjrefValue(name);
		String methodType = java_lang_invoke_MethodType.as_signature(type, false);
		Method m = null;
		try {
			m =  resoluton.resolveMethod(defc, new String[]{ methodName, methodType.replace('.', '/')});
		} catch (ClassFileException e) {
			e.printStackTrace();
		}
		CallInfo ret = new CallInfo();
		ret.set_static(m.getClassFile(),m);
		return ret;
	}

	public static CallInfo resolve_virtual_call(Frame frame, ClassFile receiver, ClassFile resolved, Objectref name, Objectref type,
			Objectref caller, boolean checkAccess, boolean checkNullAndAbstract) {
		
		ResolutionAlgorithms resoluton = new ResolutionAlgorithms(frame.getVm().getClassLoader());
		String methodName = frame.getVm().getStringCache().getStringObjrefValue(name);
		String methodType = java_lang_invoke_MethodType.as_signature(type, false);
		Method m = null;
		try {
			m =  resoluton.resolveMethod(receiver, new String[]{ methodName, methodType.replace('.', '/')});
		} catch (ClassFileException e) {
			e.printStackTrace();
		}
		CallInfo ret = new CallInfo();
		ret.set_virtual(m.getClassFile(),receiver,m,m,1);
		return ret;
		
	}

	public static Field resolve_field(Frame frame, ClassFile receiver, Objectref name, Objectref type, Objectref caller,
			Bytecodes nop, boolean checkAcces, boolean initializeClass) {
		
		String fieldName = frame.getVm().getStringCache().getStringObjrefValue(name);
		// TODO: check access, etc.
		return receiver.getInitializedClass().getClassFile().getFieldByName(fieldName);
		
	}

}
