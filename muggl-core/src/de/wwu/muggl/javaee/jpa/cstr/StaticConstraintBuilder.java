package de.wwu.muggl.javaee.jpa.cstr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.wwu.muggl.configuration.JavaEEConstants;
import de.wwu.muggl.javaee.jpa.cstr.types.JPAStaticConstraint;
import de.wwu.muggl.javaee.jpa.cstr.types.MinConstraint;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeRuntimeVisibleAnnotations;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.Annotation;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.ElementValuePair;

public class StaticConstraintBuilder {

	// key=entity value=map(key=field, value=set of constraints for that field)
	private Map<String, Map<Field, Set<JPAStaticConstraint>>> constraints;
	
	StaticConstraintBuilder() {
		this.constraints = new HashMap<>();
	}
	
	
	/**
	 * Get the static constraint set for the given entity class type.
	 * If it has not yet been generated and cached, the set will be generated.
	 * @param entityClassFile the entity class file to generate the constraint set for
	 * @return
	 */
	public Map<Field, Set<JPAStaticConstraint>> getConstraintSet(ClassFile entityClassFile) {
		Map<Field, Set<JPAStaticConstraint>> cstrMap = constraints.get(entityClassFile.getName());
		
		if(cstrMap == null) {
			// generate a new constraint set and cache it
			Constant[] constantPool = entityClassFile.getConstantPool();
			cstrMap = new HashMap<>();
			for(Field field : entityClassFile.getFields()) {
				analyzeField(entityClassFile.getName(), constantPool, field, cstrMap);
			}
		}
		
		return cstrMap;
	}

	
	private void analyzeField(String entityName, Constant[] constantPool, Field field, Map<Field, Set<JPAStaticConstraint>> cstrMap) {
		Set<JPAStaticConstraint> fieldConstraintSet = cstrMap.get(field);
		if(fieldConstraintSet == null) {
			fieldConstraintSet = new HashSet<>();
		}
		
		for(Attribute attribute : field.getAttributes()) {
			if (attribute.getStructureName().equals("attribute_runtime_visible_annotation")) {
				AttributeRuntimeVisibleAnnotations attributeAnnotation = (AttributeRuntimeVisibleAnnotations) attribute;
				for(Annotation annotation : attributeAnnotation.getAnnotations()) {
					String annotationName = constantPool[annotation.getTypeIndex()].getStringValue();
					switch(annotationName) {
						case JavaEEConstants.ANNOTATION_VALIDATION_CONSTRAINTS_MIN : {
							for(ElementValuePair evp : annotation.getElementValuePairs()) {
								if(constantPool[evp.getElementNameIndex()].getStringValue().equals("value")) {
									int minValue = Integer.parseInt(evp.getElementValues().getStringValue());
									fieldConstraintSet.add(new MinConstraint(entityName, field, minValue));
									cstrMap.put(field, fieldConstraintSet);
									break;
								}
							}
						}	
					}
				}
			}
		}
	}
}