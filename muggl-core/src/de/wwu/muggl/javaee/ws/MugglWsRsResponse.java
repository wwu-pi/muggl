package de.wwu.muggl.javaee.ws;

import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

public class MugglWsRsResponse extends MugglWsRs {

	protected NumericVariable status;
	
	public MugglWsRsResponse(SymbolicVirtualMachine vm) throws MugglWsRsException {
		super("javax.ws.rs.core.Response", vm);
		this.status = new NumericVariable("Status of Muggl JAX-RS Response (id="+this.initNumber+")", Expression.INT);
	}
	
	public NumericVariable getStatus() {
		return this.status;
	}
	
}
