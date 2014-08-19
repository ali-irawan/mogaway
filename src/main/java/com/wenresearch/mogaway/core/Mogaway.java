package com.wenresearch.mogaway.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.wenresearch.mogaway.controller.ApiController;
import com.wenresearch.mogaway.http.Server;
import com.wenresearch.mogaway.model.ConnectorModel;

@Component
@Scope("prototype")
public class Mogaway {

	private final static Logger log = LoggerFactory.getLogger(ApiController.class);
	
	@Autowired
	public Server server;


	public static void prepareMogawayObject(ScriptableObject scope, HttpServletRequest request, ConnectorModel connectorModel){
        Mogaway mogaway = MogawayContext.getContext().getBean(Mogaway.class);
        
        mogaway.server.setConfiguration(connectorModel.getConfiguration());
        mogaway.server.setRequest(request);
        
        log.debug("Server: " + mogaway.server);
        
        scope.put("Mogaway", scope, mogaway);
        scope.put("Log", scope, log);
        scope.put("Headers", scope, getHeader(request));
        scope.put("Session", scope, request.getSession());
	}
	
	private static Map<String,String> getHeader(HttpServletRequest request){
		Enumeration<String> e = request.getHeaderNames();
		Map<String,String> headers = new HashMap<String, String>();
		while(e.hasMoreElements()){
			String key = e.nextElement();
			headers.put(key, request.getHeader(key));
		}
		return headers;
	}
}
