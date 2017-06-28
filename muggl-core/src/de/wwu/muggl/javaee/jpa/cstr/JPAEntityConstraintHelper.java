//package de.wwu.muggl.javaee.jpa.cstr;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import de.wwu.muggl.javaee.jpa.JPAEntityClassAnalyzer;
//import de.wwu.muggl.javaee.jpa.cstr.types.JPAStaticConstraint;
//import de.wwu.muggl.javaee.jpa.cstr.types.MaxConstraint;
//import de.wwu.muggl.javaee.jpa.cstr.types.MinConstraint;
//import de.wwu.muggl.solvers.expressions.Expression;
//import de.wwu.muggl.solvers.expressions.GreaterOrEqual;
//import de.wwu.muggl.solvers.expressions.NumericConstant;
//import de.wwu.muggl.solvers.expressions.NumericVariable;
//import de.wwu.muggl.vm.classfile.ClassFile;
//import de.wwu.muggl.vm.classfile.structures.Field;
//import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
//import de.wwu.muggl.vm.initialization.Objectref;
//
//
//public class JPAEntityConstraintHelper {
//
//	private static JPAEntityConstraintHelper instance;
//	
//	private Map<String, Set<JPAStaticConstraint>> constraints;
//	
//	private JPAEntityConstraintHelper() {
//		this.constraints = new HashMap<>();
//	}
//	
//	public void setStaticConstraints(Objectref entity, SymbolicVirtualMachine vm) throws JPAEntityConstraintException {
//		// check if the given object reference is indeed an entity type
//		if(!JPAEntityClassAnalyzer.getInst().isEntityType(entity)) {
//			throw new JPAEntityConstraintException("Object reference must be an entity type, but was: " + entity);
//		}
//		
//		// get the entity class
//		ClassFile entityClassFile = entity.getInitializedClass().getClassFile();
//		
//		// add static constraints
//		Set<JPAStaticConstraint> constraintSet = this.constraints.get(entityClassFile.getName());
//		if(constraintSet == null) {
//			constraintSet = StaticConstraintBuilder.getInst().generateConstraintSet(entityClassFile);
//			this.constraints.put(entityClassFile.getName(), constraintSet);
//		}
//		
//		for(JPAStaticConstraint cstr : constraintSet) {
//			applyConstraint(entity, cstr, vm);
//		}
//		
//	}
//
//
//	private void applyConstraint(Objectref entity, JPAStaticConstraint cstr, SymbolicVirtualMachine vm) throws JPAEntityConstraintException {
//		if(cstr instanceof MaxConstraint) {
//			MaxConstraint max = (MaxConstraint)cstr;
//			Field field = max.getField();
//			applyMaxConstraint(entity, field, max.getMaxValue(), vm);
//		} else if(cstr instanceof MinConstraint) {
//			MinConstraint min = (MinConstraint)cstr;
//			Field field = min.getField();
//			applyMinConstraint(entity, field, min.getMinValue(), vm);
//		} else {
//			throw new JPAEntityConstraintException("Constraint ["+cstr+"] not handeled yet");
//		}
//	}
//
//	private void applyMaxConstraint(Objectref entity, Field field, int maxValue, SymbolicVirtualMachine vm) throws JPAEntityConstraintException {
//		Object maxFieldValue = entity.getField(field);
//		if(maxFieldValue instanceof NumericVariable) {
//			NumericVariable nv = (NumericVariable)maxFieldValue;
//			vm.getSolverManager().addConstraint(
//					GreaterOrEqual.newInstance(
//							NumericConstant.getInstance(maxValue, Expression.INT), 
//							nv));
//		} else {
//			throw new JPAEntityConstraintException("Applying a maximum constraint on ["+maxFieldValue+"] not supported yet");
//		}
//	}
//	
//	private void applyMinConstraint(Objectref entity, Field field, int minValue, SymbolicVirtualMachine vm) throws JPAEntityConstraintException {
//		Object minFieldValue = entity.getField(field);
//		if(minFieldValue instanceof NumericVariable) {
//			NumericVariable nv = (NumericVariable)minFieldValue;
//			vm.getSolverManager().addConstraint(
//					GreaterOrEqual.newInstance(
//							nv, NumericConstant.getInstance(minValue, Expression.INT)));
//		} else {
//			throw new JPAEntityConstraintException("Applying a minimum constraint on ["+minFieldValue+"] not supported yet");
//		}
//	}
//
//	public static synchronized JPAEntityConstraintHelper getInst() {
//		if(instance == null) {
//			instance = new JPAEntityConstraintHelper();
//		}
//		return instance;
//	}
//}
