package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.vm.Frame;

/**
 * A special method invocation for:
 * javax.persistence.EntityManager#find(Class,Object)
 * @author Andreas Fuchs
 *
 */
public class MugglEntityManagerFind implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame) {
		frame.getOperandStack().pop();
		frame.getOperandStack().push(null);
	}
	
}
