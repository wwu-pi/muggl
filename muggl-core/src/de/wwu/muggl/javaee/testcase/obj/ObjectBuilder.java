package de.wwu.muggl.javaee.testcase.obj;

import java.util.HashMap;
import java.util.Map;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * Object Generator for JUnit test cases.
 * 
 * @author Andreas Fuchs
 */
public class ObjectBuilder {

	// the generated names of the object references
	protected Map<Objectref, String> objectRefNameMap;
	
	// the constraint solver solution
	protected Solution solution;
	
	public ObjectBuilder(Solution solution) {
		this.objectRefNameMap = new HashMap<>();
		this.solution = solution;
	}

	/**
	 * Return the generated object name.
	 * If the object has not been generated yet, then generate it.
	 * @param o the object reference for which the object should be generated.
	 * @param sb the string builder used to generate a new object in case it has not yet been generated
	 * @return the generated name
	 */
	public String getObjectName(Objectref o, StringBuilder sb) {
		String objName = this.objectRefNameMap.get(o);
		
		if(objName == null) {
			// object has not yet been generated, so generate it!
			objName  = generateNewObject(o, sb);
			this.objectRefNameMap.put(o, objName);
		}
		
		return objName;
	}

	private String generateNewObject(Objectref o, StringBuilder sb) {
		String objName = generateNewName(o);
		sb.append("\t\t// generate new object for : " + objName + "\n");
		return objName;
	}
	
	private String generateNewName(Objectref o) {
		
		// check if object is null
		if(isNull(o)) {
			return "null";
		}
		
		if(o instanceof ObjectrefVariable) {
			ObjectrefVariable v = (ObjectrefVariable)o;
			return v.getVariableName();
		}
		
		return "obj"+o.getInstantiationNumber();
	}

	private boolean isNull(Objectref o) {		
		if(o == null) {
			// check here: either return 'true' because object reference is indeed null
			// or implement new symbolic execution handling
			// for example:
			// 1.   Object o = new Object();
			// 2.   o = null; // <-- explicitly set to null <- ADD NEW OBJECT_NULL_REFERENCE.
			throw new ObjectBuilderException("Object reference is null, cannot build it");
		}
		
		// only an object reference VARIABLE can be either null or not null
		if(o instanceof ObjectrefVariable) {
			ObjectrefVariable v = (ObjectrefVariable)o;
			NumericVariable nv = v.getIsNullVariable();
			NumericConstant c = (NumericConstant)this.solution.getValue(nv);
			if(c == null) {
				// no restrictions have been made if the given object is null / is not null
				// so we simply say: it is null
				return true;
			}
			int b = c.getIntValue();
			if(b == 0) {
				return false;
			}
			if(b == 1) {
				return true;
			}
			throw new ObjectBuilderException("The variable to indicate that an object reference is null must be either 0 or 1, but was:" + b);
		}
		
		// if not an object reference VARIABLE, it can never be null
		return false;
	}
	
	
}
