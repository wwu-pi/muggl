package muggl.javax.ws.rs.client;

import java.util.Stack;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jaxrs.SymbolicResponse;
import de.wwu.muggl.javaee.rest.RequestType;
import de.wwu.muggl.javaee.ws.MugglRESTResponse;
import de.wwu.muggl.javaee.ws.MugglWsRsInvocationBuilder;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="javax.ws.rs.client.Invocation$Builder")
public class Invocation_Builder {

	private static int resourceCounter = 0;
	private static int responseCounter = 0;
	
	@InvokeSpecialMethod(name="get", signature="(Ljava/lang/Class;)Ljava/lang/Object;")
	public static void get(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsInvocationBuilder builder = getBuilder(frame.getOperandStack());
		
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		String name = getResourceName();
		String className = SpecialMethodHelper.getClassNameFromObjectRef((Objectref)parameters[1]);
		MugglRESTResponse response = generateResponse(vm, name, className);
		
		builder.getRESTResource().setResponse(response);
		builder.getRESTResource().setRequestType(RequestType.GET);
		
		frame.getOperandStack().push(response);
	}
	
	@InvokeSpecialMethod(name="post", signature="(Ljavax/ws/rs/client/Entity;Ljava/lang/Class;)Ljava/lang/Object;")
	public static void post(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsInvocationBuilder builder = getBuilder(frame.getOperandStack());
		
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		String name = getResourceName();
		String className = SpecialMethodHelper.getClassNameFromObjectRef((Objectref)parameters[2]);
		
		MugglRESTResponse response = null;
		if(className.equals("javax.ws.rs.core.Response")) {
			response = new SymbolicResponse(name, vm);
		} else {
			response = generateResponse(vm, name, className);
		}
		
		builder.getRESTResource().setResponse(response);
		builder.getRESTResource().setRequestType(RequestType.POST);
		
		frame.getOperandStack().push(response);
	}
	
	@InvokeSpecialMethod(name="post", signature="(Ljavax/ws/rs/client/Entity;)Ljavax/ws/rs/core/Response;")
	public static void post2(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsInvocationBuilder builder = getBuilder(frame.getOperandStack());
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		String name = getResponseName();
		
		SymbolicResponse response = new SymbolicResponse(name, vm);
		
		builder.getRESTResource().setResponse(response);
		builder.getRESTResource().setRequestType(RequestType.POST);
		
		frame.getOperandStack().push(response);
	}
	
	protected static MugglRESTResponse generateResponse(SymbolicVirtualMachine vm, String name, String responseClassName) throws SpecialMethodInvokeException {
		try {
			ClassFile classFile = vm.getClassLoader().getClassAsClassFile(responseClassName);
			InitializedClass ic = vm.getAnObjectref(classFile).getInitializedClass();
			MugglRESTResponse response = new MugglRESTResponse(name, ic, vm);
//			// set the response object reference to be NOT null
//			vm.getSolverManager().addConstraint(NumericEqual.newInstance(
//					response.getIsNullVariable(), NumericConstant.getZero(Expression.BOOLEAN)));
			return response;
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Could not generate REST response object reference", e);
		} 
	}
	
	protected static MugglWsRsInvocationBuilder getBuilder(Stack<Object> stack) throws SpecialMethodInvokeException {
		Object obj = stack.pop();
		if(!(obj instanceof MugglWsRsInvocationBuilder)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type MugglWsRsInvocationBuilder, but was: " + obj);
		}
		return (MugglWsRsInvocationBuilder)obj;
	}

	private static String getResourceName() {
		return "rest-resource" + ++resourceCounter;
	}
	
	private static String getResponseName() {
		return "rest-response" + ++responseCounter;
	}
}
