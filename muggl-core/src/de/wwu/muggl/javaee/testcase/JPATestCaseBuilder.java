package de.wwu.muggl.javaee.testcase;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.symbolic.testCases.TestCaseSolution;

/**
 * JUnit test case builder for classes and methods using JPA to interact with a database.
 * 
 * @author Andreas Fuchs
 */
public class JPATestCaseBuilder {

	protected TestCaseSolution solution;
	
	protected StringBuilder sb;

	protected String methodUnderTestName;
	
	protected String classUnderTestType;
	
	public JPATestCaseBuilder(TestCaseSolution solution) {
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
			buildTestMethod(solution, "test"+ i++);
			solution = solution.getSuccessor();
		}
	}

	private void buildTestMethod(TestCaseSolution solution, String testMethodName) {
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
		sb.append("\t\t// pre-execution required data\n");
		sb.append("\t\t// no data is required to exists before MUT is invoked\n");
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
		for(int i=0; i<this.solution.getParameters().length; i++) {
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
		sb.append(this.solution.getReturnValue());
		sb.append(", result);\n");
	}

	private void buildMethodArguments() {
		int i=0;
		for(Object o : this.solution.getParameters()) {
			generateArgument(o, i++);
		}
	}

	private void generateArgument(Object o, int i) {
		String argType = "Object";
		String argName = "arg"+i;
		sb.append("\t\t"+argType+" "+argName+" = "+o+";\n");
	}

	private void buildCheckPostExecutionExpectedDatabase() {
		sb.append("\t\t// check post execution expected data\n");
		sb.append("\t\t// nothing to check...\n");
	}
}
