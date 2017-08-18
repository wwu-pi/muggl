package de.wwu.muggl.instructions.general;

import de.wwu.muggl.instructions.InvalidInstructionInitialisationException;
import de.wwu.muggl.instructions.interfaces.control.JumpConditional;
import de.wwu.muggl.instructions.interfaces.data.StackPop;
import de.wwu.muggl.instructions.interfaces.data.VariableUsing;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Term;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeCode;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.execution.ExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.impl.symbolic.exceptions.SymbolicExceptionHandler;

/**
 * Abstract instruction with some concrete methods for comparison instructions of the group
 * if_cmp&lt;cond&gt;. Concrete instructions can be extended from this class.
 *
 * @author Tim Majchrzak, Andreas Fuchs
 * @version 1.0.0, 2010-08-29
 */
public abstract class If_icmp extends GeneralInstructionWithOtherBytes implements JumpConditional,
		StackPop, VariableUsing {
	// The line number is stored for jumping purposes.
	private int	lineNumber;

	/**
	 * Standard constructor. For the extraction of the other bytes, the attribute_code of the method that the
	 * instruction belongs to is supplied as an argument. Also the line number is given as an argument, since
	 * it is needed in case of jumping.
	 *
	 * @param code The attribute_code of the method that the instruction belongs to.
	 * @param lineNumber The line number of this instruction (including other bytes!).
	 * @throws InvalidInstructionInitialisationException If the instruction could not be initialized successfully, most likely due to missing additional bytes. This might be caused by a corrupt classfile, or a classfile of a more recent version than what can be handled.
	 */
	public If_icmp(AttributeCode code, int lineNumber) throws InvalidInstructionInitialisationException {
		super(code);
		this.lineNumber = lineNumber;
	}

	/**
	 * Execute the inheriting instruction.
	 *
	 * @param frame The currently executed frame.
	 */
	@Override
	public void execute(Frame frame) {
		Integer value2 = (Integer) VmSymbols.wideningPrimConversion(frame.getOperandStack().pop(), Integer.class);
		Integer value1 = (Integer) VmSymbols.wideningPrimConversion(frame.getOperandStack().pop(), Integer.class);
		if (compare(value1, value2)) {
			frame.getVm().setPC(this.lineNumber + (this.otherBytes[0] << ONE_BYTE | this.otherBytes[1]));
		}
	}

	/**
	 * Execute the inheriting instruction symbolically.
	 * @param frame The currently executed frame.
	 * @throws SymbolicExecutionException Thrown in case of fatal problems during the symbolic execution.
	 */
	@Override
	public void executeSymbolically(Frame frame) throws SymbolicExecutionException, NoExceptionHandlerFoundException {
		Object obj2 = frame.getOperandStack().pop();
		Object obj1 = frame.getOperandStack().pop();
		
		Term term2 = null;
		Term term1 = null;
		
		if(obj1 instanceof ObjectrefVariable || obj2 instanceof ObjectrefVariable) {
			ComparePairTerms compairPair = generateComparePair(obj1, obj2);
			if(compairPair == null) {
				throw new RuntimeException("Could not generate the compair values for this instruction!");
			}
			term1 = compairPair.term1;
			term2 = compairPair.term2;
		} else {
			term1 = (Term)obj1;
			term2 = (Term)obj2;
		}

		// Check if both values are constant.
		if (term1.isConstant() && term2.isConstant()) {
			// Execute without generating a choice point.
			int value1 = ((IntConstant) term1).getIntValue();
			int value2 = ((IntConstant) term2).getIntValue();
			if (compare(value1, value2)) {
				frame.getVm().setPC(this.lineNumber + (this.otherBytes[0] << ONE_BYTE | this.otherBytes[1]));
			}
		} else {
			// Create the ConstraintExpression and generate a new ChoicePoint. It will set the pc.
			ConstraintExpression expression = getConstraintExpression(term1, term2);
			try {
				((SymbolicVirtualMachine) frame.getVm()).generateNewChoicePoint(this, expression);
			} catch (VmRuntimeException e) {
				SymbolicExceptionHandler handler = new SymbolicExceptionHandler(frame, e);
				try {
					handler.handleException();
				} catch (ExecutionException e2) {
					executionFailedSymbolically(e2);
				}
			}
		}
	}
	
	public ComparePairTerms generateComparePair(Object obj1, Object obj2) {
		if(obj1 instanceof ObjectrefVariable) {
			if(obj2 instanceof ObjectrefVariable) {
				return generateComparePair((ObjectrefVariable) obj1, (ObjectrefVariable)obj2);
			} else if(obj2 instanceof NumericVariable) {
				return generateComparePair((ObjectrefVariable) obj1, (NumericVariable)obj2);
			} else if(obj2 instanceof NumericConstant) {
				return generateComparePair((ObjectrefVariable) obj1, (NumericConstant)obj2);
			} else {
				return null;
			}
		} else if(obj2 instanceof ObjectrefVariable) {
			return generateComparePair(obj2, obj1);
		}
		
		return null;
	}
	
	public ComparePairTerms generateComparePair(ObjectrefVariable objRef1, ObjectrefVariable objRef2) {
		NumericVariable nv1 = getValueFieldOfObjectref(objRef1);
		NumericVariable nv2 = getValueFieldOfObjectref(objRef2);
		return new ComparePairTerms(nv1, nv2);
	}
	
	public ComparePairTerms generateComparePair(ObjectrefVariable objRef, NumericConstant constant) {
		NumericVariable nv = getValueFieldOfObjectref(objRef);
		return new ComparePairTerms(nv, constant);
	}
	
	public ComparePairTerms generateComparePair(ObjectrefVariable objRef, NumericVariable variable) {
		NumericVariable nv = getValueFieldOfObjectref(objRef);
		return new ComparePairTerms(nv, variable);
	}
	
	public NumericVariable getValueFieldOfObjectref(ObjectrefVariable objRef) {
		ClassFile classFile = objRef.getInitializedClass().getClassFile();
		
		if(classFile.getName().equals(Character.class.getName())
			|| classFile.getName().equals(Boolean.class.getName())
			|| classFile.getName().equals(Integer.class.getName())) {
			
			Field valueField = classFile.getFieldByName("value");
			Object value = objRef.getField(valueField);
			return (NumericVariable)value;
		} else {
			throw new RuntimeException("Cannot get value field from object reference varialbe");
		}
	}

	/**
	 * Get the number of other bytes for this instruction.
	 * @return The number of other bytes.
	 */
	@Override
	public int getNumberOfOtherBytes() {
		return 2;
	}

	/**
	 * Compare two int values. Return true if the expected condition is met, false otherwise.
	 *
	 * @param value1 The first int value.
	 * @param value2 The second int value.
	 * @return true if the expected condition is met, false otherwise.
	 */
	protected abstract boolean compare(int value1, int value2);

	/**
	 * Get the ConstraintExpression for this instruction.
	 *
	 * @param term1 The left-hand term.
	 * @param term2 The right-hand term.
	 * @return A new ConstraintExpression.
	 */
	protected abstract ConstraintExpression getConstraintExpression(Term term1, Term term2);

	/**
	 * Return the target pc of the possible jump.
	 *
	 * @return The target pc of the possible jump.
	 */
	public int getJumpTarget() {
		return this.lineNumber + (this.otherBytes[0] << ONE_BYTE | this.otherBytes[1]);
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
		byte[] types = {ClassFile.T_INT, ClassFile.T_INT};
		return types;
	}
	
	public class ComparePairTerms {
		protected Term term1;
		protected Term term2;
		public ComparePairTerms(Term term1, Term term2) {
			this.term1 = term1;
			this.term2 = term2;
		}
	}
}
