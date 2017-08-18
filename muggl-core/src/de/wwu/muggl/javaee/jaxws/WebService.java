//package de.wwu.muggl.javaee.jaxws;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import de.wwu.muggl.solvers.expressions.Variable;
//import de.wwu.muggl.vm.classfile.structures.Method;
//import de.wwu.muggl.vm.initialization.Objectref;
//
//public class WebService {
//
//	/**
//	 * The name of the service, must be of type javax.xml.namespace.QName
//	 */
//	protected Objectref serviceName;
//	
//	/**
//	 * The service class, must be of type java.lang.Class
//	 */
//	protected Objectref serviceClass;
//	
//	/**
//	 * The WSDL location, must be of type java.net.URL
//	 */
//	protected Objectref wsdlLocation;
//	
//	/**
//	 * The set of ports to this web service.
//	 */
//	protected Set<WebServicePort> ports;
//	
//	protected Map<Method, Set<Variable>> methodResponseMap;
//	
//	public WebService(Objectref serviceName, Objectref serviceClass, Objectref wsdlLocation) {
//		this.serviceName = serviceName;
//		this.serviceClass = serviceClass;
//		this.wsdlLocation = wsdlLocation;
//		this.methodResponseMap = new HashMap<>();
//		this.ports = new HashSet<>();
//	}
//	
//	public void addResponse(Method method, Variable resp) {
//		Set<Variable> responses = this.methodResponseMap.get(method);
//		if(responses == null) {
//			responses = new HashSet<>();
//		}
//		responses.add(resp);
//		this.methodResponseMap.put(method, responses);
//	}
//
//	public void addPort(WebServicePort webServicePort) {
//		this.ports.add(webServicePort);
//	}
//
//}
