package muggl.javax.persistence;

import java.util.Stack;

import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.instructions.invokespecial.util.SpecialMethodHelper;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.javaee.jpa.MugglTypedQuery;
import de.wwu.muggl.javaee.jpa.SymbolicQueryResultList;
import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.impl.symbolic.SymbolicVirtualMachine;
import de.wwu.muggl.vm.initialization.Objectref;

@InvokeSpecialClass(className="javax.persistence.TypedQuery")
public class TypedQuery {

	@InvokeSpecialMethod(name="setParameter", signature="(Ljava/lang/String;Ljava/lang/Object;)Ljavax/persistence/TypedQuery;")
	public static void setParameter(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglTypedQuery typedQuery = getTypedQuery(frame.getOperandStack());
		
		String paraName = SpecialMethodHelper.getStringFromObjectref((Objectref)parameters[1]);
		Object paraValue = parameters[2];
		
		typedQuery.setParameter(paraName, paraValue);
		
		frame.getOperandStack().push(typedQuery);
	}
	
	@InvokeSpecialMethod(name="getResultList", signature="()Ljava/util/List;")
	public static void getResultList(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
		MugglTypedQuery query = getTypedQuery(frame.getOperandStack());
		SymbolicVirtualMachine vm = (SymbolicVirtualMachine)frame.getVm();
		frame.getOperandStack().push(new SymbolicQueryResultList("query-result", vm));
	}
	
	protected static MugglTypedQuery getTypedQuery(Stack<Object> stack) throws SpecialMethodInvokeException {
		Object typedQueryObject = stack.pop();
		if(!(typedQueryObject instanceof MugglTypedQuery)) {
			throw new SpecialMethodInvokeException("Expected object to be of type MugglTypedQuery, but was: " + typedQueryObject);
		}
		return (MugglTypedQuery)typedQueryObject;
	}
	
}
