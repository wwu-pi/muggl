package de.wwu.muggl.instructions.invokespecial;

/**
 * A special method invocation entry.
 * Specified by:
 * <ul>
 * 	<li>class name</li>
 *  <li>method name</li>
 *  <li>method signature</li>
 * </ul>
 * @author Andreas Fuchs
 */
public class SpecialMethodEntry {

	protected String className;
	protected String methodName;
	protected String methodSignature;
	
	public SpecialMethodEntry(String className, String methodName, String methodSignature) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof SpecialMethodEntry) {
			SpecialMethodEntry otherObj = (SpecialMethodEntry)obj;
			return this.className.equals(otherObj.className)
				&& this.methodName.equals(otherObj.methodName)
				&& this.methodSignature.equals(otherObj.methodSignature);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + className.hashCode();
		result = 31 * result + methodName.hashCode();
		result = 31 * result + methodSignature.hashCode();
		return result;
	}
}
