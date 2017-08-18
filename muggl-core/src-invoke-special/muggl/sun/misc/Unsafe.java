package muggl.sun.misc;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
@InvokeSpecialClass(className="sun.misc.Unsafe")
public class Unsafe {
	
	private static Objectref theUnsafe;

	@InvokeSpecialMethod(name="getUnsafe", signature="()Lsun/misc/Unsafe;")
	public static void getUnsafe(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		Objectref theUnsafe = getTheUnsafe((SymbolicVirtualMachine)frame.getVm());
		frame.getOperandStack().push(theUnsafe);
	}
	
	@InvokeSpecialMethod(name="objectFieldOffset", signature="(Ljava/lang/reflect/Field;)J")
	public static void objectFieldOffset(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		frame.getOperandStack().pop();
		frame.getOperandStack().push(NumericConstant.getInstance(0, Expression.LONG));
	}
	
	@InvokeSpecialMethod(name="arrayBaseOffset", signature="(Ljava/lang/Class;)I")
	public static void arrayBaseOffset(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		frame.getOperandStack().pop();
		frame.getOperandStack().push(NumericConstant.getInstance(0, Expression.INT));
	}
	
	@InvokeSpecialMethod(name="arrayIndexScale", signature="(Ljava/lang/Class;)I")
	public static void arrayIndexScale(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		frame.getOperandStack().pop();
		frame.getOperandStack().push(NumericConstant.getInstance(0, Expression.INT));
	}
	
	protected static Objectref getTheUnsafe(SymbolicVirtualMachine vm) {
		if(theUnsafe == null) {
			try {
				ClassFile classFile = vm.getClassLoader().getClassAsClassFile("sun.misc.Unsafe");
				Objectref unsafe = vm.getAnObjectref(classFile);
				Field f = classFile.getFieldByName("theUnsafe");
				theUnsafe = (Objectref) unsafe.getInitializedClass().getField(f);
			} catch (ClassFileException e) {
				e.printStackTrace();
			}
		}
		return theUnsafe;
	}
	
}
