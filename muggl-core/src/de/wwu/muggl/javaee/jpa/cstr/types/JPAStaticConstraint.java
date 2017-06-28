package de.wwu.muggl.javaee.jpa.cstr.types;

import de.wwu.muggl.vm.classfile.structures.Field;

public abstract class JPAStaticConstraint {

	protected String entityClassName;
	protected Field field;
	
	public JPAStaticConstraint(String entityClassName, Field field) {
		this.entityClassName = entityClassName;
		this.field = field;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public Field getField() {
		return field;
	}
	
}
