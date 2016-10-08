package de.wwu.muggl.instructions.typed;

import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Variable;

/**
 * This class provides static methods to be accessed by instructions typed as an Integer.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2008-08-27
 */
public class IntegerInstruction extends TypedInstruction {

	/**
	 * Get a String representation of the desired type of this instruction. This method is used to
	 * check whather the fetched Object is an instance of the desired type.
	 * @return A String representation of the desired type.
	 */
	@Override
	public String getDesiredType() {
		return "java.lang.Integer";
	}

	/**
	 * Get the int representation of the type that will be wrapped by this Variable.
	 *
	 * @return The value of SymbolicVirtualMachineElement.INT.
	 *  @see de.wwu.testtool.expressions.Expression#INT
	 */
	@Override
	public int[] getDesiredSymbolicalTypes() {
		int[] types = {Expression.INT};
		return types;
	}

	/**
	 * Extend the value if this is required by its type. For java.lang.Boolean use zero-extension,
	 * for java.lang.Byte, .Short, and .Char use sign-extension.
	 * @param value The object that needs to be extended.
	 * @param type The type of the object, which determines the extension strategy.
	 * @return The value, extended to Integer.
	 */
	@Override
	protected Object extendValue(Object value, String type) {
		switch (type) {
		case "java.lang.Boolean":
			return ((Boolean) value).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
		case "java.lang.Byte":
			return ((Byte) value).intValue();
		case "java.lang.Short":
			return ((Short) value).intValue();
		case "java.lang.Character":
			return (int) ((Character) value);
		default: 
			return value;
		}
	}

	/**
	 * Truncate the value if this is required by its type. In this case the integer value is
	 * truncated to a byte, short, char, or boolean.
	 * @param value The object that needs to be truncated.
	 * @param type The type of the object, which determines the truncation strategy.
	 * @return The value, truncated to the type indicated by `type'.
	 */
	@Override
	protected Object truncateValue(Object value, String type) {
		// Do nothing if we're already right
		// FIXME mxs boolean handling
		if (!value.getClass().getName().equals(type)) {
			switch (type) {
			case "java.lang.Boolean":
				return ((Integer) value).intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;
			case "java.lang.Byte":
				return ((Integer) value).byteValue();
			case "java.lang.Short":
				return ((Integer) value).shortValue();
			case "java.lang.Character":
				return (char) ((Integer) value).intValue();
			default:
				return value;
			}
		}

		return value;
	}

	/**
	 * Get a new int variable for the symbolic execution.
	 * @param method The Method the variable is a parameter of.
	 * @param localVariable The index into the local variables (required for the correct naming of the variable generated).
	 * @return An instance of NumericVariable.
	 */
	@Override
	public Variable getNewVariable(Method method, int localVariable) {
		return new NumericVariable(generateVariableNameByNumber(method, localVariable), Expression.INT, false);
	}

	/**
	 * Get a String array representation of the desired types of this instruction. This method is used to
	 * check whather the fetched Object is an instance of one of the desired types.
	 * @return A String representation of the desired type.
	 */
	@Override
	public String[] getDesiredTypes() {
		String[] desiredTypes = {"java.lang.Integer", "java.lang.Boolean", "java.lang.Byte", "java.lang.Short", "java.lang.Character"};
		return desiredTypes;
	}

}
