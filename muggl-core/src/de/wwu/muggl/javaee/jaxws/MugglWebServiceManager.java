package de.wwu.muggl.javaee.jaxws;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.wwu.muggl.javaee.jaxws.ex.MugglWebServiceException;
import de.wwu.muggl.javaee.jaxws.objref.MugglWSPort;
import de.wwu.muggl.javaee.jaxws.sym.WebService;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
public class MugglWebServiceManager {
	
	protected static int counter = 0;
	
	protected static Set<WebService> webServiceSet = new HashSet<>();
	
	protected static Map<Integer, LinkedList<WebServiceResponse>> responseMap = new HashMap<>();
	
	public static Variable generateResponse(SymbolicVirtualMachine vm, MugglWSPort port, Method method) {
		int constraintLevel = vm.getSolverManager().getConstraintLevel();
		String responseName = port.getName()+"_"+method.getName()+"_"+(counter++);
		Variable responseVar = generateResponseVariable(vm, method.getReturnType(), responseName);	
		
		LinkedList<WebServiceResponse> responses = responseMap.get(constraintLevel);
		if(responses == null) {
			responses = new LinkedList<>();
		}
		responses.add(new WebServiceResponse(port, method, responseVar));
		responseMap.put(constraintLevel, responses);
		
		
		
		
		// ------------------------
//		
//		WebService webService = getWebService(port.get
//		
		// ------------------------
		
		
		
		return responseVar;
	}
	
//	public static WebService getWebService(Objectref serviceName, Objectref targetNamespace, Objectref wsdlLocation) {
//		for(WebService webService : webServiceSet) {
//			if( webService.getName().equals(serviceName)
//				&& webService.getTargetNamespace().equals(targetNamespace)
//				&& webService.getWsdlLocation().equals(wsdlLocation)) {
//				return webService;
//			}
//		}
//		
//		WebService webService = new WebService(serviceName, targetNamespace, wsdlLocation);
//		webServiceSet.add(webService);
//		
//		return webService;
//	}
	
	
	public static void resetResponseMap(int constraintLevel) {
		for(Integer level : responseMap.keySet()) {
			if(level > constraintLevel) {
				responseMap.remove(level);
			}
		}
	}
	
	public static Map<Integer, LinkedList<WebServiceResponse>> getResponseMap() {
		return responseMap;
	}
	
	private static Variable generateResponseVariable(SymbolicVirtualMachine vm, String responseType, String responseName) throws MugglWebServiceException {
		switch(responseType) {
			case "int" : return generateNumericVariable(responseName, Expression.INT);
			case "short" : return generateNumericVariable(responseName, Expression.SHORT);
			case "long" : return generateNumericVariable(responseName, Expression.LONG);
			case "float" : return generateNumericVariable(responseName, Expression.FLOAT);
			case "double" : return generateNumericVariable(responseName, Expression.DOUBLE);
			case "boolean" : return generateNumericVariable(responseName, Expression.BOOLEAN);
			case "char" : return generateNumericVariable(responseName, Expression.CHAR);
			default : return generateObjectrefResponse(vm, responseName, responseType);
		}
	}

	private static NumericVariable generateNumericVariable(String responseName, byte type) {
		return new NumericVariable(responseName, type);
	}

	private static Variable generateObjectrefResponse(SymbolicVirtualMachine vm, String responseName, String responseType) throws MugglWebServiceException {
		ClassFile classFile = null;
		try {
			classFile = vm.getClassLoader().getClassAsClassFile(responseType);
		} catch (ClassFileException e) {
			throw new MugglWebServiceException("Could not generate response type." , e);
		}
		
		InitializedClass initializedClass = classFile.getInitializedClass();
		if (initializedClass == null) {
			initializedClass = new InitializedClass(classFile, vm);
		}
		
		ObjectrefVariable objRefVar = new ObjectrefVariable(responseName, initializedClass, vm);
		
		return objRefVar;
	}
	
	
}
