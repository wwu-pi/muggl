package de.wwu.muggl.javaee.testcase;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.javaee.testcase.obj.ObjectBuilder;
import de.wwu.muggl.javaee.testcase.obj.impl.EntityObjectBuilder;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.IntConstant;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.symbolic.testCases.JavaEETestCaseSolution;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * JUnit test case builder for classes and methods using JPA to interact with a database.
 * 
 * @author Andreas Fuchs
 */
public class JPATestCaseBuilder {

	protected JavaEETestCaseSolution solution;
	
	protected StringBuilder sb;

	protected String methodUnderTestName;
	
	protected String classUnderTestType;
	
	protected ObjectBuilder objBuilder;
	
	public JPATestCaseBuilder(JavaEETestCaseSolution solution) {
		this.objBuilder = new EntityObjectBuilder(solution.getSolution());
		this.solution = solution;
		this.sb = new StringBuilder();
		this.methodUnderTestName = solution.getInitialMethod().getName();
		this.classUnderTestType = solution.getInitialMethod().getClassFile().getClassName();
	}

	public void build() {
		buildPackageDeclaration();
		buildImports();
		buildClassBody();
		Globals.getInst().symbolicExecLogger.info("******************************************************");
		Globals.getInst().symbolicExecLogger.info("\n");
		Globals.getInst().symbolicExecLogger.info("\n"+sb.toString());
		Globals.getInst().symbolicExecLogger.info("\n");
		Globals.getInst().symbolicExecLogger.info("******************************************************");
	}

	private void buildPackageDeclaration() {
		sb.append("package xyz;\n\n");
	}

	private void buildImports() {
		// import CDI classes
		sb.append("import javax.inject.Inject;\n");
		
		// import JPA classes
		sb.append("import javax.persistence.EntityManager;\n");
		sb.append("import javax.persistence.PersistenceContext;\n");
		
		// import JUnit classes
		sb.append("import org.junit.Test;\n");
		sb.append("import static org.junit.Assert.*;\n");
		
		// import the class under test
		sb.append("import " + solution.getInitialMethod().getClassFile().getName() + ";\n");
		
		sb.append("\n");
	}

	private void buildClassBody() {
		sb.append("public class X {\n\n");
		
		// inject the JPA entity manager
		sb.append("\t@PersistenceContext\n\tprotected EntityManager em;\n\n");
		
		// inject the class under test
		sb.append("\t@Inject\n\tprotected "+classUnderTestType+" classUnderTest;\n\n");
		
		buildTestMethods();
		sb.append("}\n");
	}

	private void buildTestMethods() {
		int i=0;
		while(solution != null) {
			objBuilder.reset();
			buildTestMethod(solution, "test"+ i++);
			solution = (JavaEETestCaseSolution)solution.getSuccessor();
		}
	}

	private void buildTestMethod(JavaEETestCaseSolution solution, String testMethodName) {
		sb.append("\t@Test\n");
		sb.append("\t// Solution: "+solution.getSolution()+"\n");
		sb.append("\t// Return value: "+solution.getReturnValue()+"\n");
		sb.append("\tpublic void "+testMethodName+"() {\n");
		buildPreExecutionRequiredDatabase();
		buildInvokeMethodUnderTest();
		buildCheckPostExecutionExpectedDatabase();
		sb.append("\t}\n\n");
	}

	private void buildPreExecutionRequiredDatabase() {
		sb.append(solution.getPreExecutionRequiredDatabaseString());
	}
	
	private void buildInvokeMethodUnderTest() {
		buildMethodArguments();
		sb.append("\t\t");
		
		// if method has a return type, we save it in variable 'result'
		String retType = this.solution.getInitialMethod().getReturnType();
		if(!(retType.equals("void"))) {
			sb.append(retType + " result = ");
		}
		
		// if method has parameters, we add them as arg0, arg1, ...
		sb.append("this.classUnderTest."+this.methodUnderTestName+"(");
		
		// build the start of the parameters, if not static, start is index 1
		int i=0;
		if(!this.solution.getInitialMethod().isAccStatic()) { i = 1; }
		
		for(; i<this.solution.getParameters().length; i++) {
			sb.append("arg"+i);
			if(i < this.solution.getParameters().length-1) {
				sb.append(", ");
			}
		}
		
		sb.append(");\n");
		
		if(!(retType.equals("void"))) {
			buildCheckResult();
		}
	}
	
	private void buildCheckResult() {
		sb.append("\t\t");
		// the result must equal the expected return value from the symbolic execution
		sb.append("assertEquals(");
		sb.append(getTransformedResultValue(this.solution.getReturnValue()));
		sb.append(", result);\n");
	}

	private String getTransformedResultValue(Object actualReturnValue) {
		// get the return type of the method
		String returnType = this.solution.getInitialMethod().getReturnType();
		
		// check if boolean -> transform 0->false and 1->true
		if(returnType.equals("boolean") || returnType.equals("java.lang.Boolean")) {
			if(actualReturnValue instanceof IntConstant) {
				IntConstant ic = (IntConstant)actualReturnValue;
				actualReturnValue = ic.getIntValue();
			}
			
			if(actualReturnValue instanceof Integer) {
				Integer b = (Integer)actualReturnValue;
				if(b == 0) {
					return "false";
				}
				if(b == 1) {
					return "true";
				}
				throw new RuntimeException("Expect boolean value to be either 0 or 1");
			}
		}
		
		if(actualReturnValue instanceof NumericVariable) {
			NumericVariable nv = (NumericVariable)actualReturnValue;
			NumericConstant nc = (NumericConstant)solution.getSolution().getValue(nv);
			return getNumericConstantString(nc);
		}
		
		if(actualReturnValue instanceof NumericConstant) {
			return getNumericConstantString((NumericConstant)actualReturnValue);
		}
		
		// if null, return "null" string
		if(actualReturnValue == null) {
			return "null";
		}
		
		// if no special transformation required -> simply return .toString()
		return actualReturnValue.toString();
	}
	
	private String getNumericConstantString(NumericConstant nc) {
		switch(nc.getType()) {
			case Expression.INT    : return nc.getIntValue()+"";
			case Expression.LONG   : return nc.getLongValue()+"L";
			case Expression.SHORT  : return nc.getIntValue()+"s";
			case Expression.DOUBLE : return nc.getDoubleValue()+"";
			case Expression.FLOAT  : return nc.getFloatValue()+"";
			default: return null;
		}
	}

	private void buildMethodArguments() {
		Object[] parameters = this.solution.getParameters();
		int i=0;
		if(!this.solution.getInitialMethod().isAccStatic()) {
			i = 1;
		}
		for(;i<parameters.length; i++) {
			generateArgument(parameters[i], i);
		}
	}

	private void generateArgument(Object o, int i) {
		String argName = "arg"+i;
		
		if(o instanceof Objectref) {
			generateObjectRefArgument((Objectref)o, argName);
		}
		
		else if(o instanceof NumericConstant) {
			generateNumericConstantArgument((NumericConstant)o, argName);
		}
		
		else {
			sb.append("\t\t// TODO type ["+o+"]not supported yet, implement it in class " + this.getClass().getName() + "\n");
		}
	}

	private void generateNumericConstantArgument(NumericConstant o, String argName) {
		String value = getNumericConstantString(o);
		switch(o.getType()) {
			case Expression.INT    : { sb.append("\t\tint " + argName + " = " + value +    ";\n");  break; }
			case Expression.LONG   : { sb.append("\t\tlong " + argName + " = " + value +   ";\n");  break; }
			case Expression.SHORT  : { sb.append("\t\tshort " + argName + " = " + value +  ";\n");  break; }
			case Expression.DOUBLE : { sb.append("\t\tdouble " + argName + " = " + value + ";\n");  break; }
			case Expression.FLOAT  : { sb.append("\t\tfloat " + argName + " = " + value +  ";\n");  break; }
			default: sb.append("\t\t// type : " + o.getType() + " not supported yet for numeric values, implement it in class: " + this.getClass().getName());
		}
	}

	private void generateObjectRefArgument(Objectref o, String argName) {
		String argType = o.getInitializedClass().getClassFile().getName();
		String argValue = objBuilder.getObjectName(o, sb);
		sb.append("\t\t"+argType+" "+argName+" = "+ argValue +";\n");
	}

	private void buildCheckPostExecutionExpectedDatabase() {
		sb.append("\t\t// check post execution expected data\n");
		sb.append("\t\t// nothing to check...\n");
	}
}
