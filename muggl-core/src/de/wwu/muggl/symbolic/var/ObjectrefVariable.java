package de.wwu.muggl.symbolic.var;

import java.io.PrintStream;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.TypeCheckException;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.solvers.expressions.ref.meta.ReferenceVariable;
import de.wwu.muggl.solvers.solver.constraints.Assignment;
import de.wwu.muggl.symbolic.var.meta.ReferenceVariableException;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.ReferenceValue;

/**
 * A variable for an object reference.
 * 
 * @author Andreas Fuchs
 */
public class ObjectrefVariable extends Objectref implements ReferenceVariable, ReferenceValue, Comparable<ObjectrefVariable> {
	
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
	 * Generate a new object reference as a variable.
	 * @param staticReference the InitializedClass (which has been generated by the ClassFile)
	 */
	public ObjectrefVariable(String name, InitializedClass staticReference, SymbolicVirtualMachine vm) {
		this(name, staticReference, false, vm);
	}
	
	/**
	 * This constructor is only meant to be used by the primitive data type wrapper variable in this same package.
	 */
	ObjectrefVariable(String name, InitializedClass staticReference, boolean primitiveWrapper, SymbolicVirtualMachine vm) {
		super(staticReference, primitiveWrapper);
		this.name = name;
		this.vm = vm;
		this.isNull = new NumericVariable(name+".isNull", Expression.BOOLEAN);
	}
	
	/**
	 * Special handling of <code>getField</code> for an object-reference variable. 
	 * Each field is represented by a logic variable. 
	 * This method never return <code>null</code>.
	 * A field of a (object/array) reference is <code>null</code>, if its 
	 * logic variable is set to be <code>null</code> - instead of returning 
	 * a <code>null</code> value by this method. 
	 */
	@Override
	public Object getField(Field field) {
		if(this.fields.containsKey(field)) {
			return this.fields.get(field);
		}
		
		// there is no logic variable for the field yet,
		// let's generate a variable and put it on the field map
		String type = field.getType();
		String varName = this.name +"."+field.getName();
		Variable fieldVar = null;
		
		// 1) check if primitive type
		if(type.equals("byte") || type.equals("short") || type.equals("int") || type.equals("long")
	 	   || type.equals("char") || type.equals("double") || type.equals("float") || type.equals("boolean")) {
			byte dataType = Expression.Type.getPrimitiveTypeByString(type);
			fieldVar = new NumericVariable(varName, dataType);
		}
		
		// 2) primitive data type wrapper
		else if(Expression.Type.getPrimitiveWrapperTypeByString(type) != -1) {
			try {
				ClassFile classFile = vm.getClassLoader().getClassAsClassFile(type);
				fieldVar = new PrimitiveDatatypeWrapperVariable(
						name, new InitializedClass(classFile, vm), 
						Expression.Type.getPrimitiveWrapperTypeByString(type),	vm);
			} catch(ClassFileException e) {
				throw new ReferenceVariableException("Cannot create primitive type wrapper variable.", e);
			}
		}
		
		// 3) array reference
		else if(type.endsWith("[]") && !type.endsWith("[][]")) {
			String arrayType = type.substring(0, type.length()-2);
			if(field.isPrimitiveType()) {
				arrayType = Expression.Type.getPrimitiveWrapper(arrayType);
			}
			try {
				ClassFile classFile = vm.getClassLoader().getClassAsClassFile(arrayType);
				fieldVar = new ArrayrefVariable(varName, vm.getAnObjectref(classFile), vm);
			} catch(ClassFileException | TimeoutException | SolverUnableToDecideException e) {
				throw new ReferenceVariableException(e);
			}
		}
		
		// 4) must be an object reference
		else {
			try {
				ClassFile classFile = vm.getClassLoader().getClassAsClassFile(type);
				fieldVar = new ObjectrefVariable(varName, new InitializedClass(classFile, vm), vm);
			} catch(ClassFileException e) {
				throw new ReferenceVariableException(e);
			}
		}
		
		this.fields.put(field, fieldVar);
		
		return fieldVar;
	}
	
	/**
	 * Get the name of this variable.
	 */
	public String getVariableName() {
		return this.name;
	}
	
	@Override
	public NumericVariable getIsNullVariable() {
		return this.isNull;
	}
	
	@Override
	public String toString() {
		return "Variable: " + super.toString();
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
	public int compareTo(ObjectrefVariable o) {
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
