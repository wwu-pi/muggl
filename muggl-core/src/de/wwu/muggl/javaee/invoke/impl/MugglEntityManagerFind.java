package de.wwu.muggl.javaee.invoke.impl;

import de.wwu.muggl.javaee.invoke.SpecialMethodInvocation;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jpa.MugglEntityManager;
import de.wwu.muggl.javaee.jpa.SymbolicDatabaseException;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * A special method invocation for:
 * javax.persistence.EntityManager#find(Class,Object)
 * @author Andreas Fuchs
 *
 */
public class MugglEntityManagerFind implements SpecialMethodInvocation {

	@Override
	public void execute(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		Object mugglEntityManagerObj = frame.getOperandStack().pop();
		if(!(mugglEntityManagerObj instanceof MugglEntityManager)) {
			throw new SpecialMethodInvokeException("Expected object to be of type MugglEntityManager, but was: " + mugglEntityManagerObj);
		}
		
		MugglEntityManager mem = (MugglEntityManager)mugglEntityManagerObj;
		
		String className = getClassName(parameters[1]);
		Object idValue = parameters[2];
		
		// check if the symbolic database already has an entity of the same type with the same identifier
		try {
			Objectref dbObjRef = mem.getDB().getEntityById(className, idValue);
			if(dbObjRef != null) {
				// an entity with the id already exists in database
				// push this entity to the operand stack
				mem.getDB().addRequiredEntity(dbObjRef, vm.getSolverManager().getConstraintLevel());
				frame.getOperandStack().push(dbObjRef);
			} else {
				// there exists no entity with the given id
				// create a new object-reference-variable as
				// a result of the #find method
				ClassFile classFile = null;
				try {
					classFile = vm.getClassLoader().getClassAsClassFile(className);
				} catch(ClassFileException e) {
					throw new SpecialMethodInvokeException("Could not load required class file: " + className);
				}
				
				String entityFindName = "FindResult@" + frame.getMethod().getName() + ":" + frame.getPc();
				ObjectrefVariable entityFindVar = new ObjectrefVariable(entityFindName, new InitializedClass(classFile, vm), vm);
				
				mem.getDB().addRequiredEntity(entityFindVar, vm.getSolverManager().getConstraintLevel());
				frame.getOperandStack().push(entityFindVar);
			}
		} catch (SymbolicDatabaseException e) {
			throw new SpecialMethodInvokeException("Error while generating find result object", e);
		}
	}
	
	private String getClassName(Object object) throws SpecialMethodInvokeException {
		if(object instanceof ObjectrefVariable) {
			throw new SpecialMethodInvokeException("Objectref-Variables are not supported yet");
		}
		
		if(object instanceof Objectref) {
			Objectref objRef = (Objectref)object;
			Field nameField = objRef.getInitializedClass().getClassFile().getFieldByNameAndDescriptor("name", "Ljava/lang/String;");
			Objectref nameObjRef = (Objectref)objRef.getField(nameField); // Objectref of type String, now get the char array of it for the 'real' string values
			Field valueField = nameObjRef.getInitializedClass().getClassFile().getFieldByNameAndDescriptor("value", "[C");
			Arrayref valueArray = (Arrayref)nameObjRef.getField(valueField);
			StringBuffer sb = new StringBuffer();
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
		
		throw new SpecialMethodInvokeException("Only object reference types supported yet");
	}

	@Override
	public String toString() {
		return "Special execution of EntityManager#find(Class,Object) via the MugglEntityManager";
	}
}
