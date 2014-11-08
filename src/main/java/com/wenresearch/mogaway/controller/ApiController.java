package com.wenresearch.mogaway.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wenresearch.mogaway.ServiceProperties;
import com.wenresearch.mogaway.core.Mogaway;
import com.wenresearch.mogaway.core.MogawayContants;
import com.wenresearch.mogaway.core.MogawayException;
import com.wenresearch.mogaway.model.ConnectorInfo;
import com.wenresearch.mogaway.model.ConnectorModel;
import com.wenresearch.mogaway.model.InvokeCall;
import com.wenresearch.mogaway.util.ConnectorHelper;
import com.wenresearch.mogaway.util.EncodeUtil;
import com.wenresearch.mogaway.util.Util;

/**
 * Standard API controller
 * 
 * This controller serve for URL <b>api/service</b>
 * 
 * @author Ali Irawan
 * @version 1.0
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("api")
public class ApiController {

	private static final Logger log = LoggerFactory.getLogger(ApiController.class);

	@Autowired
	private ServiceProperties properties;
	
	@Autowired
	private ConnectorHelper connHelper;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/service")
	@ResponseBody
	public Object executeService(HttpServletRequest request,
			HttpServletResponse response) throws IOException, MogawayException {
		log.info("Execute service");

		// Check if encrypt or not
		boolean useEncryption = false;
		String format = request.getHeader(MogawayContants.HTTP_HEADER_FORMAT);
		if(format!=null && format.equals(MogawayContants.HTTP_HEADER_FORMAT_VALUE_ENCRYPT)){
			useEncryption = true;
		}

		log.debug("Use encryption: " +useEncryption);
		InvokeCall invokeData = Util.parseJsonCall(request.getInputStream(), useEncryption, (byte) 123456);

		log.debug("query: " + invokeData.toJson());

		
		
		StringWriter jsonString = new StringWriter();

		// Run specified name
		Map mapObject = run(request, invokeData);
		
		// Convert map to JSON
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(jsonString, mapObject);
		
		String result = jsonString.toString();
		// log.debug("Result: " + result);
		if(useEncryption){
			response.setContentType("mogaway/data");
			return EncodeUtil.toBase64(EncodeUtil.encrypt_decrypt(EncodeUtil.gzip(result.getBytes()), 123456));
		}
		response.setContentType("application/json");
		return mapObject;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map run(HttpServletRequest request, InvokeCall invokeData)
			throws MogawayException, JsonParseException, JsonMappingException, IOException {

		
		String name = invokeData.getName();
		String procedure = invokeData.getProcedure();
		
		ServletContext application = request.getServletContext();

		ConnectorInfo connInfo = connHelper.getConnectorInfo(application, name);
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
		
		log.debug("Request: " + request);
		
		// Inject some object into context
        Mogaway.prepareMogawayObject(scope, request, connectorModel);
        
		// Execute the script
		Map<String,String> map = new HashMap<String,String>();
		
        Object result = ctx.evaluateString(scope,jsCode, name, 1, null);
        Object json = NativeJSON.stringify(ctx, scope, result, null, null);
        
        String output = Context.toString(json);
        // log.debug("Output: " + output);
        
        Context.exit();
        
        ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(output, HashMap.class);

		return map;
	}
	

	
	
}
