package de.wwu.muggl.instructions.general;

import java.lang.Exception;
import java.util.*;

import de.wwu.muggl.configuration.Defaults;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.instructions.interfaces.Instruction;
import de.wwu.muggl.instructions.interfaces.control.JumpException;
import de.wwu.muggl.instructions.interfaces.data.StackPop;
import de.wwu.muggl.instructions.interfaces.data.StackPush;
import de.wwu.muggl.instructions.typed.TypedInstruction;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.solvers.expressions.And;
import de.wwu.muggl.solvers.expressions.Or;
import de.wwu.muggl.solvers.solver.constraints.ArraySelect;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;
import de.wwu.muggl.vm.initialization.*;
import de.wwu.muli.searchtree.*;

/**
 * Abstract instruction with some concrete methods for loading elements from an array.
 * Concrete instructions can be extended from this class.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-11
 */
public abstract class Aload extends GeneralInstruction implements JumpException, StackPop, StackPush, Instruction {


	/**
	 * The type the inheriting class will take.
	 */
	protected TypedInstruction typedInstruction;

	/**
	 * Execute the inheriting instruction.
	 * @param frame The currently executed frame.
	 * @throws ExecutionException Thrown in case of fatal problems during the execution.
	 */
	@Override
	public void execute(Frame frame) throws ExecutionException {
		try {
			// Preparations.
			Stack<Object> stack = frame.getOperandStack();

			Object index = stack.pop();
			int idx;
			if (index instanceof Character) {
				idx = (int)(((Character) index).charValue());
			} else {
				idx = (int)index;
			}
			Object arrayrefObject = stack.pop();

			// Runtime exception: arrayref is null
			if (arrayrefObject == null)
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));

			// Unexpected exception: Arrayref does not point to an array.
			if (!(arrayrefObject instanceof Arrayref)) {
				throw new ExecutionException("Could not " + getName() + " array entry #" + index + ": Expected an array, but did not get one.");
			}
			Arrayref arrayref = (Arrayref) arrayrefObject;

			// Runtime exception array index out of bounds.
			if (idx >= arrayref.getLength() || idx < 0) {
				throw new VmRuntimeException(frame.getVm().generateExc(
						"java.lang.ArrayIndexOutOfBoundsException", "Array index is out of bounds"));
			}

			// Unexpected exception: The object at the index is not of one of the required types.
			Object value = arrayref.getElement(idx);
			value = this.typedInstruction.validateAndExtendValue(value);

			// Push the value.
			stack.push(value);
		} catch (VmRuntimeException e) {
			ExceptionHandler handler = new ExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailed(e2);
			}
		} catch (ExecutionException e) {
			executionFailed(e);
		}
	}

	/**
	 * Execute the instruction symbolically.
	 *
	 * @param frame The currently executed frame.
	 * @throws NoExceptionHandlerFoundException If no handler could be found.
	 * @throws SymbolicExecutionException Thrown in case of fatal problems during the symbolic
	 *         execution.
	 */
	@Override
	public void executeSymbolically(Frame frame) throws NoExceptionHandlerFoundException,
			SymbolicExecutionException {
		try {
			// Preparations.
			Stack<Object> stack = frame.getOperandStack();
			Object elem = stack.pop();
			Object index;
			if (elem instanceof IntConstant) {
				index = ((IntConstant) elem).getValue();
			} else {
				index = elem;
			}
			Object arrayrefObject  = stack.pop();

			// Runtime exception: arrayref is null
			if (arrayrefObject == null)
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));

			// Unexpected exception: arrayref does not point to an array.
			if (!(arrayrefObject instanceof Arrayref)) {
				throw new SymbolicExecutionException("Could not " + getName() + " array entry #" + index + ": Expected an array, but did not get one.");
			}
			Arrayref arrayref = (Arrayref) arrayrefObject;

			// Runtime exception array index out of bounds.
			Integer indexCast = (Integer) index;
			if (arrayref.getLength() <= indexCast || indexCast < 0) {
				throw new VmRuntimeException(frame.getVm().generateExc(
						ArrayIndexOutOfBoundsException.class.getName(),
						"Array index is out of bounds"));
			}
			Object value = arrayref.getElement(indexCast);
			// Push the value.
			stack.push(value);

		} catch (VmRuntimeException e) {
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, e);
			try {
				handler.handleException();
			} catch (ExecutionException e2) {
				executionFailedSymbolically(e2);
			}
		} catch (SymbolicExecutionException e) {
			executionFailedSymbolically(e);
		}
	}

	@Override
	public Optional<ST> executeMuli(SearchingVM vm, Frame frame) throws ExecutionException {
		if (!vm.isInSearch()) {
			// Non-symbolic case
			execute(frame);
			return Optional.empty();
		}
		// Symbolic case:
		try {
			Stack<Object> stack = frame.getOperandStack();
			Object elem = stack.pop();

			if (elem instanceof ArrayLoadMarker) {
				ArrayLoadMarker marker = (ArrayLoadMarker) elem;
				if (marker.hasSolutionOutOfBounds) {
					marker.hasSolutionOutOfBounds = false;
					throwOutOfBoundsException(vm, frame);
				} else if (marker.hasSolutionInBounds) {
					marker.hasSolutionInBounds = false;
					Arrayref arrayref = marker.arrayref;
					Term indexTerm = marker.indexAsTerm;
					if (arrayref instanceof FreeArrayref) {
						if (marker.loadedElement instanceof UninitializedMarker) {
							throw new IllegalStateException("Should have been substituted with real value.");
						}
						((FreeArrayref) arrayref).putElementIntoFreeArray(indexTerm, marker.loadedElement, true);
						frame.getOperandStack().push(marker.loadedElement);
					} else {
						frame.getOperandStack().push(arrayref.getElement(((IntConstant) indexTerm).getIntValue()));
					}
				} else {
					throw new IllegalStateException("Should not occur.");
				}
				return Optional.empty();
			}
			Object arrayrefObject  = stack.pop();

			// Runtime exception: arrayref is null
			if (arrayrefObject == null)
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));
			// Unexpected exception: arrayref does not point to an array.
			if (!(arrayrefObject instanceof Arrayref)) {
				throw new SymbolicExecutionException("Could not " + getName() + " array entry #" + elem + ": Expected an array, but did not get one.");
			}
			Arrayref arrayref = (Arrayref) arrayrefObject;
			Term indexAsTerm;
			if (elem instanceof NumericVariable) {
				if (!arrayref.isPrimitive()) {
					throw new ExecutionException("Free indexes are not yet implemented for objects."); // TODO
				}
				indexAsTerm = (NumericVariable) elem;
			} else if (elem instanceof Integer) {
				indexAsTerm = IntConstant.getInstance(elem);
			} else if (elem instanceof IntConstant) {
				indexAsTerm = (IntConstant) elem;
			} else {
				throw new UnsupportedOperationException("Unknown case for index: " + elem);
			}
			// Create constraint expressions for case index < array.length and index >= array.length
			ConstraintExpression indexInRange = getIndexInBoundsConstraint(vm, arrayref, indexAsTerm, null);
			ConstraintExpression indexOutOfRange = getIndexOutOfBoundsConstraint(arrayref, indexAsTerm);

			// Check if GTE is feasible. This would lead to an ArrayIndexOutOfBoundsException.
			SolverManager sm = vm.getSolverManager();
			boolean hasSolutionInRange = sm.checkSatWithNewConstraintAndRemove(indexInRange);
			boolean hasSolutionOutOfRange = Defaults.EXCEPTION_IF_FREE_ARRAY_INDEX_OOB
					&& sm.checkSatWithNewConstraintAndRemove(indexOutOfRange);


			Choice currentChoice = vm.getCurrentChoice();
			if (hasSolutionInRange || hasSolutionOutOfRange) {
				// Start constructing choices & exception
				ArrayLoadMarker marker = new ArrayLoadMarker();
				marker.arrayref = arrayref;
				marker.indexAsTerm = indexAsTerm;
				marker.hasSolutionOutOfBounds = hasSolutionOutOfRange;
				marker.hasSolutionInBounds = hasSolutionInRange;
				List<ConstraintExpression> constraintExpressions = new ArrayList<>();
				List<Integer> pcs = new ArrayList<>();
				if (hasSolutionOutOfRange) {
					pcs.add(vm.getPc());
					constraintExpressions.add(indexOutOfRange);
				}
				if (hasSolutionInRange) {
					pcs.add(vm.getPc());
					constraintExpressions.add(getIndexInBoundsConstraint(vm, arrayref, indexAsTerm, marker));
				}
				stack.push(marker);
				Stack<TrailElement> trail = vm.extractCurrentTrail();
				vm.preventNextSkip();
				Choice choice = new Choice(
						frame,
						pcs,
						constraintExpressions,
						trail,
						currentChoice);
				return Optional.of(choice);
			} else {
				return Optional.of(new Fail());
			}
		} catch (VmRuntimeException e) {
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, e);
			try {
				handler.handleException();
				return Optional.empty();
			} catch (ExecutionException e2) {
				executionFailedSymbolically(e2);
				return Optional.empty();
			}
		} catch (ExecutionException e) {
			executionFailedSymbolically(e);
			return Optional.empty();
		} catch (TimeoutException | SolverUnableToDecideException e) {
			throw new ExecutionException(e);
		}
	}

	protected class ArrayLoadMarker {
		protected Term indexAsTerm;
		protected Arrayref arrayref;
		protected Object loadedElement = new UninitializedMarker();
		protected boolean hasSolutionOutOfBounds = false;
		protected boolean hasSolutionInBounds = false;
	}

	protected class UninitializedMarker {}

	protected ConstraintExpression getIndexOutOfBoundsConstraint(Arrayref arrayref, Term index) {
		return Or.newInstance(
				LessThan.newInstance(index, IntConstant.ZERO),
				GreaterOrEqual.newInstance(index, arrayref.getLengthTerm()));
	}

	protected ConstraintExpression getIndexInBoundsConstraint(SearchingVM vm, Arrayref arrayref, Term index, ArrayLoadMarker marker) throws ExecutionException {
		ConstraintExpression indexConstraint = And.newInstance(
				LessThan.newInstance(index, arrayref.getLengthTerm()),
				GreaterOrEqual.newInstance(index, IntConstant.ZERO));

		Object loadedOrGeneratedElement = getElementFromArrayAtIndex(arrayref, index);

		if (loadedOrGeneratedElement instanceof FreeArrayref.UninitializedMarker) {
			loadedOrGeneratedElement = initializeNewElementOfFreeArrayref(vm, (FreeArrayref) arrayref, index);
		}

		if (marker != null) {
			marker.loadedElement = loadedOrGeneratedElement;
		}

		if (!(loadedOrGeneratedElement instanceof Objectref)) {
			if (arrayref instanceof FreeArrayref) {
				Term encodedObject = encodeValueToTerm(loadedOrGeneratedElement);
				ArraySelect arraySelect = ArraySelect.newInstance(
						arrayref,
						arrayref instanceof FreeArrayref ? ((FreeArrayref) arrayref).getVarNameWithId() : arrayref.getName() + arrayref.getArrayrefId(),
						index,
						arrayref.getLengthTerm(),
						encodedObject);
				return And.newInstance(indexConstraint, arraySelect);
			} else { // TODO enable the same for regular arrays (?) implicitly use free arrays with pre-initialized values
				return indexConstraint;
			}
		} else { // TODO enable objects here
			return indexConstraint;
		}
	}

	protected Object getElementFromArrayAtIndex(Arrayref arrayref, Term index) {
		if (arrayref instanceof FreeArrayref) {
			return ((FreeArrayref) arrayref).getFreeArrayElement(index);
		} else {
			return arrayref.getElement(((IntConstant) index).getIntValue());
		}
	}

	protected void throwOutOfBoundsException(SearchingVM vm, Frame frame) throws SymbolicExecutionException, NoExceptionHandlerFoundException {
		try {
			Objectref exceptionRef = vm.generateExc(ArrayIndexOutOfBoundsException.class.getName());
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, exceptionRef);
			handler.handleException();
		} catch (ExecutionException e) {
			executionFailedSymbolically(e);
		}
	}

	private static int indexAccesses = 0;
	protected Object initializeNewElementOfFreeArrayref(SearchingVM vm, FreeArrayref freeArrayref, Term index) {
		ReferenceValue referenceValue = freeArrayref.getReferenceValue();
		if (referenceValue.isPrimitive() || freeArrayref.isRepresentedTypeIsAPrimitiveWrapper()) {
			// TODO in principle, more than int should be possible.
			NumericVariable var = new NumericVariable(freeArrayref.getVarNameWithId() + "[" + index + "]_"+indexAccesses++, Expression.Type.INT.toByte());
			return var;
		} else {
			return FreeObjectrefInitialisers.createRepresentationForFreeObject(vm, vm.getCurrentFrame().getMethod().getClassFile(), freeArrayref.getReferenceValue().getName());
		}
	}

	protected Term encodeValueToTerm(Object value) {
		if (value instanceof Term) {
			return (Term) value;
		}
		Term result;
		if (value instanceof Number) {
			if (value instanceof Integer) {
				result = IntConstant.getInstance(((Integer) value).intValue());
			} else if (value instanceof Double) {
				result = DoubleConstant.getInstance(((Double) value).doubleValue());
			} else {
				throw new IllegalStateException("Case not implemented: " + value);
			}
		} else if (value instanceof Objectref) {
			result = encodeObjectAsExpression((Objectref) value);
		} else {
			throw new IllegalStateException("Case not implemented: " + value);
		}
		return result;
	}

	protected Term encodeObjectAsExpression(Objectref value) {
		// TODO Get fields of super- and subclass.
		InitializedClass initializedClass = value.getInitializedClass();
		Field[] fields = initializedClass.getClassFile().getFields();
		String[] fieldNames = new String[fields.length];
		Object[] vals = new Object[fields.length];
		Map<Field, Object> fieldsOfValue = value.getFields();


		for (int i = 0; i < fields.length; i++) {
			Object valToInsert = value.getField(fields[i]);
			if (valToInsert == null && !fieldsOfValue.containsKey(fields[i])) {
				valToInsert = new ObjectExpression.UninitializedMarker();
			}
			vals[i] = valToInsert;
			fieldNames[i] = fields[i].getFullName();
		}
		return new ObjectExpression(fieldNames, vals, value);
	}

	/**
	 * Get the number of other bytes for this instruction.
	 * @return The number of other bytes.
	 */
	@Override
	public int getNumberOfOtherBytes() {
		return 0;
	}

	/**
	 * Resolve the instructions name.
	 * @return The instructions name as a String.
	 */
	@Override
	public String getName() {
		return "aload";
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		String[] exceptionTypes = { "java.lang.ArrayIndexOutOfBoundsException",
				"java.lang.NullPointerException"};
		return exceptionTypes;
	}

	/**
	 * Get the number of elements that will be popped from the stack when this instruction is
	 * executed.
	 *
	 * @return The number of elements that will be popped from the stack.
	 */
	public int getNumberOfPoppedElements() {
		return 2;
	}

	/**
	 * Get the number of elements that will be pushed onto the stack when this instruction is
	 * executed.
	 *
	 * @return The number of elements that will be pushed onto the stack.
	 */
	public int getNumberOfPushedElements() {
		return 1;
	}

	/**
	 * Get the types of elements this instruction will pop from the stack.
	 *
	 * @param methodClassFile The class file of the method this instruction belongs to.
	 * @return The types this instruction pops. The length of the arrays reflects the number of
	 *         elements pushed in the order they are pushed. Types are {@link ClassFile#T_BOOLEAN},
	 *         {@link ClassFile#T_BYTE} {@link ClassFile#T_CHAR}, {@link ClassFile#T_DOUBLE},
	 *         {@link ClassFile#T_FLOAT}, {@link ClassFile#T_INT}, {@link ClassFile#T_LONG} and
	 *         {@link ClassFile#T_SHORT}, 0 to indicate a reference or return address type or -1 to
	 *         indicate the popped type cannot be determined statically.
	 */
	public byte[] getTypesPopped(ClassFile methodClassFile) {
		byte[] types = {ClassFile.T_INT, 0};
		return types;
	}
}