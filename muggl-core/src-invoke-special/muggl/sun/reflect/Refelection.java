package muggl.sun.reflect;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.vm.Frame;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="sun.reflect.Refelection")
public class Refelection {

	@InvokeSpecialMethod(name="getCallerClass", signature="()Ljava/lang/Class;")
	public static void getCallerClass(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		throw new RuntimeException("Not handled yet. Implement special handling for the methods before this call.");
	}
}
