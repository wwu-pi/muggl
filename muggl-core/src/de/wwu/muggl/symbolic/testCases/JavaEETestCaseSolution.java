package de.wwu.muggl.symbolic.testCases;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.wwu.muggl.javaee.jaxws.MugglWebServiceManager;
import de.wwu.muggl.javaee.jaxws.WebServiceManager;
import de.wwu.muggl.javaee.jaxws.WebServiceResponse;
import de.wwu.muggl.javaee.jaxws.sym.WebService;
import de.wwu.muggl.javaee.jpa.SymbolicDatabase;
import de.wwu.muggl.javaee.rest.RESTResource;
import de.wwu.muggl.javaee.testcase.JAXWSBuilder;
import de.wwu.muggl.javaee.testcase.RESTfulWebServiceBuilder;
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
	
	protected Set<RESTResource> requiredRESTResources;
	
	protected Set<WebService> webServiceSet;
	
	// the string builder for the pre-execution required database state
	protected StringBuilder dbPre;
	
	// the string builder for the required web services
	protected StringBuilder wsReq;
	
	public JavaEETestCaseSolution(Method initialMethod, Solution solution, Object returnValue,
			SymbolicDatabase symbolicDatabase, 
			Set<RESTResource> requiredRESTResources, Set<WebService> webServiceSet,
			boolean throwsAnUncaughtException,
			Object[] variables, boolean[] dUCoverage, Map<Method, boolean[]> cFCoverageMapping) {
		super(initialMethod, solution, returnValue, throwsAnUncaughtException, variables, dUCoverage, cFCoverageMapping);
		this.symbolicDatabase = symbolicDatabase;
		this.requiredRESTResources = requiredRESTResources;
		this.webServiceSet = webServiceSet;
		init();
	}

	public JavaEETestCaseSolution(Method initialMethod, Solution solution, Object returnValue,
			SymbolicDatabase symbolicDatabase, 
			Set<RESTResource> requiredRESTResources, Set<WebService> webServiceSet,
			boolean throwsAnUncaughtException,
			Object[] variables, boolean[] dUCoverage, Map<Method, boolean[]> cFCoverageMapping,
			TestCaseSolution latestSolutionFound) {
		super(initialMethod, solution, returnValue, throwsAnUncaughtException, variables, dUCoverage, cFCoverageMapping, latestSolutionFound);
		this.symbolicDatabase = symbolicDatabase;
		this.requiredRESTResources = requiredRESTResources;
		this.webServiceSet = webServiceSet;
		init();
	}

	private void init() {
		this.objectBuilder = new EntityObjectBuilder(solution);
		this.dbPre = new StringBuilder();
		this.wsReq = new StringBuilder();
		buildPreExecutionRequiredDatabase();
		buildRequiredWebServices();
	}
	
	private void buildRequiredWebServices() {
		buildREST();
		buildJAXWS();
	}
	
	private void buildJAXWS() {
		JAXWSBuilder jaxWSBuilder = new JAXWSBuilder(this.webServiceSet, this.solution, this.initialMethod);
		jaxWSBuilder.build("C:\\_WORK\\_muggl\\_workspace-ws\\counter-client-test\\gen-tests");		
	}
	
	private void buildREST() {
		RESTfulWebServiceBuilder restBuilder = new RESTfulWebServiceBuilder(this.requiredRESTResources, this.solution);
		restBuilder.buildServices(this.wsReq);
	}

	private void buildPreExecutionRequiredDatabase() {
		dbPre.append("\t\t// pre-execution required data\n");
		boolean preExeDataExists = false; // flag to indicate that no pre execution required data exists
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
		if(objRef instanceof ObjectrefVariable) {
			ObjectrefVariable objRefVar = (ObjectrefVariable) objRef;
			NumericVariable nv = objRefVar.getIsNullVariable();
			NumericConstant nc = (NumericConstant)this.solution.getValue(nv);
			if(nc != null && nc.getIntValue() == 0) {
				dbPre.append("\t\t// generate data for: " + objRef+"\n");
				String entityName = objectBuilder.getObjectName(objRef, dbPre);
				dbPre.append("\t\tem.persist("+entityName+");\n");
				return true;
			}
		}
		return false;
	}

	public String getPreExecutionRequiredDatabaseString() {
		return this.dbPre.toString();
	}
	
	public String getRequiredWebServicesString() {
		return this.wsReq.toString();
	}
}
