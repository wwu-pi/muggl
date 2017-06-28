package de.wwu.muggl.javaee.jpa.cstr.types;

import de.wwu.muggl.vm.classfile.structures.Field;

public class IdConstraint extends JPAStaticConstraint {

	public IdConstraint(String entityClassName, Field field) {
		super(entityClassName, field);
	}
	
}
