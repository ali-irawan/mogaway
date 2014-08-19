package com.wenresearch.mogaway.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wenresearch.mogaway.core.Mogaway;
import com.wenresearch.mogaway.core.MogawayContants;
import com.wenresearch.mogaway.core.MogawayException;
import com.wenresearch.mogaway.core.NullCallable;
import com.wenresearch.mogaway.core.ServerProperties;
import com.wenresearch.mogaway.model.ConnectorInfo;
import com.wenresearch.mogaway.model.ConnectorModel;
import com.wenresearch.mogaway.util.ConnectorHelper;
import com.wenresearch.mogaway.util.JsonParserUtil;
import com.wenresearch.mogaway.util.Util;

/**
 * Class for server side code. This will be mapped into Mogaway.server in
 * Javascript
 * 
 * @author Ali Irawan
 * 
 */
@Component
@Scope("prototype")
public class Server implements ServletContextAware {
	private final static Logger log = LoggerFactory.getLogger(Server.class);

	private Map<String, String> configuration;
	private ServletContext application;

	@Autowired
	private ServerProperties serverProperties;
	private HttpServletRequest request;
	
	@Autowired
	private ConnectorHelper connHelper;

	public Server() {
		configuration = new HashMap<String, String>();
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	private void validateCallHttpJsonRequest(JsonObject jsonObject) {

	}

	public Object callProcedure(String connectorName, String procedureName,	Object parameters) throws JsonParseException, JsonMappingException, IOException, MogawayException {
		log.debug("Params type: " + parameters.getClass().getName());
		
		ConnectorInfo connInfo = connHelper.getConnectorInfo(application, connectorName);
		if(connInfo==null){
			throw new MogawayException("Connector '" + connectorName + "' not found");
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
		NativeArray params = (NativeArray) parameters;
		
		String[] arr = new String[params.size()];
		for(int i=0;i<params.size();i++){
			arr[i] = "\"" + params.get(i).toString() + "\"";
		}
		String args = StringUtils.arrayToCommaDelimitedString(arr);
		jsCode += procedureName+"("+args+");";
 
		log.debug("***** JSCODE: " +jsCode);

		Context ctx = Context.enter();
		ScriptableObject scope = ctx.initStandardObjects();
		
		// Inject some object into context
        Mogaway.prepareMogawayObject(scope, request, connectorModel);
        
        Object result = ctx.evaluateString(scope,jsCode, connectorName, 1, null);
        Object json = NativeJSON.stringify(ctx, scope, result, null, null);
        
        String output = Context.toString(json);
        log.debug("Output: " + output);
        
        Context.exit();

        JsonObject jsonObject = (JsonObject) JsonParserUtil.parse(output);
        return jsonObject.toString();
	}

	public Object callHttp(Object json) throws MalformedURLException,
			IOException {
		Context ctx = Context.enter();
		Scriptable scope = ctx.initStandardObjects();

		log.debug("callHttp() invoked");
		log.debug("JsonObject: " + json.toString());
		log.debug("Class: " + json.getClass().getName());

		Object jsonString = NativeJSON.stringify(ctx, scope, json, null, null);
		JsonObject jsonObject = (JsonObject) JsonParserUtil.parse(jsonString
				.toString());

		Context.exit();

		// Validate call Http
		validateCallHttpJsonRequest(jsonObject);

		String protocol = configuration.get("protocol");
		String host = configuration.get("host");
		String port = configuration.get("port");
		String prefix = protocol + "://" + host + ":" + port + "/";

		String url = prefix
				+ jsonObject.get(MogawayContants.PARAM_URL).getAsString();
		String method = (jsonObject.has(MogawayContants.PARAM_METHOD)) ? jsonObject
				.get(MogawayContants.PARAM_METHOD).getAsString() : "GET";

		HttpURLConnection httpConnection = (HttpURLConnection) new URL(url)
				.openConnection();
		httpConnection.setRequestMethod(method);

		if (MogawayContants.REQUEST_METHOD_POST.equals(method)) {
			StringBuilder postPayload = new StringBuilder();

			// Setting contentType
			if (jsonObject.has(MogawayContants.PARAM_CONTENT_TYPE)) {

				httpConnection.setRequestProperty("Content-Type", jsonObject
						.get(MogawayContants.PARAM_CONTENT_TYPE).getAsString());
				if (jsonObject.has(MogawayContants.PARAM_DATA)) {
					JsonObject data = jsonObject
							.get(MogawayContants.PARAM_DATA).getAsJsonObject();
					postPayload.append(data.toString());
				} else {
					JsonObject data = new JsonObject();
					postPayload.append(data.toString());
				}
			} else {
				httpConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				if (jsonObject.has(MogawayContants.PARAM_DATA)) {
					JsonObject data = jsonObject
							.get(MogawayContants.PARAM_DATA).getAsJsonObject();

					log.debug("param data: " + data);

					Set<Entry<String, JsonElement>> set = data.entrySet();
					Iterator<Entry<String, JsonElement>> it = set.iterator();

					// Populate postPayload
					while (it.hasNext()) {
						Entry<String, JsonElement> entry = it.next();
						String key = entry.getKey();
						JsonElement el = entry.getValue();
						postPayload.append(key);
						postPayload.append("=");
						postPayload.append(el.getAsString());
						if (it.hasNext())
							postPayload.append("&");
					}
				}
			}

			String postPayloadString = postPayload.toString();
			log.debug("Post payload: " + postPayloadString);

			httpConnection.setDoOutput(true);

			// Write the bytes
			OutputStream out = httpConnection.getOutputStream();
			out.write(postPayloadString.getBytes());
			out.flush();
		}

		InputStream in = httpConnection.getInputStream();
		String response = Util.read(in);
		Object result = NativeJSON.parse(ctx, scope, response,
				new NullCallable());
		return result;
	}

	@Override
	public void setServletContext(ServletContext arg0) {
		this.application = arg0;

	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
