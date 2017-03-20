package de.wwu.muggl.symbolic.var;

import java.io.PrintStream;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.GreaterOrEqual;
import de.wwu.muggl.solvers.expressions.Max;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Sum;
import de.wwu.muggl.solvers.expressions.TypeCheckException;
import de.wwu.muggl.solvers.expressions.ref.meta.ReferenceVariable;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.symbolic.var.arr.SymbolicArray;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

/**
 * A variable for an array reference.
 * 
 * @author Andreas Fuchs
 */
public class ArrayrefVariable extends Arrayref implements ReferenceVariable, ReferenceValue, Comparable<ArrayrefVariable> {

	/**
	 * The type of this array.
	 */
	protected ReferenceValue arrayType;
	
	/**
	 * The length of this array.
	 */
	protected NumericVariable length;
	
	/**
	 * Internal variable for the maximum index. Constraint: maxIndex+1 = length
	 */
	protected NumericVariable maxIndex;
	
	/**
	 * The name of this variable.
	 */
	protected String name;
	
	/**
	 * A flag to indicate that this reference-variable is indeed null.
	 */
	protected NumericVariable isNull;
	
	/**
	 * The virtual machine that initiates this reference variable.
	 */
	protected SymbolicVirtualMachine vm;
	
	/**
	 * The symblic array for this variable.
	 */
	protected SymbolicArray symbolicArray;
	
	/**
	 * Create a new array reference variable.
	 * @param name the name of the variable.
	 * @param arrayType the type of the array
	 * @param vm the virtual machine
	 * @throws SolverUnableToDecideException 
	 * @throws TimeoutException 
	 */
	public ArrayrefVariable(String name, ReferenceValue arrayType, SymbolicVirtualMachine vm) throws TimeoutException, SolverUnableToDecideException {
		super(arrayType, 0);
		this.vm = vm;
		this.name = name;
		this.arrayType = arrayType;
		this.symbolicArray = new SymbolicArray();
		this.length = new NumericVariable(name+".length", Expression.INT);
		this.maxIndex = new NumericVariable(name+".maxIndex", Expression.INT);
		this.isNull = new NumericVariable(name+".isNull", Expression.BOOLEAN);
		this.vm.getSolverManager().addConstraint(GreaterOrEqual.newInstance(length, NumericConstant.getZero(Expression.INT)));
		this.vm.getSolverManager().addConstraint(GreaterOrEqual.newInstance(maxIndex, NumericConstant.getZero(Expression.INT)));
		this.vm.getSolverManager().addConstraint(NumericEqual.newInstance(length, Sum.newInstance(this.maxIndex, NumericConstant.getOne(Expression.INT))));
		if(!this.vm.getSolverManager().hasSolution()) {
			throw new RuntimeException("Cannot generate array reference, no solution possible!");
		}
	}
	
	
	public void addElementAt(int index, Object element) {
		if(index < 0) {
			throw new RuntimeException("Sorry, but an index must be >= 0");
		}
		this.vm.getSolverManager().addConstraint(GreaterOrEqual.newInstance(this.maxIndex, NumericConstant.getInstance(index, Expression.INT)));
		this.symbolicArray.addElementAt(index, element);
	}
	
	public void addElementAt(NumericVariable index, Object element) {
		this.symbolicArray.addElementAt(index, element);
		this.vm.getSolverManager().addConstraint(GreaterOrEqual.newInstance(index, NumericConstant.getZero(Expression.INT)));
		this.vm.getSolverManager().addConstraint(Max.newInstance(this.symbolicArray.getSymElementMap().keySet(), this.maxIndex));
	}
	
	
	
	
	
	@Override
	public NumericVariable getIsNullVariable() {
		return this.isNull;
	}

	@Override
	public String toString(boolean useInternalVariableNames) {
		return name +" (" + this.getClass().getSimpleName()+")";
	}
	
	@Override
	public String getInternalName() {
		return this.name;
	}

	@Override
	public byte getType() {
		return -1;
	}
	
	@Override
	public int compareTo(ArrayrefVariable o) {
		return -1;
	}
	
	
	
	
	
	
	
	
	@Override
	public void checkTypes() throws TypeCheckException {}

	@Override
	public Expression insert(Solution solution, boolean produceNumericSolution) {return null;}

	@Override
	public Expression insertAssignment(Assignment assignment) {	return null; }

	@Override
	public boolean isBoolean() {return false;}

	@Override
	public boolean isConstant() {return false;}

	@Override
	public String toTexString(boolean useInternalVariableNames) {return null;}

	@Override
	public String toHaskellString() {return null;}

	@Override
	public String toTexString(boolean inArrayEnvironment, boolean useInternalVariableNames) {	return null;}

	@Override
	public void writeToLog(PrintStream logStream) {	}

	@Override
	public boolean isInternalVariable() {return false;}
}
