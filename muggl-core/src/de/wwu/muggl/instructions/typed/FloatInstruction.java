package de.wwu.muggl.instructions.typed;

import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Variable;

/**
 * This class provides static methods to be accessed by instructions typed as an Float.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2008-08-27
 */
public class FloatInstruction extends TypedInstruction {

	/**
	 * Get a String representation of the desired type of this instruction. This method is used to
	 * check whather the fetched Object is an instance of the desired type.
	 * @return A String representation of the desired type.
	 */
	@Override
	public String getDesiredType() {
		return "java.lang.Float";
	}

	/**
	 * Get the int representation of the type that will be wrapped by this Variable.
	 *
	 * @return The value of SymbolicVirtualMachineElement.FLOAT.
	 *  @see de.wwu.testtool.expressions.Expression#FLOAT
	 */
	@Override
	public int[] getDesiredSymbolicalTypes() {
		int[] types = {Expression.FLOAT};
		return types;
	}

	/**
	 * Get a new float variable for the symbolic execution.
	 * @param method The Method the variable is a parameter of.
	 * @param localVariable The index into the local variables (required for the correct naming of the variable generated).
	 * @return An instance of NumericVariable.
	 */
	@Override
	public Variable getNewVariable(Method method, int localVariable) {
		return new NumericVariable(generateVariableNameByNumber(method, localVariable), Expression.FLOAT, false);
	}

	/**
	 * Get a String array representation of the desired types of this instruction. This method is used to
	 * check whather the fetched Object is an instance of one of the desired types.
	 * @return A String representation of the desired type.
	 */
	@Override
	public String[] getDesiredTypes() {
		String[] desiredTypes = {"java.lang.Float"};
		return desiredTypes;
	}

}
