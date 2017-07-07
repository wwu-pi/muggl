package muggl.javax.persistence;

import java.util.Stack;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jpa.JPAEntityClassAnalyzer;
import de.wwu.muggl.javaee.jpa.MugglEntityManager;
import de.wwu.muggl.javaee.jpa.SymbolicDatabaseException;
import de.wwu.muggl.javaee.jpa.cstr.EntityConstraintException;
import de.wwu.muggl.javaee.jpa.cstr.StaticConstraintManager;
import de.wwu.muggl.solvers.exceptions.SolverUnableToDecideException;
import de.wwu.muggl.solvers.exceptions.TimeoutException;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericEqual;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.initialization.Objectref;

@InvokeSpecialClass(className="javax.persistence.EntityManager")
public class EntityManager {
	
	@InvokeSpecialMethod(name="find", signature="(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;")
	public static void find(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		MugglEntityManager entityManager = getEntityManager(frame.getOperandStack());
		
		String className = SpecialMethodHelper.getClassNameFromObjectRef((Objectref)parameters[1]);
		Object idValue = parameters[2];
		
		// check if the symbolic database already has an entity of the same type with the same identifier
		try {
			Objectref dbObjRef = entityManager.getDB().getEntityById(className, idValue);
			if(dbObjRef != null) {
				// an entity with the id already exists in database
				// push this entity to the operand stack
				entityManager.getDB().addRequiredEntity(dbObjRef, vm.getSolverManager().getConstraintLevel());
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
				
				// generate a new object reference variable of the given type, i.e. of the given class in parameter
				String entityFindName = "FindResult@" + frame.getMethod().getName() + ":" + frame.getPc();
				ObjectrefVariable entityFindVar = new ObjectrefVariable(entityFindName, new InitializedClass(classFile, vm), vm);
				
				// set the given id value from the parameter as its id value
				Field idField = JPAEntityClassAnalyzer.getInst().getIdField(className, vm);
				entityFindVar.putField(idField, idValue);
				
				// check if the idValue is an object reference variable, in that case -> it cannot be null
				if(idValue instanceof ObjectrefVariable) {
					ObjectrefVariable objRefVar = (ObjectrefVariable)idValue;
					NumericVariable nv = objRefVar.getIsNullVariable();
					vm.getSolverManager().addConstraint(NumericEqual.newInstance(nv, NumericConstant.getZero(Expression.BOOLEAN)));
				}
				
				// add static entity constraints, e.g., minimum and maximum values, etc.
				StaticConstraintManager.getInst().setStaticConstraints(entityFindVar, vm);
				
				// check if there is still a solution possible
				if(!vm.getSolverManager().hasSolution()) {
					throw new SpecialMethodInvokeException("Static constraints cannot be added to object reference ["+entityFindVar+"] and cause an error");
				}				
				
				entityManager.getDB().addRequiredEntity(entityFindVar, vm.getSolverManager().getConstraintLevel());
				frame.getOperandStack().push(entityFindVar);
			}
		} catch (SymbolicDatabaseException | EntityConstraintException | TimeoutException | SolverUnableToDecideException e) {
			throw new SpecialMethodInvokeException("Error while generating find result object", e);
		}
	}
	
	
	@InvokeSpecialMethod(name="createQuery", signature="(Ljava/lang/String;Ljava/lang/Class;)Ljavax/persistence/TypedQuery;")
	public static void createQuery(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		
	}
	
	
	protected static MugglEntityManager getEntityManager(Stack<Object> stack) throws SpecialMethodInvokeException {
		Object mugglEntityManagerObj = stack.pop();
		if(!(mugglEntityManagerObj instanceof MugglEntityManager)) {
			throw new SpecialMethodInvokeException("Expected object to be of type MugglEntityManager, but was: " + mugglEntityManagerObj);
		}
		return (MugglEntityManager)mugglEntityManagerObj;
	}
}
