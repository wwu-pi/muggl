package muggl.javax.ws.rs.client;

import java.util.Stack;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.javaee.ws.MugglWsRsInvocationBuilder;
import de.wwu.muggl.javaee.ws.MugglWsRsTarget;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="javax.ws.rs.client.WebTarget")
public class WebTarget {

	@InvokeSpecialMethod(name="path", signature="(Ljava/lang/String;)Ljavax/ws/rs/client/WebTarget;")
	public static void path(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsTarget originalTarget = getWebTarget(frame.getOperandStack());
		
		MugglWsRsTarget target = null;
		try {
			target = new MugglWsRsTarget(originalTarget);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Could not generate MugglWsRsTarget", e);
		}
		String path = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[1]);
		target.setPath(path);
		frame.getOperandStack().push(target);
	}
	
	@InvokeSpecialMethod(name="resolveTemplate", signature="(Ljava/lang/String;Ljava/lang/Object;)Ljavax/ws/rs/client/WebTarget;")
	public static void resolveTemplate(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsTarget originalTarget = getWebTarget(frame.getOperandStack());
		
		MugglWsRsTarget target = null;
		try {
			target = new MugglWsRsTarget(originalTarget);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Could not generate MugglWsRsTarget", e);
		}
		String name = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[1]);
		Object value = parameters[2];
		target.setTemplate(name, value);
		frame.getOperandStack().push(target);
	}
	
	@InvokeSpecialMethod(name="queryParam", signature="(Ljava/lang/String;[Ljava/lang/Object;)Ljavax/ws/rs/client/WebTarget;")
	public static void queryParam(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsTarget originalTarget = getWebTarget(frame.getOperandStack());
		
		MugglWsRsTarget target = null;
		try {
			target = new MugglWsRsTarget(originalTarget);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Could not generate MugglWsRsTarget", e);
		}
		String name = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[1]);
		Object value = parameters[2];
		target.setQueryParam(name, value);
		frame.getOperandStack().push(target);
	}
	
	
	@InvokeSpecialMethod(name="request", signature="()Ljavax/ws/rs/client/Invocation$Builder;")
	public static void request(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsTarget target = getWebTarget(frame.getOperandStack());
		try {
			MugglWsRsInvocationBuilder builder = new MugglWsRsInvocationBuilder((SymbolicVirtualMachine) frame.getVm(), target);
			frame.getOperandStack().push(builder);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Error while generating MugglWsRsInvocationBuilder", e);
		}
	}
	
	@InvokeSpecialMethod(name="request", signature="([Ljava/lang/String;)Ljavax/ws/rs/client/Invocation$Builder;")
	public static void request2(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsTarget target = getWebTarget(frame.getOperandStack());
		try {
			MugglWsRsInvocationBuilder builder = new MugglWsRsInvocationBuilder((SymbolicVirtualMachine) frame.getVm(), target);
			frame.getOperandStack().push(builder);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Error while generating MugglWsRsInvocationBuilder", e);
		}
	}
	
	protected static MugglWsRsTarget getWebTarget(Stack<Object> stack) throws SpecialMethodInvokeException {
		Object obj = stack.pop();
		if(!(obj instanceof MugglWsRsTarget)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type MugglWsRsTarget, but was: " + obj);
		}
		return (MugglWsRsTarget)obj;
	}
}
