package de.wwu.muggl.symbolic.testCases;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.wwu.muggl.javaee.jpa.SymbolicDatabase;
import de.wwu.muggl.javaee.testcase.obj.ObjectBuilder;
import de.wwu.muggl.javaee.testcase.obj.impl.EntityObjectBuilder;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * The test case solution for a Java EE application.
 * 
 * @author Andreas Fuchs
 */
public class JavaEETestCaseSolution extends TestCaseSolution {
	
	protected ObjectBuilder objectBuilder;
	
	protected SymbolicDatabase symbolicDatabase;
	
	// the string builder for the pre-execution required database state
	protected StringBuilder dbPre;
	
	public JavaEETestCaseSolution(Method initialMethod, Solution solution, Object returnValue,
			SymbolicDatabase symbolicDatabase, boolean throwsAnUncaughtException,
			Object[] variables, boolean[] dUCoverage, Map<Method, boolean[]> cFCoverageMapping) {
		super(initialMethod, solution, returnValue, throwsAnUncaughtException, variables, dUCoverage, cFCoverageMapping);
		this.symbolicDatabase = symbolicDatabase;
		init();
	}

	public JavaEETestCaseSolution(Method initialMethod, Solution solution, Object returnValue,
			SymbolicDatabase symbolicDatabase, boolean throwsAnUncaughtException,
			Object[] variables, boolean[] dUCoverage, Map<Method, boolean[]> cFCoverageMapping,
			TestCaseSolution latestSolutionFound) {
		super(initialMethod, solution, returnValue, throwsAnUncaughtException, variables, dUCoverage, cFCoverageMapping, latestSolutionFound);
		this.symbolicDatabase = symbolicDatabase;
		init();
	}

	private void init() {
		this.objectBuilder = new EntityObjectBuilder(solution);
		this.dbPre = new StringBuilder();
		buildPreExecutionRequiredDatabase();
	}
	
	private void buildPreExecutionRequiredDatabase() {
		dbPre.append("\t\t// pre-execution required data\n");
		boolean preExeDataExists = true; // flag to indicate that no pre execution required data exists
		Map<String, Set<Objectref>> data = symbolicDatabase.getPreExecutionRequiredData();
		for(String entityName : data.keySet()) {
			for(Objectref objRef : data.get(entityName)) {
				preExeDataExists |= generateData(objRef);
			}
		}
		if(!preExeDataExists) {
			dbPre.append("\t\t// no pre-execution data required for this test case");
		}
		dbPre.append("\n");
	}

	protected boolean generateData(Objectref objRef) {
		dbPre.append("\t\t// generate data for: " + objRef+"\n");
		if(objRef instanceof ObjectrefVariable) {
			ObjectrefVariable objRefVar = (ObjectrefVariable) objRef;
			NumericVariable nv = objRefVar.getIsNullVariable();
			NumericConstant nc = (NumericConstant)this.solution.getValue(nv);
			if(nc != null && nc.getIntValue() == 0) {
				objectBuilder.getObjectName(objRef, dbPre);
//				generateNotNullObjectReference(objRefVar);
				return true;
			}
		}
		return false;
	}
	
//	private void generateNotNullObjectReference(Objectref objRef) {
//		ClassFile classFile = objRef.getInitializedClass().getClassFile();
//		String fullClassName = classFile.getName();
//		this.requiredPackages.add(fullClassName);
//		
//		String className = classFile.getClassName();
//		String objName = "foo";
//		dbPre.append("\t\t"+className+" "+objName + " = new "+className+"();\n");
//		
//		
//		
//	}

	public String getPreExecutionRequiredDatabaseString() {
		return this.dbPre.toString();
	}
	
	
}
