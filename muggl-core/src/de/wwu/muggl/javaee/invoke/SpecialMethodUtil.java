package de.wwu.muggl.javaee.invoke;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * @author Andreas Fuchs
 */
public class SpecialMethodUtil {
	
	protected Map<String, Objectref> classRefCache;
	
	private static SpecialMethodUtil instance;
	
	private SpecialMethodUtil() {
		this.classRefCache = new HashMap<>();
	}
	
	public static synchronized SpecialMethodUtil getInstance() {
		if(instance == null) {
			instance = new SpecialMethodUtil();
		}
		return instance;
	}

	/**
	 * Get an object reference of type java.lang.Class for the given class name
	 * @param vm the symbolic virtual machine
	 * @param className the class name to get the objectref from
	 * @return an object reference of type java.lang.Class for the given class name
	 * @throws SpecialMethodInvokeException
	 */
	public Objectref getClassObjectRef(SymbolicVirtualMachine vm, String className) throws SpecialMethodInvokeException {
		if(classRefCache.containsKey(className)) {
			return classRefCache.get(className);
		}
		
		Objectref classNameObjRef = getStringObjectref(vm, className);
		
		// generating the class reference
		ClassFile classFile = null;
		try {
			classFile = vm.getClassLoader().getClassAsClassFile(Class.class.getName());
		} catch(ClassFileException e) {
			// quite unlikely this exception, but nvmd...
			throw new SpecialMethodInvokeException("Could not load required class file java.lang.Class");
		}
		
		Objectref classRef = vm.getAnObjectref(classFile);
		Field nameField = classRef.getInitializedClass().getClassFile().getFieldByName("name");
		classRef.putField(nameField, classNameObjRef);
		classRefCache.put(className, classRef);
		
		return classRef;
	}
	
	/**
	 * Get an object reference of type java.lang.String for the given string
	 * @param vm the symbolic virtual machine
	 * @param s the string
	 * @return the object reference for the string
	 * @throws SpecialMethodInvokeException
	 */
	public Objectref getStringObjectref(SymbolicVirtualMachine vm, String s) throws SpecialMethodInvokeException {
		// generating a 'String' object reference with the name of the class of 'objRef'
		ClassFile stringClassFile = null;
		ClassFile characterClassFile = null;
		try {
			stringClassFile = vm.getClassLoader().getClassAsClassFile(String.class.getName());
			characterClassFile = vm.getClassLoader().getClassAsClassFile(Character.class.getName());
		} catch(ClassFileException e) {
			// quite unlikely this exception, but nvmd...
			throw new SpecialMethodInvokeException("Could not load required class file java.lang.String");
		}
		Objectref stringRef = vm.getAnObjectref(stringClassFile);
		Objectref charRef = vm.getAnObjectref(characterClassFile);
		Field charValueField = charRef.getInitializedClass().getClassFile().getFieldByName("value");
		
		char[] classNameArray = s.toCharArray();
		Arrayref stringValues = new Arrayref(charRef, s.length());
		for(int i=0; i<s.length(); i++) {
			Objectref c0 = vm.getAnObjectref(characterClassFile);
			c0.putField(charValueField,  classNameArray[i]);
			stringValues.putElement(i, c0);
		}
		
		Field valueField = stringClassFile.getInitializedClass().getClassFile().getFieldByName("value");
		stringRef.putField(valueField, stringValues);
		
		return stringRef;
	}
	
	/**
	 * Get the class name as string form the given object reference (must be of type java.lang.Class)
	 * @param objRef the object reference of type java.lang.Class
	 * @return the string in the field 'name' of object reference, i.e. the name of the class
	 * @throws SpecialMethodInvokeException
	 */
	public String getClassNameFromObjectRef(Objectref objRef) throws SpecialMethodInvokeException {
		ClassFile classFile = objRef.getInitializedClass().getClassFile();
		if(!(classFile.getName().equals(java.lang.Class.class.getName()))) {
			throw new SpecialMethodInvokeException("Expected argument to be of type Objectref for the type java.lang.Class, but was: " + objRef);
		}
		
		Field nameField = classFile.getFieldByName("name");		
		Object nameFieldValueObj = objRef.getField(nameField);
		
		if(!(nameFieldValueObj instanceof Objectref)) {
			throw new SpecialMethodInvokeException("Exepcted name to have value of type Objectref, but was: " + nameFieldValueObj);
		}
		
		Objectref nameFieldValue = (Objectref)nameFieldValueObj;
		
		return getStringFromObjectref(nameFieldValue);
	}
	
	/**
	 * Get the string from an objectreference of type java.lang.String
	 * @param objRef the object reference
	 * @return the string from an object reference
	 * @throws SpecialMethodInvokeException
	 */
	public String getStringFromObjectref(Objectref objRef) throws SpecialMethodInvokeException{
		ClassFile classFile = objRef.getInitializedClass().getClassFile();
		if(!(classFile.getName().equals(java.lang.String.class.getName()))) {
			throw new SpecialMethodInvokeException("Expected argument to be of type Objectref for the type java.lang.String, but was: " + objRef);
		}
		
		Field valueField = classFile.getFieldByName("value");
		Object valueFieldValueObj = objRef.getField(valueField);
		
		if(!(valueFieldValueObj instanceof Arrayref)) {
			throw new SpecialMethodInvokeException("Exepcted value field to be of type Arrayref, but was: " + valueFieldValueObj);
		}
		
		Arrayref valueArray = (Arrayref)valueFieldValueObj;
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<valueArray.length; i++) {
			Object c = valueArray.getElement(i);
			if(c instanceof IntConstant)  {
				IntConstant ic = (IntConstant)c;
				int iVal = ic.getIntValue();
				char charVal = (char)iVal;
				sb.append(charVal);
			} else {
				throw new SpecialMethodInvokeException("String value char array of type: " + c + " not supported yet");
			}
		}
		
		return sb.toString();
	}
}
