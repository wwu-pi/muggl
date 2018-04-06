package muggl.javax.ws.rs.client;

import java.util.Stack;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.ws.MugglWsRsClient;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.javaee.ws.MugglWsRsTarget;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="javax.ws.rs.client.Client")
public class Client {

	@InvokeSpecialMethod(name="target", signature="(Ljava/lang/String;)Ljavax/ws/rs/client/WebTarget;")
	public static void target(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglWsRsClient client = getClient(frame.getOperandStack());
		String requestUrl = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[1]);
		try {
			MugglWsRsTarget target = new MugglWsRsTarget((SymbolicVirtualMachine) frame.getVm(), client);
			target.setTargetUrl(requestUrl);
			frame.getOperandStack().push(target);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Error while generating MugglWsRsTarget", e);
		}
	}
	
	@InvokeSpecialMethod(name="register", signature="(Ljava/lang/Object;)Ljavax/ws/rs/core/Configurable;")
	public static void register(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		// nothing to do
	}
	
	@InvokeSpecialMethod(name="close", signature="()V")
	public static void close(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		// nothing to do
	}
	
	protected static MugglWsRsClient getClient(Stack<Object> stack) throws SpecialMethodInvokeException {
		Object obj = stack.pop();
		if(!(obj instanceof MugglWsRsClient)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type MugglWsRsClient, but was: " + obj);
		}
		return (MugglWsRsClient)obj;
	}
}
