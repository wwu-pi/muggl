package de.wwu.muggl.javaee.testcase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.wwu.muggl.javaee.jaxrs.SymbolicResponse;
import de.wwu.muggl.javaee.rest.RESTResource;
import de.wwu.muggl.javaee.testcase.obj.ObjectBuilder;
import de.wwu.muggl.javaee.testcase.obj.impl.EntityObjectBuilder;
import de.wwu.muggl.javaee.ws.MugglRESTResponse;
import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.expressions.NumericConstant;
import de.wwu.muggl.vm.initialization.Arrayref;
import de.wwu.muggl.vm.initialization.Objectref;

/**
 * This class builds a RESTful web service.
 * 
 * @author Andreas Fuchs
 */
public class RESTfulWebServiceBuilder {

	protected String packageName = "de.wwu.pi.ws";
	
	protected Solution solution;
	
	protected Set<RESTResource> resourceSet;
	
	// map for key=targetURL and value=ServiceName, 
	// e.g.,    http://example/webapi = Service001
	protected Map<String, String> serviceNameMap;
	
	// map for key=ServiceName and value=resources for that service
	// e.g.,    Service001 = {resource1, resource2}
	protected Map<String, Set<RESTResource>> serviceResourceMap;
	
	public RESTfulWebServiceBuilder(Set<RESTResource> resourceSet, Solution solution) {
		this.resourceSet = resourceSet;
		this.solution = solution;
		this.serviceNameMap = new HashMap<>();
		this.serviceResourceMap = new HashMap<>();
		buildServiceNameMap();
	}
	
	protected void buildServiceNameMap() {
		for(RESTResource resource : this.resourceSet) {
			String targetURL = resource.getTarget().getTargetUrl();
			String serviceName = serviceNameMap.get(targetURL);
			if(serviceName == null) {
				serviceName = "Service" + resource.hashCode();
				serviceNameMap.put(targetURL, serviceName);
			}
			
			Set<RESTResource> resourceSet = serviceResourceMap.get(serviceName);
			if(resourceSet ==  null) {
				resourceSet = new HashSet<>();
			}
			resourceSet.add(resource);
			serviceResourceMap.put(serviceName, resourceSet);
		}
	}
	
	public void buildServices(StringBuilder sb) {
		for(String serviceName : serviceResourceMap.keySet()) {
			String servicePath = "";
			buildService(sb, serviceName, servicePath, serviceResourceMap.get(serviceName));
		}
	}
	
	/**
	 * Build the JAX-RS web service.
	 */
	protected void buildService(StringBuilder sb, String serviceName, String servicePath, Set<RESTResource> resourceSet) {
		sb.append("package " + packageName + ";\n");
		
		sb.append("import javax.ws.rs.*;\n\n");
		
		sb.append("@Path(\""+servicePath+"\")\n");
		sb.append("public class "+serviceName+" {\n");
		
		for(RESTResource resource : resourceSet) {
			buildResource(resource, sb);
		}
		
		sb.append("}\n");		
	}
	
	/**
	 * Build a REST method for the given resource.
	 */
	protected void buildResource(RESTResource resource, StringBuilder sb) {
		String responseType = resource.getResponse().getInitializedClass().getClassFile().getName();
		String methodName = "resource" + resource.hashCode();
		
		sb.append("\t@"+resource.getRequestType()+"\n");
		sb.append("\t@Path(\""+resource.getTarget().getPath()+"\")\n");
		sb.append("\t@Produces(MediaType.APPLICATION_JSON)\n");
		sb.append("\tpublic " + responseType + " " + methodName + "(");
		
		int paramCounter = 0;
		// add path parameter
		Map<String, Object> templateMap = resource.getTarget().getTemplateMap();
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
		Map<String, Object> queryParamMap = resource.getTarget().getQueryParamMap();
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
		
		sb.append(") {\n");
		
		if(resource.getResponse() instanceof SymbolicResponse) {
			// this is a symbolic object-ref for: javax.ws.rs.Response
			generateRESTResponse((SymbolicResponse)resource.getResponse(), sb);
		} else {
			// this is a symbolic object-ref of _any_ value of the resource (could be String, etc..)
			generateResponseObject(resource.getResponse(), sb);
		}
		
		sb.append("\t}\n");
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
	

	/**
	 * Build the main application for JAX-RS.
	 */
	protected void buildRESTApplication(StringBuilder sb, String applicationPath, Set<String> serviceClassNameSet) {
		sb.append("package " + packageName + ";\n");
		
		sb.append("import java.util.HashSet;\n");
		sb.append("import java.util.Set;\n");
		sb.append("import javax.ws.rs.ApplicationPath;\n");
		sb.append("import javax.ws.rs.core.Application;\n\n");
		
		sb.append("@ApplicationPath(\"/"+applicationPath+"\")\n");
		sb.append("public class WarehouseApplication extends Application {\n");
		sb.append("\t@Override\n");
		sb.append("\tpublic Set<Class<?>> getClasses() {\n");
		sb.append("\t\tfinal Set<Class<?>> classes = new HashSet<>();\n");
		
		for(String serviceClassName : serviceClassNameSet) {
			sb.append("\t\tclasses.add("+serviceClassName+".class);\n");
		}
		
		sb.append("\t\treturn classes;\n");
		sb.append("\t}\n");
		sb.append("}\n");
	}
}
