package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.invoke.SpecialMethodUtil;
import de.wwu.muggl.javaee.ws.MugglWsRsTarget;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.initialization.Objectref;

public class WsRsTargetPath implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Object obj = frame.getOperandStack().pop();
		if(!(obj instanceof MugglWsRsTarget)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type MugglWsRsTarget, but was: " + obj);
		}
		
		MugglWsRsTarget target = (MugglWsRsTarget)obj;
		
		Object pathObj = parameters[1]; // must be an object reference to a String
		if(!(pathObj instanceof Objectref
			&& ((Objectref)pathObj).getInitializedClass().getClassFile().getName().equals(String.class.getName()))) {
			throw new SpecialMethodInvokeException("Expected parameter to be of type Objectref and to be a reference to a String");
		}

		String path = SpecialMethodUtil.getInstance().getStringFromObjectref((Objectref)pathObj);
		
		target.setPath(path);
		
		frame.getOperandStack().push(target);
	}

}
