package muggl.javax.xml.ws;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="javax.xml.ws.Service")
public class Service {

	@InvokeSpecialMethod(name="<init>", signature="(Ljava/net/URL;Ljavax/xml/namespace/QName;)V")
	public static void init(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		Objectref serviceRef = (Objectref)frame.getOperandStack().pop();
		ClassFile serviceClassFile = serviceRef.getInitializedClass().getClassFile();
		Field delegateField = serviceClassFile.getFieldByName("delegate", true);
		Objectref delegateRef = getNewDelegate(vm, (Objectref)parameters[1], (Objectref)parameters[2], serviceRef);
		serviceRef.putField(delegateField, delegateRef);
	}

	private static Objectref getNewDelegate(SymbolicVirtualMachine vm, Objectref urlRef, Objectref qnameRef, Objectref serviceRef) throws SpecialMethodInvokeException {
		ClassFile classFile = null;
		try {
			classFile = vm.getClassLoader().getClassAsClassFile(m.wrapper.javax.xml.ws.spi.ServiceDelegate.class.getName());
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Could not find ServiceDelegate class", e);
		}
		
		Objectref delegateRef = vm.getAnObjectref(classFile);
		
		Field wsdlDocLocField = classFile.getFieldByName("wsdlDocumentLocation");
		Field serviceNameField = classFile.getFieldByName("serviceName");
		Field serviceClassField = classFile.getFieldByName("serviceClass");
		
		delegateRef.putField(wsdlDocLocField, urlRef);
		delegateRef.putField(serviceNameField, qnameRef);
		String serviceClassName = serviceRef.getInitializedClass().getClassFile().getName();
		Objectref serviceClassRef = SpecialMethodHelper.getClassObjectRef(vm, serviceClassName);
		delegateRef.putField(serviceClassField, serviceClassRef);
		
		return delegateRef;
	}

}
