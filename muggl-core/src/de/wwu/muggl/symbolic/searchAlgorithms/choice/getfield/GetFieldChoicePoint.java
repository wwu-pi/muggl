package de.wwu.muggl.symbolic.searchAlgorithms.choice.getfield;

import java.util.Stack;

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
	
	private boolean anotherChoice;
	
	public GetFieldChoicePoint(Frame frame, int pc, int pcNext, ObjectrefVariable objRefVar, ChoicePoint parent) throws SymbolicExecutionException, VmRuntimeException, EquationViolationException, SolvingException {
		super(frame);
		if (parent != null) {
			this.number = parent.getNumber() + 1;
			this.parent = parent;
		}
		this.frame = frame;
		this.pc = pc;
		this.pcNext = pcNext;
		this.trail = new Stack<TrailElement>();
		this.measureExecutionTime = Options.getInst().measureSymbolicExecutionTime;
		this.anotherChoice = true;
		
		// first constraint: object reference must be null -> throw NPE exception if this is the case
		this.constraintExpression = NumericEqual.newInstance(
				objRefVar.getIsNullVariable(), NumericConstant.getOne(Expression.BOOLEAN));
		
		SolverManager solverManager = ((SymbolicVirtualMachine) frame.getVm()).getSolverManager();
		solverManager.addConstraint(constraintExpression);
		
		try {
			if (this.measureExecutionTime) this.timeSolvingTemp = System.nanoTime();
			if (solverManager.hasSolution()) {
				if (this.measureExecutionTime) ((SymbolicVirtualMachine) frame.getVm()).increaseTimeSolvingForChoicePointGeneration(System.nanoTime() - this.timeSolvingTemp);
				throw new VmRuntimeException(frame.getVm()
						.generateExc("java.lang.NullPointerException","getfield"));
			} else {
				this.anotherChoice = false;
			}
		} catch (SolverUnableToDecideException e) {
			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving lead to a SolverUnableToDecideException with message: " + e.getMessage());
			tryTheNegatedConstraint(solverManager, constraintExpression);
			this.anotherChoice = false;
		} catch (TimeoutException e) {
			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving lead to a TimeoutException with message: " + e.getMessage());
			tryTheNegatedConstraint(solverManager, constraintExpression);
			this.anotherChoice = false;
		}
	}

	@Override
	public long getNumber() {
		return number;
	}

	@Override
	public boolean hasAnotherChoice() {
		return this.anotherChoice;
	}

	@Override
	public void changeToNextChoice() throws MugglException {
		if (this.anotherChoice) {
			this.anotherChoice = false;
			// Negate the constraint expression.
			this.constraintExpression = this.constraintExpression.negate();
		}
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
		return false;
	}

	@Override
	public void applyStateChanges() {
	}
	
	private void tryTheNegatedConstraint(SolverManager solverManager, ConstraintExpression constraintExpression) throws EquationViolationException, SolvingException {
		try {
			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Cannot proceed with the jumping branch since this would violate the current constraint system. Trying the non-jumping branch...");
			// The expression is unsolvable. Is it at least possible to use the non-jumping branch?
			solverManager.removeConstraint();
			solverManager.addConstraint(constraintExpression.negate());
			if (this.measureExecutionTime) this.timeSolvingTemp = System.nanoTime();
			if (solverManager.hasSolution()) {
				if (this.measureExecutionTime) ((SymbolicVirtualMachine) this.frame.getVm()).increaseTimeSolvingForChoicePointGeneration(System.nanoTime() - this.timeSolvingTemp);
				// Use the non-jumping branch.
				this.frame.getVm().setPC(this.pcNext);
				this.anotherChoice = false;
			} else {
				if (this.measureExecutionTime) ((SymbolicVirtualMachine) this.frame.getVm()).increaseTimeSolvingForChoicePointGeneration(System.nanoTime() - this.timeSolvingTemp);
				if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Cannot proceed with the non-jumping branch either since this would violate the current constraint system. Tracking back...");
				// Throw the appropriate Exception.
				throw new EquationViolationException("Cannot continue with this choice point, since equations are violated.");
			}
		} catch (SolverUnableToDecideException e) {
			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving again lead to a SolverUnableToDecideException with message: " + e.getMessage());
			throw new SolvingException("Cannot continue with this choice point, since solving failed.");
		} catch (TimeoutException e) {
			if (Globals.getInst().symbolicExecLogger.isTraceEnabled()) Globals.getInst().symbolicExecLogger.trace("Solving again lead to a TimeoutException with message: " + e.getMessage());
			throw new SolvingException("Cannot continue with this choice point, since solving failed.");
		}
	}

	@Override
	public String getChoicePointType() {
		return "getfield choice point";
	}

	
}
