package muggl.java.util.logging;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.logging.SymoblicLogger;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="java.util.logging.Logger")
public class Logger {

	@InvokeSpecialMethod(name="log", signature="(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V")
	public static void log1(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		// nothing to log
		System.out.println("nothing to log");
	}
	
	@InvokeSpecialMethod(name="getLogger", signature="(Ljava/lang/String;)Ljava/util/logging/Logger;")
	public static void getLogger(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		System.out.println("nothing to log");
		frame.getOperandStack().push(new SymoblicLogger("MugglLogger", vm));
	}
	
}
