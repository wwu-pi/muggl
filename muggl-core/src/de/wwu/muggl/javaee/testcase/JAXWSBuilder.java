package de.wwu.muggl.javaee.testcase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import de.wwu.muggl.configuration.Options;
import de.wwu.muggl.javaee.jaxws.WebServiceResponse;
import de.wwu.muggl.javaee.jaxws.objref.MugglWSPort;
import de.wwu.muggl.javaee.jaxws.sym.Operation;
import de.wwu.muggl.javaee.jaxws.sym.Port;
import de.wwu.muggl.javaee.jaxws.sym.WebService;
import de.wwu.muggl.javaee.testcase.obj.impl.EntityObjectBuilder;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.structures.Method;

public class JAXWSBuilder {
	
	protected EntityObjectBuilder objBuilder;
	protected Set<WebService> webServiceSet;
	protected Solution solution;
	protected Method initialMethod;
	
	public JAXWSBuilder(Set<WebService> webServiceSet, Solution solution, Method initialMethod) {
		this.webServiceSet = webServiceSet;
		this.solution = solution;
		this.initialMethod = initialMethod;
		this.objBuilder = new EntityObjectBuilder(solution);
	}

	
	public void build(String outputBaseDir) {
		
		Set<String> endpointSet = new HashSet<>();
		int counter = 0;
		
		for(WebService webService : webServiceSet) {
			for(Port port : webService.getPorts()) {
				String packageName = port.getInitializedClass().getClassFile().getPackageName();
				String className = webService.getName() + webService.hashCode() + "0";
				String packageDirectory = packageName.replace(".", "/");
				File file = new File(outputBaseDir + "/" + packageDirectory + "/" + className + ".java");
				file.getParentFile().mkdirs();
				int i=0;
				while(file.exists() && i < 1000) {
					className = webService.getName() + webService.hashCode() + i++;
					file = new File(outputBaseDir + "/" + packageDirectory + "/" + className + ".java");
				}
				StringBuilder sb = buildWebService(port, webService.getName(), webService.getTargetNamespace(), className, packageName);
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				    writer.write(sb.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				generateInterface(
						outputBaseDir, 
						port.getInitializedClass().getClassFile().getPackageName(),
						port.getInitializedClass().getClassFile().getClassName(),
						port.getOperationMap().keySet());
				
				generateJUnitTestMethod(port, webService.getWsdlLocation(), className, counter++, endpointSet);
				
			}
			
		}
	}
	
	private StringBuilder generateJUnitTestMethod(Port port, String wsdlLocation, String webServiceClassName,int counter, Set<String> endpointSet) {
		String testClassName = initialMethod.getClassFile().getClassName();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\t@Test\n");
		sb.append("\tpublic void test");
		sb.append(counter);
		sb.append("() {\n");
		
		String endpointName = "endpoint"+counter+""+port.getPortName();
		sb.append("\t\tthis.");
		sb.append(endpointName);
		sb.append("Endpoint.publish(\"");
		sb.append(wsdlLocation);
		sb.append("\", new ");
		sb.append(webServiceClassName);
		sb.append("());\n");
		sb.append("\t\t");
		sb.append(testClassName);
		sb.append(" cut = new ");
		sb.append(testClassName);
		sb.append("();\n");
		
		generateMUTInvoke(sb);
		
		
		
		endpointSet.add(endpointName);
		
		return sb;
	}

	private void generateMUTInvoke(StringBuilder sb) {
		if(initialMethod.getReturnType().equals("void")) {
			sb.append("\t\tcut.");
			sb.append(initialMethod.getName());
			
		} else {
			
		}
	}
	
	


	private void generateInterface(String outputBaseDir, String packageName, String className, Set<Method> methods) {
		String fullPath = outputBaseDir + "/" + packageName.replace(".", "/") + "/" + className;
		File file = new File(fullPath);
		if(!file.exists()) {
			// package declaration
			StringBuilder sb = new StringBuilder();
			sb.append("package ");
			sb.append(packageName);
			sb.append(";\n\n");
			// imports
			sb.append("import javax.jws.WebMethod;\n");
			sb.append("import javax.jws.WebService;\n\n");
			// class
			sb.append("@WebService\n");
			sb.append("public interface ");
			sb.append(className);
			sb.append(" {\n\n");
			for(Method m : methods) {
				sb.append("\t@WebMethod ");
				sb.append(m.getReturnType());
				sb.append(" ");
				sb.append(m.getName());
				String[] paraNames = m.getParameterNames();
				if(paraNames != null) {
					for(int i=0; i<paraNames.length; i++) {
						String n = paraNames[i];
						String t = m.getParameterTypeAtIndex(i);
						sb.append(t);
						sb.append(" ");
						sb.append(n);
						if((i+1)<paraNames.length) {
							sb.append(", ");
						}
					}
				}
			}
			sb.append("}\n");
			
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			    writer.write(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public StringBuilder buildWebService(Port port, String webServiceName, String targetNamespace, String className, String packageName) {
		// package declaration
		StringBuilder sbPackage = new StringBuilder();
		sbPackage.append("package ");
		sbPackage.append(packageName);
		sbPackage.append(";\n\n");
		
		// imports
		StringBuilder sbImports = new StringBuilder();
		sbImports.append("import javax.jws.WebService;\n");
		
		// @WebService annotation
		String interfaceType = port.getInitializedClass().getClassFile().getName();
		StringBuilder sbWSAnnoation = new StringBuilder();
		sbWSAnnoation.append("@WebService(\n");
		sbWSAnnoation.append("\tserviceName=\"");
		sbWSAnnoation.append(webServiceName);
		sbWSAnnoation.append("\",\n");
		sbWSAnnoation.append("\tportName=\"");
		sbWSAnnoation.append(port.getPortName());
		sbWSAnnoation.append("\",\n");
		sbWSAnnoation.append("\tendpointInterface=\"");
		sbWSAnnoation.append(interfaceType);
		sbWSAnnoation.append("\",\n");
		sbWSAnnoation.append("\ttargetNamespace=\"");
		sbWSAnnoation.append(targetNamespace);
		sbWSAnnoation.append("\")\n");
		
		// class
		StringBuilder sbClass = new StringBuilder();
		sbClass.append("public class ");
		sbClass.append(className);
		sbClass.append(" implements ");
		sbClass.append(interfaceType);
		sbClass.append("{\n\n");
		sbClass.append("\tprivate int i=0;\n\n");
		for(Entry<Method, LinkedList<Operation>> entry : port.getOperationMap().entrySet()) {
			Method method = entry.getKey();
			LinkedList<Operation> operations = entry.getValue();
			
			sbClass.append("\tpublic ");
			sbClass.append(method.getReturnType());
			sbClass.append(" ");
			sbClass.append(method.getName());
			sbClass.append("(");
			
			String[] paraNames = method.getParameterNames();
			if(paraNames != null) {
				for(String paraName : method.getParameterNames()) {
					sbClass.append(paraName);
				}
			}
			
			sbClass.append(") {\n");
			
			sbClass.append("\t\tswitch(i++) {\n");
			
			for(int i=0; i<operations.size(); i++) {
				Operation op = operations.get(i);
				Variable output = op.getOutput();
				sbClass.append("\t\t\tcase ");
				sbClass.append(i);
				sbClass.append(": {\n");
				generateOutputVariable(output, sbClass);
				sbClass.append("}\n");
			}
			
			sbClass.append("\t\t\tdefault : throw new IllegalStateException();\n");
			sbClass.append("\t\t}\n");
			sbClass.append("\t}\n");
		}
		
		sbClass.append("}\n");
		
				
		return new StringBuilder()
						.append(sbPackage)
						.append(sbImports)
						.append(sbWSAnnoation)
						.append(sbClass);
	}
	
	public StringBuilder buildWebService(WebService webService, String className, String packageName) {
		// package declaration
		StringBuilder sbPackage = new StringBuilder();
		sbPackage.append("package ");
		sbPackage.append(packageName);
		sbPackage.append(";\n\n");
		
		// imports
		StringBuilder sbImports = new StringBuilder();
		sbImports.append("import javax.jws.WebService;\n");
		
		// @WebService annotation
		StringBuilder sbWSAnnoation = new StringBuilder();
		sbWSAnnoation.append("@WebService(\n");
		sbWSAnnoation.append("\tserviceName=\"");
		sbWSAnnoation.append(webService.getName());
		sbWSAnnoation.append("\",\n");
		sbWSAnnoation.append("\tportName=\"");
		sbWSAnnoation.append("TODO");
		sbWSAnnoation.append("\",\n");
		sbWSAnnoation.append("\tendpointInterface=\"");
		sbWSAnnoation.append("TODO");
		sbWSAnnoation.append("\",\n");
		sbWSAnnoation.append("\ttargetNamespace=\"");
		sbWSAnnoation.append(webService.getTargetNamespace());
		sbWSAnnoation.append("\")\n");
		
		// class
		StringBuilder sbClass = new StringBuilder();
		sbClass.append("public class ");
		sbClass.append(className);
		sbClass.append(" implements Bar {\n\n");
		sbClass.append("\tprivate int i=0;\n\n");
		for(Port port : webService.getPorts()) {
			for(Entry<Method, LinkedList<Operation>> entry : port.getOperationMap().entrySet()) {
				Method method = entry.getKey();
				LinkedList<Operation> operations = entry.getValue();
				
				sbClass.append("\tpublic ");
				sbClass.append(method.getReturnType());
				sbClass.append(" ");
				sbClass.append(method.getName());
				sbClass.append("(");
				
				String[] paraNames = method.getParameterNames();
				if(paraNames != null) {
					for(String paraName : method.getParameterNames()) {
						sbClass.append(paraName);
					}
				}
				
				sbClass.append(") {\n");
				
				sbClass.append("\t\tswitch(i++) {\n");
				
				for(int i=0; i<operations.size(); i++) {
					Operation op = operations.get(i);
					Variable output = op.getOutput();
					sbClass.append("\t\t\tcase ");
					sbClass.append(i);
					sbClass.append(": {\n");
					generateOutputVariable(output, sbClass);
					sbClass.append("}\n");
				}
				
				sbClass.append("\t\t\tdefault : throw new IllegalStateException();\n");
				sbClass.append("\t}\n");
			}
			
			sbClass.append("}\n");
		}
				
		return new StringBuilder()
						.append(sbPackage)
						.append(sbImports)
						.append(sbWSAnnoation)
						.append(sbClass);
	}


	private void generateOutputVariable(Variable output, StringBuilder sb) {
		if(output instanceof NumericVariable) {
			NumericVariable nv = (NumericVariable)output;
			NumericConstant nc = this.solution.getNumericValue(nv);
			sb.append("\t\t\treturn ");
			sb.append(nc.getIntValue());
			sb.append(";");
		} else if(output instanceof ObjectrefVariable) {
			String obj = objBuilder.getObjectName((ObjectrefVariable)output, sb);
			sb.append("\t\treturn ");
			sb.append(obj);
			sb.append(";");
		} else {
			throw new RuntimeException("Variable of type: " +output.getClass().getName()+" not supported yet");
		}
	}

}
