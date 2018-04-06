package muggl.javax.ws.rs.client;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.vm.Frame;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="javax.ws.rs.client.Entity")
public class Entity {

	@InvokeSpecialMethod(name="entity", signature="(Ljava/lang/Object;Ljava/lang/String;)Ljavax/ws/rs/client/Entity;")
	public static void entity(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		frame.getOperandStack().push(parameters[0]);
	}
}
