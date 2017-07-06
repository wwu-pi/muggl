package de.wwu.muggl.symbolic.searchAlgorithms.choice.getfield;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.MugglException;
import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.solvers.SolverManager;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.ConstraintExpression;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.ConstraintResetChoicePoint;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.EquationViolationException;
import de.wwu.muggl.symbolic.searchAlgorithms.choice.SolvingException;
import de.wwu.muggl.symbolic.searchAlgorithms.depthFirst.trailelements.TrailElement;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.exceptions.VmRuntimeException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicExecutionException;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;

/**
 * A choice point for instruction GETFIELD. If the object reference on which such a instruction is invoked
 * is an variable, it might be that the object-reference can either (1) be null or (2) not null.
 * 
 * @author Andreas Fuchs
 *
 */
public class GetFieldChoicePoint extends ConstraintResetChoicePoint {

	/**
	 * Flag to determine if the execution time should be measured.
	 */
	protected boolean measureExecutionTime;
	/**
	 * Temporary field for time measuring.
	 */
	protected long timeSolvingTemp;
	
	private long number;
	private ChoicePoint parent;
	private Frame frame;
	private int pc;
	private int pcNext;
	private Stack<TrailElement> trail;
	private ConstraintExpression constraintExpression;
	
//	private boolean anotherChoice;
	
	private Queue<GetFieldChoices> choices;
	private GetFieldChoices nextChoice;
	
	private ObjectrefVariable objRefVar;
	
	private Object fieldValue;
	
	public GetFieldChoicePoint(Frame frame, int pc, int pcNext, ObjectrefVariable objRefVar, Field field, ChoicePoint parent) throws SymbolicExecutionException, VmRuntimeException, EquationViolationException, SolvingException {
		super(frame);
		
		this.measureExecutionTime = Options.getInst().measureSymbolicExecutionTime;
		
		if (parent != null) {
			this.number = parent.getNumber() + 1;
			this.parent = parent;
		}
		
		this.frame = frame;
		this.pc = pc;
		this.pcNext = pcNext;
		this.trail = new Stack<TrailElement>();
		this.objRefVar = objRefVar;
//		this.anotherChoice = true;
		
		this.choices = new LinkedList<>();
		this.choices.add(GetFieldChoices.OBJECT_REF_IS_NULL);
		this.choices.add(GetFieldChoices.OBJECT_REF_IS_NOT_NULL);
		this.nextChoice = this.choices.poll();
		
		
		this.fieldValue = objRefVar.getField(field);
		
		
		
		
		
		// first constraint: object reference must be null -> throw NPE exception if this is the case
//		this.constraintExpression = NumericEqual.newInstance(
//				objRefVar.getIsNullVariable(), NumericConstant.getZero(Expression.BOOLEAN));
		
//		Object fieldValue = objRefVar.getField(field);
//		frame.getOperandStack().push(fieldValue);
		
//		SolverManager solverManager = ((SymbolicVirtualMachine) frame.getVm()).getSolverManager();
//		solverManager.addConstraint(constraintExpression);
		
		
//		
//		try {
//			if (this.measureExecutionTime) this.timeSolvingTemp = System.nanoTime();
//			if (solverManager.hasSolution()) {
//				if (this.measureExecutionTime) ((SymbolicVirtualMachine) frame.getVm()).increaseTimeSolvingForChoicePointGeneration(System.nanoTime() - this.timeSolvingTemp);
//				throw new VmRuntimeException(frame.getVm()
//						.generateExc("java.lang.NullPointerException","getfield"));
//			} else {
//				this.anotherChoice = false;
//			}
//		} catch (SolverUnableToDecideException e) {
//			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving lead to a SolverUnableToDecideException with message: " + e.getMessage());
//			tryTheNegatedConstraint(solverManager, constraintExpression);
//			this.anotherChoice = false;
//		} catch (TimeoutException e) {
//			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving lead to a TimeoutException with message: " + e.getMessage());
//			tryTheNegatedConstraint(solverManager, constraintExpression);
//			this.anotherChoice = false;
//		}
	}
	
	@Override
	public void applyStateChanges() throws SymbolicExecutionException, VmRuntimeException {
		try {
			switch(this.nextChoice) {
				case OBJECT_REF_IS_NOT_NULL: handleObjRefNotNull(); return;
				case OBJECT_REF_IS_NULL: handleObjRefIsNull(); return;
				default: throw new SymbolicExecutionException("Option " + this.nextChoice + " not implemented yet");
			}
		} catch(TimeoutException | SolverUnableToDecideException e) {
			throw new SymbolicExecutionException("Error while evaluating GETFIELD choice options", e);
		}
	}
	
	
	
	protected void handleObjRefIsNull() throws TimeoutException, SolverUnableToDecideException, VmRuntimeException, SymbolicExecutionException {
		ConstraintExpression ce = NumericEqual.newInstance(
				objRefVar.getIsNullVariable(), 
				NumericConstant.getOne(Expression.BOOLEAN));
		SolverManager solverManager = ((SymbolicVirtualMachine) frame.getVm()).getSolverManager();
		solverManager.addConstraint(ce);
		if(solverManager.hasSolution()) {
			throw new VmRuntimeException(frame.getVm().generateExc("java.lang.NullPointerException","getfield"));
		} else {
			if(hasAnotherChoice()) {
				this.nextChoice = this.choices.poll();
				applyStateChanges();
			}
		}
	}
	
	protected void handleObjRefNotNull() throws TimeoutException, SolverUnableToDecideException, VmRuntimeException, SymbolicExecutionException {
		ConstraintExpression ce = NumericEqual.newInstance(
				objRefVar.getIsNullVariable(), 
				NumericConstant.getZero(Expression.BOOLEAN));
		SolverManager solverManager = ((SymbolicVirtualMachine) frame.getVm()).getSolverManager();
		solverManager.addConstraint(ce);
		if(solverManager.hasSolution()) {
			frame.getOperandStack().push(fieldValue);
		} else {
			this.nextChoice = this.choices.poll();
			if(hasAnotherChoice()) {
				applyStateChanges();
			}
		}
	}

	@Override
	public long getNumber() {
		return number;
	}

	@Override
	public boolean hasAnotherChoice() {
//		return this.anotherChoice;
		return !this.choices.isEmpty();
	}

	@Override
	public void changeToNextChoice() throws MugglException {
		this.nextChoice = this.choices.poll();
		
		switch(this.nextChoice) {
			case OBJECT_REF_IS_NOT_NULL: this.constraintExpression = NumericEqual.newInstance(objRefVar.getIsNullVariable(), NumericConstant.getZero(Expression.BOOLEAN)); return;
			case OBJECT_REF_IS_NULL: this.constraintExpression = NumericEqual.newInstance(objRefVar.getIsNullVariable(), NumericConstant.getOne(Expression.BOOLEAN)); return;
			default: throw new SymbolicExecutionException("Option " + this.nextChoice + " not implemented yet");
		}
		
		
//		if (this.anotherChoice) {
//			this.anotherChoice = false;
//			// Negate the constraint expression.
//			this.constraintExpression = this.constraintExpression.negate();
//		}
	}

	@Override
	public Frame getFrame() {
		return this.frame;
	}

	@Override
	public int getPc() {
		return this.pc;
	}

	@Override
	public int getPcNext() {
		return this.pcNext;
	}

	@Override
	public ChoicePoint getParent() {
		return this.parent;
	}

	@Override
	public boolean changesTheConstraintSystem() {
		return true;
	}

	@Override
	public ConstraintExpression getConstraintExpression() {
		return this.constraintExpression;
	}

	@Override
	public void setConstraintExpression(ConstraintExpression constraintExpression) {
		// nothing to do here, we set our constraint by ourselves!
	}

	@Override
	public boolean hasTrail() {
		return true;
	}

	@Override
	public Stack<TrailElement> getTrail() {
		return this.trail;
	}

	@Override
	public void addToTrail(TrailElement element) {
		this.trail.push(element);
	}

	@Override
	public boolean enforcesStateChanges() {
		return true;
	}


	
//	private void tryTheNegatedConstraint(SolverManager solverManager, ConstraintExpression constraintExpression) throws EquationViolationException, SolvingException {
//		try {
//			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Cannot proceed with the jumping branch since this would violate the current constraint system. Trying the non-jumping branch...");
//			// The expression is unsolvable. Is it at least possible to use the non-jumping branch?
//			solverManager.removeConstraint();
//			solverManager.addConstraint(constraintExpression.negate());
//			if (this.measureExecutionTime) this.timeSolvingTemp = System.nanoTime();
//			if (solverManager.hasSolution()) {
//				if (this.measureExecutionTime) ((SymbolicVirtualMachine) this.frame.getVm()).increaseTimeSolvingForChoicePointGeneration(System.nanoTime() - this.timeSolvingTemp);
//				// Use the non-jumping branch.
//				this.frame.getVm().setPC(this.pcNext);
//				this.anotherChoice = false;
//			} else {
//				if (this.measureExecutionTime) ((SymbolicVirtualMachine) this.frame.getVm()).increaseTimeSolvingForChoicePointGeneration(System.nanoTime() - this.timeSolvingTemp);
//				if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Cannot proceed with the non-jumping branch either since this would violate the current constraint system. Tracking back...");
//				// Throw the appropriate Exception.
//				throw new EquationViolationException("Cannot continue with this choice point, since equations are violated.");
//			}
//		} catch (SolverUnableToDecideException e) {
//			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving again lead to a SolverUnableToDecideException with message: " + e.getMessage());
//			throw new SolvingException("Cannot continue with this choice point, since solving failed.");
//		} catch (TimeoutException e) {
//			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving again lead to a TimeoutException with message: " + e.getMessage());
//			throw new SolvingException("Cannot continue with this choice point, since solving failed.");
//		}
//	}

	@Override
	public String getChoicePointType() {
		return "getfield choice point";
	}

	
	enum GetFieldChoices {
		OBJECT_REF_IS_NULL,
		OBJECT_REF_IS_NOT_NULL
	}
}
