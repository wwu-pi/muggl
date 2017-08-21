package de.wwu.muggl.javaee.jaxws;

import java.util.HashSet;
import java.util.Set;

import de.wwu.muggl.javaee.jaxws.sym.WebService;

public class WebServiceManager {

	private static WebServiceManager inst;
	
	protected Set<WebService> webSerivceSet;
	
	private WebServiceManager() {
		this.webSerivceSet = new HashSet<>();
	}
	
	public synchronized static WebServiceManager getInstance() {
		if(inst == null) {
			inst = new WebServiceManager();
		}
		return inst;
	}

	public WebService getWebService(String serviceName, String targetNamespace, String wsdlLocation) {
		for(WebService webService : this.webSerivceSet) {
			if(webService.getName().equals(serviceName)
				&& webService.getTargetNamespace().equals(targetNamespace)
				&& webService.getWsdlLocation().equals(wsdlLocation)) {
				return webService;
			}
		}
		WebService webService = new WebService(serviceName, targetNamespace, wsdlLocation);
		this.webSerivceSet.add(webService);
		return webService;
	}
	
	public Set<WebService> getWebServiceSet() {
		return this.webSerivceSet;
	}
}
