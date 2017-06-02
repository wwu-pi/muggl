package de.wwu.muggl.javaee.jpa;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.configuration.JavaEEConstants;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeRuntimeVisibleAnnotations;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.Annotation;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * This is an entity class file analyzer, that provides utility methods.
 * 
 * @author Andreas Fuchs
 */
public class JPAEntityClassAnalyzer {
	
	protected SymbolicVirtualMachine vm;
	
	// key = entity class name
	// value = the id field of this entity
	protected Map<String, Field> entityIdFieldMap;
	
	// key = fully qualified name of a class
	// value = flag to indicate that the class is an entity class or not
	protected Map<String, Boolean> isEntityClassMap;
	
	public JPAEntityClassAnalyzer(SymbolicVirtualMachine vm) {
		this.vm = vm;
		this.entityIdFieldMap = new HashMap<>();
		this.isEntityClassMap = new HashMap<>();
	}

	public boolean isEntityClass(Objectref dbObjRef) {
		ClassFile classFile = dbObjRef.getInitializedClass().getClassFile();
		String entityClassName = classFile.getName();
		
		Boolean isEntity = this.isEntityClassMap.get(entityClassName);
		
		if(isEntity == null) {
			// initially, we say that this class is not annotated with @Entity
			this.isEntityClassMap.put(entityClassName, false);
			
			Constant[] constantPool = classFile.getConstantPool();
			for(Attribute attribute : classFile.getAttributes()) {
				if (attribute.getStructureName().equals("attribute_runtime_visible_annotation")) {
					AttributeRuntimeVisibleAnnotations attributeAnnotation = (AttributeRuntimeVisibleAnnotations) attribute;
					for(Annotation annotation : attributeAnnotation.getAnnotations()) {
						String annotationName = constantPool[annotation.getTypeIndex()].getStringValue();
						if(annotationName.equals(JavaEEConstants.ANNOTATION_PERSISTENCE_ENTITY)) {
							// now we know that this class is annotated with @Entity
							this.isEntityClassMap.put(entityClassName, true);
						}
					}
				}
			}
		}
		
		return this.isEntityClassMap.get(entityClassName);
	}
	
	/**
	 * Get the field that is annotated with @Id for the given entity class.
	 * @param entityName the name of the entity class
	 * @return the field that is annotated with @Id
	 * @throws SymbolicDatabaseException when class file analyzing fails
	 */
	public Field getIdField(String entityName) throws SymbolicDatabaseException {
		Field idField = this.entityIdFieldMap.get(entityName);
		if(idField == null) {
			 try {
				ClassFile entityClassFile = vm.getClassLoader().getClassAsClassFile(entityName);
				Constant[] constantPool = entityClassFile.getConstantPool();
				for(Field field : entityClassFile.getFields()) {
					for (Attribute attribute : field.getAttributes()) {
						if (attribute.getStructureName().equals("attribute_runtime_visible_annotation")) {
							AttributeRuntimeVisibleAnnotations attributeAnnotation = (AttributeRuntimeVisibleAnnotations) attribute;
							for(Annotation annotation : attributeAnnotation.getAnnotations()) {
								String annotationName = constantPool[annotation.getTypeIndex()].getStringValue();
								if(annotationName.equals(JavaEEConstants.ANNOTATION_PERSISTENCE_ID)) {
									this.entityIdFieldMap.put(entityName, field);
									return field;
								}
							}
						}
					}
				}
				
				// if not found for the given entity -> check its superclass
				ClassFile superClass = entityClassFile.getSuperClassFile();
				if(superClass != null) {
					return getIdField(superClass.getName());
				}
			} catch (ClassFileException e) {
				throw new SymbolicDatabaseException("Could not find entity class: " + entityName, e);
			}
		}
		
		throw new SymbolicDatabaseException("Could not find id field of entity class: " + entityName);
	}
}
