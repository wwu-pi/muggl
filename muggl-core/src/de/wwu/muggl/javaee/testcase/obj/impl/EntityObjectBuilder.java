package de.wwu.muggl.javaee.testcase.obj.impl;

import de.wwu.muggl.javaee.testcase.obj.ObjectBuilder;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.Objectref;

public class EntityObjectBuilder extends ObjectBuilder {

	public EntityObjectBuilder(Solution solution) {
		super(solution);
	}

	@Override
	protected String generateNewObject(Objectref o, StringBuilder sb) {
		ClassFile classFile = o.getInitializedClass().getClassFile();
		requiredPackages.add(classFile.getName());
		
		String className = classFile.getClassName();
		
		String objName = generateNewName(o);
		
		// TODO: find a constructor if a non-empty non-public constructor is not available
		sb.append("\t\t"+className+" "+objName+" = ");
		
		// check if object is null
		if(isNull(o)) {
			sb.append("null;\n");
		} else {
			sb.append("new "+ className + "();\n");
			for(Field field : o.getFields().keySet()) {
				Object value = o.getFields().get(field);
				generateFieldValue(field, value, objName, sb);
			}
		}
		
		return objName;
	}
	
	private void generateFieldValue(Field field, Object value, String objName, StringBuilder sb) {
		String setterName = "set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
		
		if(!(value instanceof Objectref)) {
			String primValueString = getPrimitiveFieldValueAsString(value);
			sb.append("\t\t"+objName+"."+setterName+"(");
			sb.append(primValueString);
			sb.append(");\n");
		} else {
			Objectref objRef = (Objectref)value;
			if(objectRefNameMap.containsKey(objRef)) {
				String valueObjName = objectRefNameMap.get(objRef);
				sb.append("\t\t"+objName+"."+setterName+"(");
				sb.append(valueObjName);
				sb.append(");\n");
			} else {
				String fieldObjName = generateNewObject(objRef, sb);
				this.objectRefNameMap.put(objRef, fieldObjName);
				generateFieldValue(field, value, objName, sb);
			}
		}
	}

	private String generateNewName(Objectref o) {
		return o.getInitializedClass().getClassFile().getClassName().toLowerCase() + o.getInstantiationNumber();
	}
	
}
