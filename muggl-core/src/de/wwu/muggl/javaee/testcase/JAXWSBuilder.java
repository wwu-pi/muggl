package de.wwu.muggl.javaee.testcase;

import java.util.LinkedList;
import java.util.Map;

import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jaxws.WebServiceResponse;
import de.wwu.muggl.javaee.jaxws.objref.MugglWSPort;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.symbolic.testCases.JavaEETestCaseSolution;

public class JAXWSBuilder {
	
	protected Map<Integer, LinkedList<WebServiceResponse>> jaxWsResponseMap;
	protected Solution solution;
	
	public JAXWSBuilder(Map<Integer, LinkedList<WebServiceResponse>> jaxWsResponseMap, Solution solution) {
		for(LinkedList<WebServiceResponse> respList : jaxWsResponseMap.values()) {
			for(WebServiceResponse response : respList) {
				MugglWSPort port = response.getPort();
//				port.get
			}
		}
	}
	
	

//	public StringBuilder buildWebService(String packageName, WebService webService, Solution solution) throws SpecialMethodInvokeException {	
//		// package declaration
//		StringBuilder sbPackage = new StringBuilder();
//		sbPackage.append("package ");
//		sbPackage.append(packageName);
//		sbPackage.append(";\n\n");
//		
//		// imports
//		StringBuilder sbImports = new StringBuilder();
//		sbImports.append("import javax.jws.WebService;\n");
//		
//		// @WebService annoation
//		StringBuilder sbWSAnnoation = new StringBuilder();
//		sbWSAnnoation.append("@WebService\n");
//		sbWSAnnoation.append("\tserviceName=\"");
//		sbWSAnnoation.append(SpecialMethodHelper
//				.getStringFromObjectref(webService.getServiceName()));
//		sbWSAnnoation.append("\",\n");
//		sbWSAnnoation.append("\tportName=\"");
//		sbWSAnnoation.append(SpecialMethodHelper
//				.getStringFromObjectref(webService.getPortName()));
//		sbWSAnnoation.append("\",\n");
//		sbWSAnnoation.append("\tendpointInterface=\"");
//		sbWSAnnoation.append(SpecialMethodHelper
//				.getStringFromObjectref(webService.getEndpointInterface()));
//		sbWSAnnoation.append("\",\n");
//		sbWSAnnoation.append("\ttargetNamespace=\"");
//		sbWSAnnoation.append(SpecialMethodHelper
//				.getStringFromObjectref(webService.getTargetNamespace()));
//		sbWSAnnoation.append("\",\n");
//				
//		return new StringBuilder()
//						.append(sbPackage)
//						.append(sbImports)
//						.append(sbWSAnnoation);
//	}

}
