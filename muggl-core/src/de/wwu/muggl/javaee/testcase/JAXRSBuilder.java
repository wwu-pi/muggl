package de.wwu.muggl.javaee.testcase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.wwu.muggl.javaee.jaxrs.SymbolicResponse;
import de.wwu.muggl.javaee.rest.RESTResource;
import de.wwu.muggl.javaee.testcase.obj.ObjectBuilder;
import de.wwu.muggl.javaee.testcase.obj.impl.EntityObjectBuilder;
import de.wwu.muggl.javaee.ws.MugglRESTResponse;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

public class JAXRSBuilder {

	protected Set<RESTResource> resourceSet;
	protected Solution solution;
	
	public JAXRSBuilder(Set<RESTResource> resourceSet, Solution solution) {
		this.resourceSet = resourceSet;
		this.solution = solution;
	}

	public void build(String outputDir) {
		for(RESTResource res : resourceSet) {
			buildResource(res, outputDir);
		}
	}

	private void buildResource(RESTResource res, String outputDir) {
		StringBuilder sb = new StringBuilder();
		
		String resourcePath = getResourcePath(res);
		String resourceClassName = getResourceClassName(res);
		
		String requestType = res.getRequestType().name();
		String responseType = getResponseType(res);
		
		sb.append("@Path(\""+resourcePath+"\")\n");
		sb.append("public class " + resourceClassName + " {\n\n");
		
		sb.append("\t@"+requestType+"\n");
		sb.append("\t@Produces(MediaType.APPLICATION_JSON)\n");
		sb.append("\t@Consumes(MediaType.APPLICATION_JSON)\n");
		sb.append("\tpublic " + responseType + " getResource(");
		generateArguments(res, sb);
		sb.append(") {\n");
		generateReturnValue(res, sb);
		sb.append("\t}\n");
		sb.append("}");
	
		String packageDir = "\\resources";
		
		writeToDisc(sb, outputDir, packageDir, resourceClassName);
	}

	private void writeToDisc(StringBuilder sb, String outputDir, String packageDir, String resourceClassName) {
		File outputFile = new File(outputDir + "/" + packageDir + "/" + resourceClassName + ".java");
		outputFile.getParentFile().mkdirs();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
		    writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateReturnValue(RESTResource res, StringBuilder sb) {
		if(res.getResponse() instanceof SymbolicResponse) {
			// this is a symbolic object-ref for: javax.ws.rs.Response
			generateRESTResponse((SymbolicResponse)res.getResponse(), sb);
		} else {
			// this is a symbolic object-ref of _any_ value of the resource (could be String, etc..)
			generateResponseObject(res.getResponse(), sb);
		}
	}
	
	private void generateRESTResponse(SymbolicResponse response, StringBuilder sb) {
		String responseEntityName = null;
		if(response.getEntity() != null) {
			ObjectBuilder objBuilder = new EntityObjectBuilder(solution);
			responseEntityName = objBuilder.getObjectName(response, sb);
		}
		
		NumericConstant nc = this.solution.getNumericValue(response.getStatus());
		int statusCode = nc != null ? nc.getIntValue() : 0;
		
		sb.append("\t\treturn javax.ws.rs.Response.status("+statusCode+")");
		
		if(responseEntityName != null) {
			sb.append(".entity("+responseEntityName+")");
		}
		
		sb.append(".build();\n");
	}

	private void generateResponseObject(MugglRESTResponse response, StringBuilder sb) {
		ObjectBuilder objBuilder = new EntityObjectBuilder(solution);
		String responseName = objBuilder.getObjectName(response, sb);
		sb.append("\t\treturn " + responseName+";\n");
	}

	private void generateArguments(RESTResource res, StringBuilder sb) {
		int paramCounter = 0;
		// add path parameter
		Map<String, Object> templateMap = res.getTarget().getTemplateMap();
		for(String paramName : templateMap.keySet()) {
			sb.append("@PathParam(\""+paramName+"\")");
			
			Object value = templateMap.get(paramName);
			if(value instanceof Objectref) {
				Objectref objValue = (Objectref)value;
				String valueClassName = objValue.getInitializedClass().getClassFile().getName();
				sb.append(" "+valueClassName+" "+paramName);
			} else {
				throw new RuntimeException("Not supported yet");
			}
			
			if((++paramCounter) < templateMap.size()) {
				sb.append(", ");
			}
		}
		
		// add query parameter
		Map<String, Object> queryParamMap = res.getTarget().getQueryParamMap();
		for(String paramName : queryParamMap.keySet()) {
			sb.append("@QueryParam(\""+paramName+"\")");
			
			Object value = queryParamMap.get(paramName);
			if(value instanceof Arrayref) {
				Arrayref arrValue = (Arrayref)value;
				Object element = arrValue.getElement(0);
				
				if(element instanceof Objectref) {
					Objectref objValue = (Objectref)element;
					String valueClassName = objValue.getInitializedClass().getClassFile().getName();
					sb.append(" "+valueClassName+" "+paramName);
				} else {
					throw new RuntimeException("Not supported yet");
				}
			} else {
				throw new RuntimeException("Not supported yet");
			}
			
			if((++paramCounter) < templateMap.size()) {
				sb.append(", ");
			}
		}
	}

	private String getResponseType(RESTResource res) {
		return res.getResponse().getInitializedClass().getClassFile().getName();
	}

	private String getResourcePath(RESTResource res) {
		return res.getTarget().getPath();
	}
	
	private String getResourceClassName(RESTResource res) {
		int rnd = UUID.randomUUID().hashCode();
		if(rnd < 0) {
			rnd = rnd*(-1);
		}
		return "Resource"+rnd;
	}

}
