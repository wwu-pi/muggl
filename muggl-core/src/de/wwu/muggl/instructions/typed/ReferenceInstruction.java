package de.wwu.muggl.instructions.typed;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionAlgorithms;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.initialization.ReferenceValue;
import de.wwu.muggl.solvers.expressions.Variable;

/**
 * This class provides static methods to be accessed by instructions typed as a reference.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2009-03-31
 */
public class ReferenceInstruction extends TypedInstruction {

	/**
	 * Get a String representation of the desired type of this instruction. This method is used to
	 * check whather the fetched Object is an instance of the desired type. For aload it is not needed.
	 * @return A String representation of the desired type.
	 */
	@Override
	public String getDesiredType() {
		return "de.wwu.muggl.vm.initialization.ReferenceValue";
	}

	/**
	 * Returns true in this overriding method, since no check is needed here.
	 *
	 * @param objectref The objectref (unused in this overriding method).
	 * @return true.
	 */
	@Override
	public boolean checkDesiredType(String objectref) {
		return true;
	}

	/**
	 * Get a new reference variable for the symbolic execution.
	 *
	 * @param method The Method the variable is a parameter of.
	 * @param localVariable The index into the local variables (required for the correct naming of
	 *        the variable generated).
	 * @return null.
	 */
	@Override
	public Variable getNewVariable(Method method, int localVariable) {
		return null; // TODO
	}

	/**
	 * Get a String array representation of the desired types of this instruction. This method is used to
	 * check whather the fetched Object is an instance of one of the desired types.
	 * @return A String representation of the desired type.
	 */
	@Override
	public String[] getDesiredTypes() {
		String[] desiredTypes = {"de.wwu.muggl.vm.initialization.ReferenceValue"};
		return desiredTypes;
	}

	/**
	 * Just return the value since no check is needed here.
	 *
	 * @param value The value.
	 * @return The value.
	 */
	@Override
	public Object validateAndExtendValue(Object value) {
		return value;
	}

	/**
	 * Get the int representation of the type that will be wrapped by a Term.
	 *
	 * @return The int value -1.
	 */
	@Override
	public int[] getDesiredSymbolicalTypes() {
		int[] types = {-1};
		return types;
	}

	/**
	 * Validate the value. Therefore, the assignment compatibility algorithm given in the java virtual machine
	 * specification is used.
	 * @param value The value that is to be stored into an array.
	 * @param arrayref The array reference.
	 * @param frame The currently executed frame.
	 * @return The value.
	 * @throws ExecutionException If the supplied value is not assignment compatible, a VmRuntimeException with an enclosed ArrayStoreException is thrown. Other Exceptions might be thrown due to problems while loading classes.
	 * @throws VmRuntimeException If a runtime exception happened and should be handled by the exception handler.
	 */
	@Override
	@SuppressWarnings("unused")
	public Object validateAndTruncateValue(Object value, Object arrayref, Frame frame) throws ExecutionException, VmRuntimeException {
		// A null value is always assignment compatible.
		if (value == null) return value;

		// Check assignment compatibility.
		ExecutionAlgorithms ea = new ExecutionAlgorithms(frame.getVm().getClassLoader());
		if (!ea.checkForAssignmentCompatibility((ReferenceValue) value, (ReferenceValue) arrayref))
			throw new VmRuntimeException(frame.getVm().generateExc(
					"java.lang.ArrayStoreException",
					value.getClass().getName() + " is not1 assignment compatible with "
							+ arrayref.getClass().getName()));
		return value;
	}

	/**
	 * Validate a return value. This is similar to validating a reference that is to be stored,
	 * but the type to check assignment compatibility against is fetched from the return descriptor
	 * of the current method.
	 * @param value The value that is to be returned.
	 * @param frame The currently executed frame.
	 * @return The value.
	 * @throws ExecutionException If the supplied value does not match the expected type, an ExecutionException is thrown.
	 * @throws VmRuntimeException If a runtime exception happened and should be handled by the exception handler.
	 */
	@Override
	public Object validateReturnValue(Object value, Frame frame) throws ExecutionException, VmRuntimeException {
		// Get return type.
		String returnType = frame.getMethod().getReturnType();

		ExecutionAlgorithms ea = new ExecutionAlgorithms(frame.getVm().getClassLoader());
		if (!ea.checkForAssignmentCompatibility(value, returnType, frame.getVm(), false))
			throw new VmRuntimeException(frame.getVm()
					.generateExc(
							"java.lang.ArrayStoreException",
							value.getClass().getName() + " is not assignment compatible with "
									+ returnType));
		return value;
	}

}
