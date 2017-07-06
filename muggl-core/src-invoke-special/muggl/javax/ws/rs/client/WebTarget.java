package muggl.javax.ws.rs.client;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.vm.Frame;

@InvokeSpecialClass(className="javax.ws.rs.client.WebTarget")
public class WebTarget {

	@InvokeSpecialMethod(name="resolveTemplate", signature="(Ljava/lang/String;Ljava/lang/Object;)Ljavax/ws/rs/client/WebTarget;")
	public static void resolveTemplate(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		System.out.println("hallo");
	}
	
}
