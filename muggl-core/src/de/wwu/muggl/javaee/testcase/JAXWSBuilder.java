package de.wwu.muggl.javaee.testcase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.wwu.muggl.javaee.jaxws.sym.Operation;
import de.wwu.muggl.javaee.jaxws.sym.Port;
import de.wwu.muggl.javaee.jaxws.sym.WebService;
import de.wwu.muggl.javaee.testcase.obj.impl.EntityObjectBuilder;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.Expression;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.solvers.expressions.NumericVariable;
import de.wwu.muggl.solvers.expressions.Variable;
import de.wwu.muggl.symbolic.var.ObjectrefVariable;
import de.wwu.muggl.vm.classfile.structures.Method;
import de.wwu.muggl.vm.exceptions.NoExceptionHandlerFoundException;
import de.wwu.muggl.vm.initialization.Objectref;

public class JAXWSBuilder {
	
	protected EntityObjectBuilder objBuilder;
	protected Set<WebService> webServiceSet;
	protected Solution solution;
	protected Method initialMethod;
	protected Object returnValue;
	protected Object[] variables;
	
	public JAXWSBuilder(Set<WebService> webServiceSet, Solution solution, Method initialMethod, Object returnValue, Object[] variables) {
		this.webServiceSet = webServiceSet;
		this.solution = solution;
		this.initialMethod = initialMethod;
		this.returnValue = returnValue;
		this.variables = variables;
		this.objBuilder = new EntityObjectBuilder(solution);
	}

	
	public void build(String outputBaseDir) {
		Map<String, String> endpointMap = new HashMap<>();
		StringBuilder sbTestMethods = new StringBuilder();
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
				
				String wsdlLocation = webService.getWsdlLocation();
				endpointMap.put(className, wsdlLocation);
				
				generateInterface(
						outputBaseDir, 
						port.getInitializedClass().getClassFile().getPackageName(),
						port.getInitializedClass().getClassFile().getClassName(),
						port.getOperationMap().keySet());
				
			}
		}
		
		

		sbTestMethods.append("\n");
		sbTestMethods.append(
				generateJUnitTestMethod(endpointMap));
		sbTestMethods.append("\n");
		
		saveJUnitMethodsAsFile(outputBaseDir, sbTestMethods);
	}
	
	private void saveJUnitMethodsAsFile(String outputBaseDir, StringBuilder sbTestMethods) {
		String packageName = initialMethod.getClassFile().getPackageName();
		String className = initialMethod.getClassFile().getClassName()+"Test"+this.hashCode();
		String packageDirectory = packageName.replace(".", "/");
		File file = new File(outputBaseDir + "/" + packageDirectory + "/" + className + ".java");
		file.getParentFile().mkdirs();
		int i=0;
		while(file.exists() && i < 1000) {
			className = initialMethod.getClassFile().getClassName()+"Test"+this.hashCode() + i++;
			file = new File(outputBaseDir + "/" + packageDirectory + "/" + className + ".java");
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
		    writer.write(sbTestMethods.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StringBuilder generateJUnitTestMethod(Map<String, String> endpointMap) {
		String testClassName = initialMethod.getClassFile().getClassName();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\t@Test\n");
		sb.append("\tpublic void test");
		sb.append(0);
		sb.append("() {\n");
		
		int endpointCtn = -1;
		for(String endpointClassName : endpointMap.keySet()) {
			String wsdlLocation = endpointMap.get(endpointClassName);
			sb.append("\t\tEndpoint endpoint");
			sb.append(++endpointCtn);
			sb.append(" = Endpoint.publish(\"");
			sb.append(wsdlLocation);
			sb.append("\", new ");
			sb.append(endpointClassName);
			sb.append("());\n");
		}		
		
		sb.append("\t\t");
		sb.append(testClassName);
		sb.append(" cut = new ");
		sb.append(testClassName);
		sb.append("();\n");
		
		generateMUTInvoke(sb);
		
		for(int i=endpointCtn; i>=0; i--) {
			sb.append("\t\tendpoint");
			sb.append(i);
			sb.append(".stop();\n");
		}
		
		sb.append("\t}\n");
		
		return sb;
	}

	@Deprecated
	private StringBuilder generateJUnitTestMethod(Port port, String wsdlLocation, String webServiceClassName,int counter, Set<String> endpointSet) {
		String testClassName = initialMethod.getClassFile().getClassName();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\t@Test\n");
		sb.append("\tpublic void test");
		sb.append(counter);
		sb.append("() {\n");
		
		String endpointName = "endpoint"+counter+""+port.getPortName();
//		sb.append("\t\tthis.");
//		sb.append(endpointName);
//		sb.append("Endpoint.publish(\"");
		sb.append("\t\tEndpoint endpoint = Endpoint.publish(\"");
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
		
		sb.append("\t\tendpoint.stop();\n");
		sb.append("\t}\n");
		
		return sb;
	}

	private void generateMUTInvoke(StringBuilder sb) {
		if(this.returnValue != null && this.returnValue instanceof NoExceptionHandlerFoundException) {
			NoExceptionHandlerFoundException ex = (NoExceptionHandlerFoundException)this.returnValue;
			String exceptionClassName = ex.getUncaughtThrowable().getInitializedClass().getClassFile().getName();
			sb.append("\t\ttry {\n");
			generateMethodArguments(sb);
			sb.append("\t\t\tcut.");
			generateMethodUnderTestCall(sb);
			sb.append("\t\t\tfail(\"Expect method to throw exception of type: ");
			sb.append(exceptionClassName);
			sb.append("\");\n");
			sb.append("\t\t} catch(");
			sb.append(exceptionClassName);
			sb.append(" e) {\n");
			sb.append("\t\t\t// this is what we expect to happen\n");
			sb.append("\t\t}\n");
		
		} else if(initialMethod.getReturnType().equals("void")) {
			generateMethodArguments(sb);
			sb.append("\t\tcut.");
			generateMethodUnderTestCall(sb);
			
		} else {
			generateMethodArguments(sb);
			
			sb.append("\t\t");
			sb.append(initialMethod.getReturnType());
			sb.append(" retVal = cut.");
			generateMethodUnderTestCall(sb);
			
			// assert its return value
			if(this.returnValue instanceof Objectref) {
				sb.append("\t\t// cannot assert object references directly...\n");
			} else if (this.returnValue instanceof NumericVariable) {
				sb.append("\t\tassertEquals(");
				NumericVariable nv = (NumericVariable)this.returnValue;
				NumericConstant nc = solution.getNumericValue(nv);
				int val = 0;
				if(nc != null) {
					val = nc.getIntValue();
				}
				generateNumericConstantValue(nv.getType(), val, sb);
//				generateNumericConstantValue(nc, sb);
				sb.append(", retVal);\n");
			} else if (this.returnValue instanceof NumericConstant) {
				sb.append("\t\tassertEquals(");
				NumericConstant nc = (NumericConstant)this.returnValue;
//				generateNumericConstantValue(nc, sb);
				generateNumericConstantValue(nc.getType(), nc.getIntValue(), sb);
				sb.append(", retVal);\n");
			} else {
				sb.append("\t\t// type: " + this.returnValue + " not handled yet for assertion\n");
			}
		}
	}
	
	private void generateNumericConstantValue(byte type, int val, StringBuilder sb) {
		switch(type) {
			case Expression.INT :     {sb.append(val); break;}
			case Expression.LONG :    {sb.append(val+"L"); break;}
			case Expression.SHORT :   {sb.append(val); break;}
			case Expression.BOOLEAN : {sb.append(val == 1 ? "true" : "false"); break;}
			case Expression.DOUBLE :  {sb.append(val); break;}
			case Expression.FLOAT :   {sb.append(val); break;}
			case Expression.CHAR :    {sb.append((char)val); break;}
		}
	}
	
	private void generateNumericConstantValueOLD(NumericConstant nc, StringBuilder sb) {
		switch(nc.getType()) {
			case Expression.INT :     {sb.append(nc.getIntValue()); break;}
			case Expression.LONG :    {sb.append(nc.getIntValue()+"L"); break;}
			case Expression.SHORT :   {sb.append(nc.getIntValue()); break;}
			case Expression.BOOLEAN : {sb.append(nc.getIntValue() == 1 ? "true" : "false"); break;}
			case Expression.DOUBLE :  {sb.append(nc.getIntValue()); break;}
			case Expression.FLOAT :   {sb.append(nc.getIntValue()); break;}
			case Expression.CHAR :    {sb.append((char)nc.getIntValue()); break;}
		}
	}
	
	private void generateMethodArguments(StringBuilder sb) {
		int varStartIdx = this.initialMethod.isAccStatic() ? 0 : 1;
		for(int i=varStartIdx; i<this.variables.length; i++) {
			generateConcreteArgumentValue(this.variables[i], "arg"+i, sb);
		}
	}
	
	private void generateMethodUnderTestCall(StringBuilder sb) {
		sb.append(initialMethod.getName());
		sb.append("(");
		
		int varStartIdx = this.initialMethod.isAccStatic() ? 0 : 1;

		for(int i=varStartIdx; i<this.variables.length; i++) {
			sb.append("arg"+i);
			if((i+1)<this.variables.length) {
				sb.append(", ");
			}
		}
		
		sb.append(");\n");
	}

	private void generateConcreteArgumentValue(Object object, String argName, StringBuilder sb) {
		if(object instanceof NumericVariable) {
			NumericVariable nv = (NumericVariable)object;
			NumericConstant nc = solution.getNumericValue(nv);
			int ncValue = nc != null ? nc.getIntValue() : 0;
			switch(nv.getType()) {
				case Expression.INT :     {sb.append("\t\tint "    +argName+" = "+ncValue+";\n"); break;}
				case Expression.LONG :    {sb.append("\t\tlong "   +argName+" = "+ncValue+"L;\n"); break;}
				case Expression.SHORT :   {sb.append("\t\tshort "  +argName+" = "+ncValue+";\n"); break;}
				case Expression.BOOLEAN : {sb.append("\t\tboolean "+argName+" = "+(ncValue == 1 ? "true" : "false")+";\n"); break;}
				case Expression.DOUBLE :  {sb.append("\t\tdouble " +argName+" = "+ncValue+";\n"); break;}
				case Expression.FLOAT :   {sb.append("\t\tfloat "+argName+" = "+ncValue+";\n"); break;}
				case Expression.CHAR :    {sb.append("\t\tchar "+argName+" = "+((char)ncValue)+";\n"); break;}
			}
		} else if(object instanceof ObjectrefVariable) {
			String name = this.objBuilder.getObjectName((ObjectrefVariable)object, sb);
			sb.append("\t\t");
			sb.append(argName);
			sb.append(" = ");
			sb.append(name);
			sb.append(";\n");
		} else {
			sb.append("\t\t//Could not generate value for argument " + argName + ". Value was: " + object+"\n");
		}
	}

	private void generateInterface(String outputBaseDir, String packageName, String className, Set<Method> methods) {
		String fullPath = outputBaseDir + "/" + packageName.replace(".", "/") + "/" + className + ".java";
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
				sb.append("(");
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
				sb.append(");\n");
			}
			sb.append("\n}\n");
			
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
		for(int iCtn=0; iCtn<port.getOperationMap().size(); iCtn++) {
			sbClass.append("\tprivate int i");
			sbClass.append(iCtn);
			sbClass.append("=0;\n");
		}
		sbClass.append("\n");
		int iCtn = 0;
		for(Entry<Method, LinkedList<Operation>> entry : port.getOperationMap().entrySet()) {
			Method method = entry.getKey();
			LinkedList<Operation> operations = entry.getValue();
			
			sbClass.append("\tpublic ");
			sbClass.append(method.getReturnType());
			sbClass.append(" ");
			sbClass.append(method.getName());
			sbClass.append("(");
			
			String[] types = method.getParameterTypesAsArray();
			if(types != null) {
				int i=0;
				for(String paraType : types) {
					sbClass.append(paraType);
					sbClass.append(" arg" + i++);
					if(i < types.length) {
						sbClass.append(", ");
					}
				}
			}
			
			sbClass.append(") {\n");
			
			if(!method.getReturnType().equals("void")) {
			
				sbClass.append("\t\tswitch(i");
				sbClass.append(iCtn++);
				sbClass.append("++) {\n");
			
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
			}
				
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
			boolean isBool = nv.getType() == Expression.BOOLEAN;
			NumericConstant nc = this.solution.getNumericValue(nv);
			sb.append("\t\t\treturn ");
			int val = nc != null ? nc.getIntValue() : 0;
			if(isBool) {
				if(val == 0) sb.append("false");
				else sb.append("true");
			} else {
				sb.append(val);
			}
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
