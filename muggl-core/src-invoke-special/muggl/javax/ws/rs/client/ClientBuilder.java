package muggl.javax.ws.rs.client;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.ws.MugglWsRsClient;
import de.wwu.muggl.javaee.ws.MugglWsRsException;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

@InvokeSpecialClass(className="javax.ws.rs.client.ClientBuilder")
public class ClientBuilder {
	
	@InvokeSpecialMethod(name="newClient", signature="()Ljavax/ws/rs/client/Client;")
	public static void newClient(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		try {
			MugglWsRsClient mugglRSClient = new MugglWsRsClient((SymbolicVirtualMachine) frame.getVm());
			frame.getOperandStack().push(mugglRSClient);
		} catch (MugglWsRsException e) {
			throw new SpecialMethodInvokeException("Cannot generate a Muggl JAX-RS Client.", e);
		}
	}
	
}
