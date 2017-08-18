package muggl.javax.xml.ws.spi;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jaxws.objref.MugglWSPort;
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
		
		MugglWSPort portRef = getWSPortObjectref(vm, serviceName, serviceClass, portName, serviceEndpointInterface, wsdlLocation);
		
		frame.getOperandStack().push(portRef);
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
