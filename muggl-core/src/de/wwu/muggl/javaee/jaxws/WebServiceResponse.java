package de.wwu.muggl.javaee.jaxws;

import de.wwu.muggl.javaee.jaxws.objref.MugglWSPort;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.vm.classfile.structures.Method;

public class WebServiceResponse {
	protected MugglWSPort port;
	protected Method method;
	protected Variable responseVar;
	public WebServiceResponse(MugglWSPort port, Method method, Variable responseVar) {
		this.port = port;
		this.method = method;
		this.responseVar = responseVar;
	}
	
	public MugglWSPort getPort() {
		return port;
	}
	public Method getMethod() {
		return method;
	}
	public Variable getResponseVar() {
		return responseVar;
	}
	
	@Override
	public String toString() {
		return port + " " + method + " " + responseVar;
	}
	
	
}
