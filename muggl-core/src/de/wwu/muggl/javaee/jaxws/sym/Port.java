package de.wwu.muggl.javaee.jaxws.sym;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jaxws.ex.MugglWebServiceException;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;

public class Port extends ObjectrefVariable {
	
	protected static int counter = 0;

	protected Map<Method, Integer> methodCounterMap; // how often has a method been invoked by this port?
	
	protected String portName;
	protected String namespaceURI;
	protected WebService webService;
	protected Map<Method, LinkedList<Operation>> operations;
	
	public Port(WebService webService, String portName, String namespaceURI, InitializedClass staticReference, SymbolicVirtualMachine vm) {
		super(getPortInternalName(portName), staticReference, vm);
		this.portName = portName;
		this.webService = webService;
		this.webService.addPort(this);
		this.operations = new HashMap<>();
		this.methodCounterMap = new HashMap<>();
	}
	
	public String getPortName() {
		return this.portName;
	}
	
	private static String getPortInternalName(String name) {
		return name + counter++;
	}

	public void addOperation(Operation operation) {
		LinkedList<Operation> opList = operations.get(operation.getMethod());
		if(opList == null) {
			opList = new LinkedList<>();
		}
		opList.add(operation);
		this.operations.put(operation.getMethod(), opList);
	}
	
	public Map<Method, LinkedList<Operation>> getOperationMap() {
		return this.operations;
	}

	public void invoke(Frame frame, Method method, Object[] parameters) {
		Variable output = generateResponseVariable(vm, method.getReturnType(), getResponseName(method));
		
		Operation op = new Operation(method, parameters, output);
		
		LinkedList<Operation> opList = this.operations.get(method);
		if(opList == null) {
			opList = new LinkedList<>();
		}
		opList.add(op);
		this.operations.put(method, opList);
		
		if(!method.getReturnType().equals("void")) {
			frame.getOperandStack().push(output);
		}
	}
	
	private String getResponseName(Method method) {
		Integer count = methodCounterMap.get(method);
		if(count == null) {
			count = new Integer(0);
		}
		methodCounterMap.put(method, (count+1));
		return this.name+"_response_"+method.getName()+"_"+count;
	}

	private Variable generateResponseVariable(SymbolicVirtualMachine vm, String responseType, String responseName) throws MugglWebServiceException {
		switch(responseType) {
			case "int" : return generateNumericVariable(responseName, Expression.INT);
			case "short" : return generateNumericVariable(responseName, Expression.SHORT);
			case "long" : return generateNumericVariable(responseName, Expression.LONG);
			case "float" : return generateNumericVariable(responseName, Expression.FLOAT);
			case "double" : return generateNumericVariable(responseName, Expression.DOUBLE);
			case "boolean" : return generateNumericVariable(responseName, Expression.BOOLEAN);
			case "char" : return generateNumericVariable(responseName, Expression.CHAR);
			case "void" : return null;
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
		
		vm.getSolverManager().addConstraint(NumericEqual.newInstance(objRefVar.getIsNullVariable(), NumericConstant.getZero(Expression.BOOLEAN)));
		
		try {
			if(!vm.getSolverManager().hasSolution()) {
				throw new SpecialMethodInvokeException("No Solution");
			}
		} catch(Exception e) {
			throw new MugglWebServiceException("Error while checking solution", e);
		}
		
		return objRefVar;
	}
}
