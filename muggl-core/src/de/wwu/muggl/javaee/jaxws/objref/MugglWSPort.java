package de.wwu.muggl.javaee.jaxws.objref;

import de.wwu.muggl.javaee.jaxws.MugglWebServiceManager;
import de.wwu.muggl.javaee.jaxws.ex.MugglWebServiceException;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
public class MugglWSPort extends Objectref {
	
	
	/**
	 * The port name (javax.xml.namespace.QName).
	 */
	protected Objectref portName;
	
	/**
	 * The service name (javax.xml.namespace.QName).
	 */
	protected Objectref serviceName;
	
	/**
	 * The service class (java.lang.Class).
	 */
	protected Objectref serviceClass;
	
	/**
	 * The location of the WSDL file (java.net.URL)
	 */
	protected Objectref wsdlLocation;
	
	/**
	 * The service endpoint (java.lang.Class).
	 */
	protected Objectref serviceEndpointInterface;

	/**
	 * The name of this object reference variable
	 */
	protected String name;
		
	/**
	 * 
	 * @param name
	 * @param staticReference
	 * @param portName
	 * @param serviceName
	 * @param serviceClass
	 * @param serviceEndpoint
	 */
	public MugglWSPort(String name, InitializedClass staticReference, 
			Objectref portName, Objectref serviceName, 
			Objectref serviceClass, Objectref serviceEndpointInterface,
			Objectref wsdlLocation) {
		super(staticReference, false);
		this.name = name;
		this.portName = portName;
		this.serviceName = serviceName;
		this.serviceClass = serviceClass;
		this.serviceEndpointInterface = serviceEndpointInterface;
		this.wsdlLocation = wsdlLocation;
	}
	
	/**
	 * Special invocation of methods from a JAX-WS client / port.
	 * @param frame the frame currently being executed
	 * @param methodName the name of the method being invoked
	 * @param methodType the type of the method being invoked
	 */
	public void invoke(Frame frame, Method method) throws MugglWebServiceException {	
		Variable response = MugglWebServiceManager.generateResponse((SymbolicVirtualMachine)frame.getVm(), this, method);		
		frame.getOperandStack().push(response);
	}

	public Objectref getServiceName() {
		return serviceName;
	}

	public Objectref getServiceClass() {
		return serviceClass;
	}

	public Objectref getWsdlLocation() {
		return wsdlLocation;
	}

	public Objectref getServiceEndpointInterface() {
		return serviceEndpointInterface;
	}
	
}
