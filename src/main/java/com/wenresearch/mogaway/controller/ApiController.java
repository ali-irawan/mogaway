package com.wenresearch.mogaway.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wenresearch.mogaway.core.Mogaway;
import com.wenresearch.mogaway.core.MogawayContext;
import com.wenresearch.mogaway.core.MogawayException;
import com.wenresearch.mogaway.model.ConnectorModel;
import com.wenresearch.mogaway.model.InvokeCall;
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

		String xmlFile = application.getRealPath("WEB-INF/mogaway/connector/"+name+"/"+name+".xml");
		String pathFile = application.getRealPath("WEB-INF/mogaway/connector/"+name+"/"+name+"-impl.js");

		// Read xml and configure it as ConnectorModel
		// TODO
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
	
	private Mogaway prepareMogawayObject(ConnectorModel connectorModel){
        Mogaway mogaway = MogawayContext.getContext().getBean(Mogaway.class);
        
        mogaway.server.setConfiguration(connectorModel.getConfiguration());
        
        return mogaway;
	}
}
