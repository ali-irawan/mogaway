package com.wenresearch.mogaway.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wenresearch.mogaway.core.Mogaway;
import com.wenresearch.mogaway.core.MogawayContext;
import com.wenresearch.mogaway.core.MogawayException;
import com.wenresearch.mogaway.core.ServerProperties;
import com.wenresearch.mogaway.core.ServerPropertiesConstants;
import com.wenresearch.mogaway.model.ConnectorInfo;
import com.wenresearch.mogaway.model.ConnectorModel;
import com.wenresearch.mogaway.model.InvokeCall;
import com.wenresearch.mogaway.util.DateUtil;
import com.wenresearch.mogaway.util.Util;

/**
 * Standard API controller
 * 
 * This controller serve for URL <b>api/service</b>
 * 
 * @author Ali Irawan
 * @version 1.0
 */
@Controller
@RequestMapping("api")
public class ApiController {

	private final static Logger log = LoggerFactory.getLogger(ApiController.class);

	@Autowired
	private ServerProperties properties;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/service")
	@ResponseBody
	public Map executeService(HttpServletRequest request,
			HttpServletResponse response) throws IOException, MogawayException {
		log.info("Execute service");

		InvokeCall invokeData = Util.parseJsonCall(request.getInputStream());

		log.debug("query: " + invokeData.toJson());

		// Run specified name

		return run(request, invokeData);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map run(HttpServletRequest request, InvokeCall invokeData)
			throws MogawayException, JsonParseException, JsonMappingException, IOException {

		String name = invokeData.getName();
		String procedure = invokeData.getProcedure();
		
		ServletContext application = request.getServletContext();

		ConnectorInfo connInfo = getConnectorInfo(application, name);
		if(connInfo==null){
			throw new MogawayException("Connector '" + name + "' not found");
		}
		
		String xmlFile = connInfo.getXmlFilePath();
		String pathFile = connInfo.getJsFilePath();

		// Read xml and configure it as ConnectorModel
		FileInputStream xmlInput = new FileInputStream(new File(xmlFile));
		String xmlString = Util.read(xmlInput);
		xmlInput.close();

		ConnectorModel connectorModel = null;
		try{
			connectorModel = new ConnectorModel(xmlString);
		}catch(Exception ex){
			throw new MogawayException(ex.getMessage());
		}
		
		// Read Javascript implementation code
		FileInputStream fis = new FileInputStream(new File(pathFile));
		String jsCode = Util.read(fis);
		fis.close();
		
		log.debug("Code: " + jsCode);

		// Adding procedure function call
		String args = StringUtils.arrayToCommaDelimitedString(invokeData.getParameters());
		jsCode += procedure+"("+args+");";


		Context ctx = Context.enter();
		ScriptableObject scope = ctx.initStandardObjects();
		
		
		// Inject some object into context
        Mogaway mogaway = this.prepareMogawayObject(connectorModel);
        scope.put("Mogaway", scope, mogaway);
        scope.put("Log", scope, log);
        scope.put("Headers", scope, getHeader(request));
        scope.put("Session", scope, request.getSession());
        
		// Execute the script
		Map<String,String> map = new HashMap<String,String>();
		
        Object result = ctx.evaluateString(scope,jsCode, name, 1, null);
        Object json = NativeJSON.stringify(ctx, scope, result, null, null);
        
        String output = Context.toString(json);
        log.debug("Output: " + output);
        
        Context.exit();
        
        ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(output, HashMap.class);

		return map;
	}
	
	private Map<String,String> getHeader(HttpServletRequest request){
		Enumeration<String> e = request.getHeaderNames();
		Map<String,String> headers = new HashMap<String, String>();
		while(e.hasMoreElements()){
			String key = e.nextElement();
			headers.put(key, request.getHeader(key));
		}
		return headers;
	}
	private ConnectorInfo getConnectorInfo(ServletContext application, String name){

		// Try to find from default
		String xmlFilePath = application.getRealPath("WEB-INF/mogaway/connector/"+name+"/"+name+".xml");
		String pathFilePath = application.getRealPath("WEB-INF/mogaway/connector/"+name+"/"+name+"-impl.js");
		
		File xmlFile = new File(xmlFilePath);
		File pathFile = new File(pathFilePath);
		
		if(xmlFile.exists() && pathFile.exists()){
			ConnectorInfo connInfo = new ConnectorInfo();
			connInfo.setXmlFilePath(xmlFile.getAbsolutePath());
			connInfo.setJsFilePath(pathFile.getAbsolutePath());
			return connInfo;
		} else {
			// Find in another director specified in properties
			// This can be comma separated
			String connectorDir = properties.getProperty(ServerPropertiesConstants.CONNECTOR_DIR);
			if(!connectorDir.equals("")){
				String[] connectorPaths = connectorDir.split(",");
				if(connectorPaths!=null){
					for(String path : connectorPaths){
						File xmlCon = new File(path + name + "/" + name + ".xml");
						File pathCon = new File(path + name + "/" + name + "-impl.js");
						if(xmlCon.exists() && pathCon.exists()){
							ConnectorInfo connInfo = new ConnectorInfo();
							connInfo.setXmlFilePath(xmlCon.getAbsolutePath());
							connInfo.setJsFilePath(pathCon.getAbsolutePath());
							return connInfo;
						}
					}
				}
			}
		}
		return null;
	}
	
	private Mogaway prepareMogawayObject(ConnectorModel connectorModel){
        Mogaway mogaway = MogawayContext.getContext().getBean(Mogaway.class);
        
        mogaway.server.setConfiguration(connectorModel.getConfiguration());
        
        return mogaway;
	}
}
