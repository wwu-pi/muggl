package de.wwu.muggl.javaee.jpa.cstr;

import java.util.Map;
import java.util.Set;

import de.wwu.muggl.javaee.jpa.JPAEntityClassAnalyzer;
import de.wwu.muggl.javaee.jpa.cstr.types.JPAStaticConstraint;
import de.wwu.muggl.javaee.jpa.cstr.types.MaxConstraint;
import de.wwu.muggl.javaee.jpa.cstr.types.MinConstraint;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.GreaterOrEqual;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

public class StaticConstraintManager {
	
	private static StaticConstraintManager instance;
	
	protected StaticConstraintBuilder builder;
	
	private StaticConstraintManager() {
		this.builder = new StaticConstraintBuilder();
	}

	public void setStaticConstraints(Objectref entity, SymbolicVirtualMachine vm) throws EntityConstraintException {
		// check if the given object reference is indeed an entity type
		if(!JPAEntityClassAnalyzer.getInst().isEntityType(entity)) {
			throw new EntityConstraintException("Object reference must be an entity type, but was: " + entity);
		}
		
		// get the entity class
		ClassFile entityClassFile = entity.getInitializedClass().getClassFile();
		
		Map<Field, Set<JPAStaticConstraint>> constraintSet = builder.getConstraintSet(entityClassFile);
		
		for(Field field : constraintSet.keySet()) {
			for(JPAStaticConstraint constraint : constraintSet.get(field)) {
				applyConstraint(constraint, entity, vm);
			}
		}
	}
	
	private void applyConstraint(JPAStaticConstraint constraint, Objectref entity, SymbolicVirtualMachine vm) throws EntityConstraintException {
		if(constraint instanceof MaxConstraint) {
			MaxConstraint max = (MaxConstraint)constraint;
			Field field = max.getField();
			applyMaxConstraint(entity, field, max.getMaxValue(), vm);
		} else if(constraint instanceof MinConstraint) {
			MinConstraint min = (MinConstraint)constraint;
			Field field = min.getField();
			applyMinConstraint(entity, field, min.getMinValue(), vm);
		} else {
			throw new EntityConstraintException("Constraint ["+constraint+"] not handeled yet");
		}
	}

	
	private void applyMinConstraint(Objectref entity, Field field, int minValue, SymbolicVirtualMachine vm) throws EntityConstraintException {
		Object minFieldValue = entity.getField(field);
		if(minFieldValue instanceof NumericVariable) {
			NumericVariable nv = (NumericVariable)minFieldValue;
			vm.getSolverManager().addConstraint(
					GreaterOrEqual.newInstance(
							nv, NumericConstant.getInstance(minValue, Expression.INT)));
		} else {
			throw new EntityConstraintException("Applying a minimum constraint on ["+minFieldValue+"] not supported yet");
		}
	}

	private void applyMaxConstraint(Objectref entity, Field field, int maxValue, SymbolicVirtualMachine vm) throws EntityConstraintException {
		Object maxFieldValue = entity.getField(field);
		if(maxFieldValue instanceof NumericVariable) {
			NumericVariable nv = (NumericVariable)maxFieldValue;
			vm.getSolverManager().addConstraint(
					GreaterOrEqual.newInstance(
							NumericConstant.getInstance(maxValue, Expression.INT), 
							nv));
		} else {
			throw new EntityConstraintException("Applying a maximum constraint on ["+maxFieldValue+"] not supported yet");
		}
	}

	public static synchronized StaticConstraintManager getInst() {
		if(instance == null) {
			instance = new StaticConstraintManager();
		}
		return instance;
	}
}
