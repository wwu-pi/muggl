package muggl.javax.ws.rs.core;

import java.util.Stack;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jaxrs.SymbolicResponse;
import de.wwu.muggl.vm.Frame;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="javax.ws.rs.core.Response")
public class Response {

	@InvokeSpecialMethod(name="getStatus", signature="()I")
	public static void getStatus(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicResponse response = getResponse(frame.getOperandStack());
		frame.getOperandStack().push(response.getStatus());
	}
	
	
	protected static SymbolicResponse getResponse(Stack<Object> stack) throws SpecialMethodInvokeException {
		Object obj = stack.pop();
		if(!(obj instanceof SymbolicResponse)) {
			throw new SpecialMethodInvokeException("Expected the object reference to be of type SymbolicResponse, but was: " + obj);
		}
		return (SymbolicResponse)obj;
	}
}
