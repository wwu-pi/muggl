package de.wwu.muggl.javaee.jaxws.sym;

import java.util.HashSet;
import java.util.Set;

public class WebService {

	protected String name;
	protected String targetNamespace;
	protected String wsdlLocation;
	protected Set<Port> ports;
	
	public WebService(String name, String targetNamespace, String wsdlLocation) {
		this.name = name;
		this.targetNamespace = targetNamespace;
		this.wsdlLocation = wsdlLocation;
		this.ports = new HashSet<>();
	}

	public void addPort(Port port) {
		this.ports.add(port);
	}

	public String getName() {
		return name;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}
	
	public Set<Port> getPorts() {
		return this.ports;
	}
}
