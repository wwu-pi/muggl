package de.wwu.muggl.instructions.general;

import de.wwu.muggl.configuration.Defaults;
import de.wwu.muggl.instructions.interfaces.control.JumpException;
import de.wwu.muggl.instructions.interfaces.data.StackPop;
import de.wwu.muggl.instructions.interfaces.data.VariableUsing;
import de.wwu.muggl.instructions.typed.TypedInstruction;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.And;
import de.wwu.muggl.solvers.expressions.Or;
import de.wwu.muggl.solvers.expressions.*;
import de.wwu.muggl.solvers.solver.constraints.ArrayStore;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.ArrayRestore;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.SearchingVM;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.exceptions.ExceptionHandler;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;
import de.wwu.muggl.vm.initialization.*;
import de.wwu.muli.searchtree.Choice;
import de.wwu.muli.searchtree.Fail;
import de.wwu.muli.searchtree.ST;

import java.util.*;

/**
 * Abstract instruction with some concrete methods for storing elements into arrays. Concrete
 * instructions can be extended from this class.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2010-03-26
 */
public abstract class Astore extends GeneralInstruction implements JumpException, StackPop,
		VariableUsing {
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
			Object value = stack.pop();
			int index = (Integer) stack.pop();
			Object arrayrefObject  = stack.pop();

			// Runtime exception: arrayref is null
			if (arrayrefObject == null)
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));

			// Unexpected exception: arrayref does not point to an array.
			if (!(arrayrefObject instanceof Arrayref)) {
				throw new ExecutionException("Could not  " + getName() + "  array entry #" + index + ": Expected an array, but did not get one.");
			}
			Arrayref arrayref = (Arrayref) arrayrefObject;

			// Unexpected exception: The value is not of one of the required types.
			value = this.typedInstruction.validateAndTruncateValue(value, arrayref, frame);

			// Set the value into the array and save it.
			try {
				arrayref.putElement(index, value);
			} catch (ArrayIndexOutOfBoundsException e) {
				// Runtime exception array index out of bounds.
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayIndexOutOfBoundsException", e.getMessage()));
			} catch (ArrayStoreException e) {
				// Runtime exception: Array store exception.
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayStoreException", e.getMessage()));
			}
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
	 * @param frame The currently executed frame.
	 * @throws NoExceptionHandlerFoundException If no handler could be found.
	 * @throws SymbolicExecutionException Thrown in case of fatal problems during the symbolic execution.
	 */
	@Override
	public void executeSymbolically(Frame frame) throws NoExceptionHandlerFoundException, SymbolicExecutionException {
		try {
			// Preparations.
			Stack<Object> stack = frame.getOperandStack();
			Object value = stack.pop();
			if (!(value instanceof FreeObjectref)) {
				value = Term.frameConstant(value);
			}
			Object top = stack.pop();
			Object ref  =  stack.pop();

			int index;
			if (top instanceof IntConstant) {
				index = ((IntConstant) top).getValue();
			} else if (top instanceof Integer) {
				index = (Integer) top;
			} else {
				throw new SymbolicExecutionException("The found index was of an unsupported value type.");
			}

			// Runtime exception: arrayref is null
			if (ref == null) {
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));
			}

			// Unexpected exception: arrayref does not point to an array.
			if (!(ref instanceof Arrayref)) {
				throw new SymbolicExecutionException("Could not  " + getName() + ": Expected an array, but did not get one.");
			}
			Arrayref arrayref = (Arrayref) ref;
			// Save the current value, if necessary.
			if (((SearchingVM) frame.getVm()).isInSearch()) {
				Object oldValue = arrayref.getElement(index);
				ArrayRestore arrayValue = new ArrayRestore(arrayref, index, oldValue);
				((SearchingVM) frame.getVm()).saveArrayValue(arrayValue);
			}

			// Unexpected exception: The value is not of one of the required types.
			if (!(value instanceof FreeObjectref)) {
				value = this.typedInstruction.validateAndTruncateSymbolicValue((Term) value, arrayref, frame);
			}
			// Set the value into the array and save it.
			try {
				arrayref.putElement(index, value);
			} catch (ArrayIndexOutOfBoundsException e) {
				// Runtime exception array index out of bounds.
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayIndexOutOfBoundsException", e.getMessage()));
			} catch (ArrayStoreException e) {
				// Runtime exception: Array store exception.
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.ArrayStoreException", e.getMessage()));
			}
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

	private Term toTerm(Object o) {
		Term term;
		if (o instanceof Term) {
			term = (Term) o;
		} else {
			if (!(o instanceof Integer)) {
				throw new IllegalStateException("Term type not yet transformable: " + o); // TODO Objects
			}
			term = IntConstant.getInstance(o);
		}
		return term;
	}

    public Optional<ST> executeMuli(SearchingVM vm, Frame frame) throws ExecutionException {
		if (!vm.isInSearch()) {//if (!Options.getInst().symbolicMode) {
			// Non-symbolic case
			execute(frame);
			return Optional.empty();
		}

		try {
			Stack<Object> stack = frame.getOperandStack();
			Object value = stack.pop();

			if (value instanceof ArrayStoreMarker) {
				ArrayStoreMarker marker = (ArrayStoreMarker) value;
				if (marker.hasSolutionOutOfBounds) {
					marker.hasSolutionOutOfBounds = false;
					throwOutOfBoundsException(vm, frame);
				} else if (marker.hasSolutionInBounds) {
					marker.hasSolutionInBounds = false;
					Arrayref arrayref = marker.arrayref;
					Term indexTerm = marker.indexAsTerm;
					Object toBeStored = marker.toBeStored;
					Object oldValue = marker.oldValue;
					if (!(oldValue instanceof Aload.UninitializedMarker)) {
						ArrayRestore arrayValue = new ArrayRestore(arrayref, indexTerm, oldValue);
						((SearchingVM) frame.getVm()).saveArrayValue(arrayValue);
					}
					if (arrayref instanceof FreeArrayref) {
						((FreeArrayref) arrayref).putElementIntoFreeArray(indexTerm, toBeStored);
					} else {
						// Normal arrayref
						if (!(indexTerm instanceof IntConstant)) {
							throw new IllegalStateException("For normal arrays, symbolic indexes are not planned to be implemented. " +
									"Use FreeArrayref instead.");
						} else {
							arrayref.putElement(((IntConstant) indexTerm).getIntValue(), toBeStored);
						}
					}
				} else {
					throw new IllegalStateException("Should not occur.");
				}
				return Optional.empty();
			}

			Object index = stack.pop();
			Object ref = stack.pop();

			if (ref == null)
				throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException"));

			if (!(ref instanceof Arrayref)) {
				throw new IllegalStateException("Reference should be an Arrayref.");
			}

			Arrayref arrayref = (Arrayref) ref;
			if (!(arrayref instanceof FreeArrayref)) {
				if (index instanceof NumericVariable) {
					throw new IllegalStateException("Usual Arrayrefs do not (yet) implement free indexes. Use FreeArrayrefs instead.");
				}
			}
			Term indexAsTerm = toTerm(index);
			if (value instanceof Number) {
				value = toTerm(value);
			}

			ConstraintExpression indexInRange = getIndexInBoundsConstraint(arrayref, indexAsTerm, value);
			ConstraintExpression indexOutOfRange = getIndexOutOfBoundsConstraint(arrayref, indexAsTerm);
			SolverManager sm = vm.getSolverManager();
			boolean hasSolutionInRange = sm.checkSatWithNewConstraintAndRemove(indexInRange);
			boolean hasSolutionOutOfRange = Defaults.EXCEPTION_IF_FREE_ARRAY_INDEX_OOB
					&& sm.checkSatWithNewConstraintAndRemove(indexOutOfRange);

			// Start constructing choices & exception
			List<ConstraintExpression> constraintExpressions = new ArrayList<>();
			List<Integer> pcs = new ArrayList<>();

			if (hasSolutionInRange) {
				constraintExpressions.add(indexInRange);
				pcs.add(vm.getPc());
			}

			if (hasSolutionOutOfRange) {
				constraintExpressions.add(indexOutOfRange);
				pcs.add(vm.getPc());
			}

			Choice currentChoice = vm.getCurrentChoice();
			if (!constraintExpressions.isEmpty()) {
				ArrayStoreMarker marker = new ArrayStoreMarker();
				marker.arrayref = arrayref;
				marker.indexAsTerm = indexAsTerm;
				marker.hasSolutionOutOfBounds = hasSolutionOutOfRange;
				marker.hasSolutionInBounds = hasSolutionInRange;
				marker.toBeStored = value;
				marker.oldValue = getElementFromArrayAtIndex(arrayref, indexAsTerm);
				stack.push(marker);
				vm.preventNextSkip();
				Choice choice = new Choice(
						frame,
						pcs,
						constraintExpressions,
						vm.extractCurrentTrail(),
						currentChoice);
				return Optional.of(choice);
			} else {
				return Optional.of(new Fail());
			}
		} catch (VmRuntimeException | TimeoutException | SolverUnableToDecideException e) {
			throw new ExecutionException(e);
		}
    }

	protected Object getElementFromArrayAtIndex(Arrayref arrayref, Term index) {
		if (arrayref instanceof FreeArrayref) {
			return ((FreeArrayref) arrayref).getFreeArrayElement(index);
		} else {
			return arrayref.getElement(((IntConstant) index).getIntValue());
		}
	}

    protected class ArrayStoreMarker {
		protected Term indexAsTerm;
		protected Arrayref arrayref;
		protected Object toBeStored;
		protected Object oldValue;
		protected boolean hasSolutionOutOfBounds = false;
		protected boolean hasSolutionInBounds = false;
	}

	protected ConstraintExpression getIndexInBoundsConstraint(Arrayref arrayref, Term index, Object elementToStore) {
		ConstraintExpression indexConstraint = And.newInstance(
				LessThan.newInstance(index, arrayref.getLengthTerm()),
				GreaterOrEqual.newInstance(index, IntConstant.ZERO));

		if (!(elementToStore instanceof Objectref)) {
			if (arrayref instanceof FreeArrayref) {
				Expression encodedObject = encodeValueToTerm(elementToStore);
				ArrayStore arrayStore = ArrayStore.newInstance(
						arrayref,
						((FreeArrayref) arrayref).getVarNameWithId(),
						index,
						arrayref.getLengthTerm(),
						encodedObject);
				return And.newInstance(indexConstraint, arrayStore);
			} else { // TODO enable the same for regular arrays (?) implicitly use free arrays with pre-initialized values
				return indexConstraint;
			}
		} else { // TODO enable objects here
			return indexConstraint;
		}
	}

	protected ConstraintExpression getIndexOutOfBoundsConstraint(Arrayref arrayref, Term index) {
		return Or.newInstance(
				LessThan.newInstance(index, IntConstant.ZERO),
				GreaterOrEqual.newInstance(index, arrayref.getLengthTerm()));
	}

	protected Expression encodeValueToTerm(Object value) {
		// TODO Refactor with Aload
		if (value instanceof Term) {
			return (Term) value;
		}
		Expression result;
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

	protected Expression encodeObjectAsExpression(Objectref value) {
		// TODO Refactor with Aload
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

	protected void throwOutOfBoundsException(SearchingVM vm, Frame frame) throws SymbolicExecutionException, NoExceptionHandlerFoundException {
		try {
			Objectref exceptionRef = vm.generateExc(ArrayIndexOutOfBoundsException.class.getName());
			SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, exceptionRef);
			handler.handleException();
		} catch (ExecutionException e) {
			executionFailedSymbolically(e);
		}
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
		return "astore";
	}

	/**
	 * Get the thrown exception types as fully qualified java names.
	 * @return The thrown exception types.
	 */
	public String[] getThrownExceptionTypes() {
		String[] exceptionTypes = { "java.lang.ArrayIndexOutOfBoundsException",
									"java.lang.ArrayStoreException",
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
		return 3;
	}

}
