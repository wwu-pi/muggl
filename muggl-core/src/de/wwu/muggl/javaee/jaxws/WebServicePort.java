//package de.wwu.muggl.javaee.jaxws;
//
//import de.wwu.muggl.vm.initialization.InitializedClass;
//import de.wwu.muggl.vm.initialization.Objectref;
//
//public class WebServicePort extends Objectref {
//
//	/**
//	 * The web service for this port.
//	 */
//	protected WebService webService;
//	
//	/**
//	 * The name of this port, must be of type javax.xml.namespace.QName.
//	 */
//	protected Objectref portName;
//	
//	public WebServicePort(WebService webService, Objectref portName, InitializedClass reference) {
//		super(reference, false);
//		this.webService = webService;
//		this.portName = portName;
//		this.webService.addPort(this);
//	}
//}
