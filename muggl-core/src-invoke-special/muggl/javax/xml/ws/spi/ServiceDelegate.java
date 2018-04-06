package muggl.javax.xml.ws.spi;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jaxws.WebServiceManager;
import de.wwu.muggl.javaee.jaxws.objref.MugglWSPort;
import de.wwu.muggl.javaee.jaxws.sym.Port;
import de.wwu.muggl.javaee.jaxws.sym.WebService;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="javax.xml.ws.spi.ServiceDelegate")
public class ServiceDelegate {
	
	protected static Map<String, Integer> generatedPortCounter = new HashMap<>();

	@InvokeSpecialMethod(name="getPort", signature="(Ljavax/xml/namespace/QName;Ljava/lang/Class;)Ljava/lang/Object;")
	public static void getPort(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		
		
		Objectref portName = (Objectref)parameters[1];
		Objectref serviceEndpointInterface = (Objectref)parameters[2];
		
		
		Objectref delegateRef = (Objectref)frame.getOperandStack().pop();
		ClassFile classFile = null;
		try {
			classFile = vm.getClassLoader().getClassAsClassFile(m.wrapper.javax.xml.ws.spi.ServiceDelegate.class.getName());
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Could not find ServiceDelegate class", e);
		}
		Field wsdlDocLocField = classFile.getFieldByName("wsdlDocumentLocation");
		Field serviceNameField = classFile.getFieldByName("serviceName");
		Field serviceClassField = classFile.getFieldByName("serviceClass");
		Objectref wsdlLocation = (Objectref)delegateRef.getField(wsdlDocLocField);
		Objectref serviceName = (Objectref)delegateRef.getField(serviceNameField);
		Objectref serviceClass = (Objectref)delegateRef.getField(serviceClassField);
		
//		MugglWSPort portRef = getWSPortObjectref(vm, serviceName, serviceClass, portName, serviceEndpointInterface, wsdlLocation);
		
		
		WebService webService = getWebService(vm, serviceName, wsdlLocation);
		Port port = getPort(webService, portName, serviceEndpointInterface, vm);
		
		// port reference cannot be null
		vm.getSolverManager().addConstraint(NumericEqual.newInstance(port.getIsNullVariable(), NumericConstant.getZero(Expression.BOOLEAN)));
		
		try {
			if(!vm.getSolverManager().hasSolution()) {
				throw new SpecialMethodInvokeException("No Solution");
			}
		} catch(Exception e) {
			throw new SpecialMethodInvokeException("Error while checking solution", e);
		}
		
		frame.getOperandStack().push(port);
	}

	protected static WebService getWebService(SymbolicVirtualMachine vm, Objectref serviceQNameRef, Objectref wsdlLocationRef) throws SpecialMethodInvokeException {
		ClassFile qnameClassFile = serviceQNameRef.getInitializedClass().getClassFile();
		Field nsField = qnameClassFile.getFieldByName("namespaceURI");
		Field nameField = qnameClassFile.getFieldByName("localPart");
		String serviceName = SpecialMethodHelper.getStringFromObjectref((Objectref)serviceQNameRef.getField(nameField));
		String targetNamespace = SpecialMethodHelper.getStringFromObjectref((Objectref)serviceQNameRef.getField(nsField));
		
		ClassFile wsdlClassFile = wsdlLocationRef.getInitializedClass().getClassFile();
		Field protocolField = wsdlClassFile.getFieldByName("protocol");
		String protocol = SpecialMethodHelper.getStringFromObjectref((Objectref)wsdlLocationRef.getField(protocolField));
		Field hostField = wsdlClassFile.getFieldByName("host");
		String host = SpecialMethodHelper.getStringFromObjectref((Objectref)wsdlLocationRef.getField(hostField));
		Field portField = wsdlClassFile.getFieldByName("port");
		int port = ((NumericConstant)wsdlLocationRef.getField(portField)).getIntValue();
		Field fileField = wsdlClassFile.getFieldByName("file");
		String file = SpecialMethodHelper.getStringFromObjectref((Objectref)wsdlLocationRef.getField(fileField));
		String wsdlLocation = protocol + "://" + host + ":" + port + file;
		
		return WebServiceManager.getInstance().getWebService(serviceName, targetNamespace, wsdlLocation);
	}
	
	protected static Port getPort(WebService webService, Objectref portQName, Objectref serviceEndpointInterface, SymbolicVirtualMachine vm) throws SpecialMethodInvokeException {
		ClassFile qnamePortName = portQName.getInitializedClass().getClassFile();
		Field namespaceField = qnamePortName.getFieldByName("namespaceURI");
		Field portNameField = qnamePortName.getFieldByName("localPart");
		String portName = SpecialMethodHelper.getStringFromObjectref((Objectref)portQName.getField(portNameField));
		String namespaceURI = SpecialMethodHelper.getStringFromObjectref((Objectref)portQName.getField(namespaceField));
		
		String type = SpecialMethodHelper.getClassNameFromObjectRef(serviceEndpointInterface);

		for(Port p : webService.getPorts()) {
			if(p.getPortName().equals(portName)) {
				return p;
			}
		}
		
		Port port = new Port(webService, portName, namespaceURI, getEndpointInitializedClass(vm, type), vm);
		
		return port;
	}
	
	
	
	protected static MugglWSPort getWSPortObjectref(
			SymbolicVirtualMachine vm, Objectref serviceName, Objectref serviceClass,
			Objectref portName,	Objectref serviceEndpointInterface, Objectref wsdlLocation) throws SpecialMethodInvokeException {
		
		String type = SpecialMethodHelper.getClassNameFromObjectRef(serviceEndpointInterface);
		InitializedClass initializedClass = getEndpointInitializedClass(vm, type);
		
		String portVariableName = getPortVariableName(type);
		
		MugglWSPort portRef = new MugglWSPort(portVariableName, initializedClass, 
				portName, serviceName, serviceClass, serviceEndpointInterface, wsdlLocation);
		return portRef;
	}
	
	private static String getPortVariableName(String type) {
		Integer c = generatedPortCounter.get(type);
		if(c == null) {
			c = new Integer(0);
		}
		generatedPortCounter.put(type, (c+1));
		return type + c;
	}

	protected static InitializedClass getEndpointInitializedClass(SymbolicVirtualMachine vm, String type) throws SpecialMethodInvokeException {
		ClassFile classFile = null;
		try {
			classFile = vm.getClassLoader().getClassAsClassFile(type);
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Could not load port type for delegate", e);
		}		
		InitializedClass initializedClass = classFile.getInitializedClass();
		if (initializedClass == null) {
			initializedClass = new InitializedClass(classFile, vm);
		}
		return initializedClass;
	}
	
}
