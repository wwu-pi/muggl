package de.wwu.muggl.javaee.jaxws.sym;

import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.vm.classfile.structures.Method;

public class Operation {

	protected Method method;
	protected Object[] input;
	protected Variable output;
	
	public Operation(Method method, Object[] input, Variable output) {
		this.method = method;
		this.input = input;
		this.output = output;
	}
	
	public Method getMethod() {
		return this.method;
	}
	
	public Variable getOutput() {
		return this.output;
	}
}