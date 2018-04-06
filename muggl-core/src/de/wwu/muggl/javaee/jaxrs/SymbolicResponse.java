package de.wwu.muggl.javaee.jaxrs;

import de.wwu.muggl.javaee.ws.MugglRESTResponse;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.GreaterOrEqual;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;

public class SymbolicResponse extends MugglRESTResponse {

	protected NumericVariable status;
	
	protected Object entity;
	
	public SymbolicResponse(String name, SymbolicVirtualMachine vm) {
		super(name, getReference(vm), vm);
		this.status = new NumericVariable(this.name + ".status", Expression.INT);
		vm.getSolverManager().addConstraint(GreaterOrEqual.newInstance(this.status, IntConstant.getInstance(100)));
	}

	private static InitializedClass getReference(SymbolicVirtualMachine vm) {
		try {
			ClassFile cf = vm.getClassLoader().getClassAsClassFile("javax.ws.rs.core.Response");
			InitializedClass ic = new InitializedClass(cf, vm);
			return ic;
		} catch(Exception e) {
			throw new RuntimeException("Could not load JAX-RS Response Class", e);
		}
	}

	public NumericVariable getStatus() {
		return status;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}
}
