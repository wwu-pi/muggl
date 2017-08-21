package muggl.java.lang;

import java.util.Properties;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="java.lang.System")
public class System_Muggl {
	
	private static Properties properties = new Properties();

	@InvokeSpecialMethod(name="getProperty", signature="(Ljava/lang/String;)Ljava/lang/String;")
	public static void getProperty(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		String propName = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[0]);
		String property = properties.getProperty(propName);
		Object propValue = null;
		if(property != null) {
			propValue = SpecialMethodHelper.getStringObjectref((SymbolicVirtualMachine) frame.getVm(), property);
		}
		frame.getOperandStack().push(propValue);
	}
	
	@InvokeSpecialMethod(name="getProperty", signature="(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
	public static void getProperty2(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		String propName = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[0]);
		String property = (String)properties.get(propName);
		if(property == null) {
			property = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[1]);
		}
		frame.getOperandStack().push(SpecialMethodHelper.getStringObjectref((SymbolicVirtualMachine) frame.getVm(), property));
	}
	
	@InvokeSpecialMethod(name="setProperty", signature="(Ljava/lang/String;Ljava/lang/String;)V")
	public static void setProperty(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		String propName = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[0]);
		String propValue = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[0]);
		properties.setProperty(propName, propValue);
	}
	
	
	@InvokeSpecialMethod(name="arraycopy", signature="(Ljava/lang/Object;ILjava/lang/Object;II)V")
	public static void arraycopy(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Arrayref src = (Arrayref)parameters[0];
		int srcPos = ((IntConstant)parameters[1]).getIntValue();
		Arrayref dest = (Arrayref)parameters[2];
		int destPos = ((IntConstant)parameters[3]).getIntValue();
		int length = ((IntConstant)parameters[4]).getIntValue();
		
		for(int i=srcPos; i<length; i++) {
			Object ele = src.getElement(i);
			dest.putElement(destPos++, ele);
		}
	}
	
	
}