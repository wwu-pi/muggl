package de.wwu.muggl.javaee.jpa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

public class SymbolicDatabase {

	// key = constrain level
	// value = set of object references on this level
	protected Map<Integer, Map<String, Set<Objectref>>> preExecutionRequired;
	
	// the current symbolic virtual machine
	protected SymbolicVirtualMachine vm;
	
	// helper class to analyze entity class files
	protected JPAEntityClassAnalyzer entityAnalyzer;
	
	public SymbolicDatabase(SymbolicVirtualMachine vm) {
		this.vm = vm;
		this.preExecutionRequired = new HashMap<>();
		this.entityAnalyzer = new JPAEntityClassAnalyzer(vm);
	}	
	
	/**
	 * Add a required entity.
	 * @param dbObjRef the entity as an object-reference
	 * @param constraintLevel the constraint level to add the entity for
	 */
	public void addRequiredEntity(Objectref dbObjRef, int constraintLevel) throws SymbolicDatabaseException {
		if(!entityAnalyzer.isEntityClass(dbObjRef)) {
			throw new SymbolicDatabaseException("Given object-reference must be an entity type, e.g., its class must be annoated with @Entity.");
		}
		
		ClassFile classFile = dbObjRef.getInitializedClass().getClassFile();
		String entityClassName = classFile.getName();
		
		Map<String, Set<Objectref>> entityMap = this.preExecutionRequired.get(constraintLevel);
		if(entityMap == null) {
			entityMap = new HashMap<>();
		}
		Set<Objectref> entitySet = entityMap.get(entityClassName);
		if(entitySet == null) {
			entitySet = new HashSet<>();
		}
		entitySet.add(dbObjRef);
		entityMap.put(entityClassName, entitySet);
		this.preExecutionRequired.put(constraintLevel, entityMap);
	}

	public Objectref getEntityById(String entityClassName, Object idValue) throws SymbolicDatabaseException {
		if(entityClassName == null) {
			throw new SymbolicDatabaseException("The name of the entity class must not be null.");
		}
		if(idValue == null) {
			throw new SymbolicDatabaseException("The id value must not be null.");
		}
		
		for(Integer constraintLevel : this.preExecutionRequired.keySet()) {
			Map<String, Set<Objectref>> entityMap = this.preExecutionRequired.get(constraintLevel);
			for(String entityName : entityMap.keySet()) {
				// filter for entities of specified class
				if(entityName.equals(entityClassName)) {
					Field idField = entityAnalyzer.getIdField(entityName);
					for(Objectref objRef : entityMap.get(entityName)) {
						Object o = objRef.getField(idField);
						if(isIdValueEqual(o, idValue)) {
							return objRef;
						}
					}
				}
			}
		}
		return null;
	}	

	protected boolean isIdValueEqual(Object idValue1, Object idValue2) {
		// 
		return false;
	}


	
}
