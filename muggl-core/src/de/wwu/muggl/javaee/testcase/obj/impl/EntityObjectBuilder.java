package de.wwu.muggl.javaee.testcase.obj.impl;

import de.wwu.muggl.javaee.testcase.obj.ObjectBuilder;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.symbolic.var.ArrayrefVariable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

public class EntityObjectBuilder extends ObjectBuilder {

	public EntityObjectBuilder(Solution solution) {
		super(solution);
	}

	@Override
	protected String generateNewObject(Objectref o, StringBuilder sb) {
		ClassFile classFile = o.getInitializedClass().getClassFile();
		
		// check for special objects to be built
		if(classFile.getName().equals(java.lang.String.class.getName())) {
			return generateNewStringObject(o,sb);
		}
		if(classFile.getName().equals(java.util.ArrayList.class.getName())) {
			return generateNewArrayListObject(o,sb);
		}
		
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
	
	
	private String generateNewArrayListObject(Objectref o, StringBuilder sb) {
		String objName = generateNewName(o);

		String type = "Object";
		
		Field elementDataField = o.getInitializedClass().getClassFile().getFieldByName("elementData");
		Object elementDataValue = o.getField(elementDataField);
		if(elementDataValue != null) {
			if(elementDataValue instanceof ArrayrefVariable) {
				ArrayrefVariable arrayData = (ArrayrefVariable)elementDataValue;
				int length = ((NumericConstant)solution.getValue(arrayData.getSymbolicLength())).getIntValue();
				for(int i=0; i<length; i++) {
					Object ele = arrayData.getElement(i);
					String eleName = generateNewObject((Objectref)ele, sb);
					sb.append("\t\tobjName.add("+eleName+");\n");
				}
			} else {
				Arrayref arrayData = (Arrayref)elementDataValue;
				throw new RuntimeException("build array ref");
			}
		}
		
		sb.append("\t\tjava.util.ArrayList<" + type + "> "+objName+" = ");
		
		if(isNull(o)) {
			sb.append("null;\n");
		} else {
			sb.append("new java.util.ArrayList<>();\n");
		}
		
		return objName;
	}

	private String generateNewStringObject(Objectref o, StringBuilder sb) {
		String objName = generateNewName(o);
		
		if(isNull(o)) {
			sb.append("\t\tString "+objName+" = null;\n");
			return objName;
		}
		
		sb.append("\t\tString "+objName+" = new String(\"");
		Field valueField = o.getInitializedClass().getClassFile().getFieldByName("value");
		Object stringValueObj = o.getField(valueField);
		if(stringValueObj != null) {
			Arrayref valueCharArray = (Arrayref)stringValueObj;
			for(int i=0;i<valueCharArray.length; i++) {
				Object charEle = valueCharArray.getElement(i);
				if(charEle instanceof NumericConstant) {
					NumericConstant nc = (NumericConstant)charEle;
					char c = (char)nc.getIntValue();
					sb.append(c);
				} else if(charEle instanceof NumericVariable) {
					NumericVariable nv = (NumericVariable)charEle;
					NumericConstant nc = (NumericConstant)solution.getValue(nv);
					char c = (char)nc.getIntValue();
					sb.append(c);
				} else if(charEle instanceof Objectref && ((Objectref)charEle).getInitializedClass().getClassFile().getName().equals(Character.class.getName())) {
					Objectref co = (Objectref)charEle;
					Object v = co.getInitializedClass().getClassFile().getFieldByName("value");
					if(v != null && v instanceof NumericConstant) {
						NumericConstant nc = (NumericConstant)v;
						char c = (char)nc.getIntValue();
						sb.append(c);
					} else if(v != null && v instanceof NumericVariable) {
						NumericVariable nv = (NumericVariable)v;
						NumericConstant nc = (NumericConstant)solution.getValue(nv);
						char c = (char)nc.getIntValue();
						sb.append(c);
					} else {
						sb.append("?");
					}
				} else {
					sb.append("?");
				}
			}
		}
		sb.append("\");\n");
		return objName;
	}

	@Override
	protected String generateNewArray(Arrayref a, StringBuilder sb) {
		ClassFile referencedClassFile = a.getReferenceValue().getInitializedClass().getClassFile();
		requiredPackages.add(referencedClassFile.getName());
		
		String className = referencedClassFile.getClassName();
		
		String arrName = a.getReferenceValue().getInitializedClass().getClassFile().getClassName().toLowerCase() + a.getReferenceValue().getInstantiationNumber();
		
		sb.append("\t\t"+className);
		for(int i=0;i<a.getDimensions().length; i++) {
			sb.append("[]");
		}
		sb.append(" " + arrName + " = new " + className);
		for(int i=0;i<a.getDimensions().length; i++) {
			sb.append("[");
			
			int elementsInDimension = a.getDimensions()[i];
			
			if(a instanceof ArrayrefVariable) {
				ArrayrefVariable arefVar = (ArrayrefVariable)a;
				NumericVariable symbolicLengthVar = arefVar.getSymbolicLength();
				NumericConstant lengthConstant = this.solution.getNumericValue(symbolicLengthVar);
				if(lengthConstant == null) {
					elementsInDimension = 0;
				} else {
					elementsInDimension = lengthConstant.getIntValue();
				}
			}
			
			sb.append(elementsInDimension);
			sb.append("]");
		}
		sb.append(";\n");
		
		int length = a.length;
		if(a instanceof ArrayrefVariable) {
			ArrayrefVariable arefVar = (ArrayrefVariable)a;
			NumericVariable symbolicLengthVar = arefVar.getSymbolicLength();
			NumericConstant lengthConstant = this.solution.getNumericValue(symbolicLengthVar);
			if(lengthConstant == null) {
				length = 0;
			} else {
				length = lengthConstant.getIntValue();
			}
		}
		
		for(int i=0; i<length; i++) {
			Objectref element = (Objectref) a.getElement(i);
			String elementName = getObjectName(element, sb);
			sb.append("\t\t"+arrName+"["+i+"] = " + elementName + ";\n");
		}
		
		return arrName;
	}
	
	
	
	
	
	private void generateFieldValue(Field field, Object value, String objName, StringBuilder sb) {
		String setterName = "set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
		
		
		if(value instanceof Arrayref) {
			Arrayref arrRef = (Arrayref)value;
			String arrName = generateNewArray(arrRef, sb);
			sb.append("\t\t"+objName+"."+setterName+"(");
			sb.append(arrName);
			sb.append(");\n");
		} else if(value instanceof Objectref) {
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
		} else {
			String primValueString = getPrimitiveFieldValueAsString(value);
			sb.append("\t\t"+objName+"."+setterName+"(");
			sb.append(primValueString);
			sb.append(");\n");
		}
	}


	private String generateNewName(Objectref o) {
		return o.getInitializedClass().getClassFile().getClassName().toLowerCase() + o.getInstantiationNumber();
	}
	
}
