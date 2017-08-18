package muggl.java.net;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

@InvokeSpecialClass(className="java.net.URL")
public class URL {

	@InvokeSpecialMethod(name="getURLStreamHandler", signature="(Ljava/lang/String;)Ljava/net/URLStreamHandler;")
	public static void getURLStreamHandler(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		try {
			ClassFile cf = vm.getClassLoader().getClassAsClassFile("java.net.URLStreamHandler");
			Objectref ref = vm.getAnObjectref(cf);
			frame.getOperandStack().push(ref);
		} catch (ClassFileException e) {
			throw new SpecialMethodInvokeException("Cannot generate urlstreamhandler", e);
		}
	}
}
