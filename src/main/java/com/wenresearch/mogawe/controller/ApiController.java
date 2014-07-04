package com.wenresearch.mogawe.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wenresearch.mogawe.core.MogaweException;
import com.wenresearch.mogawe.model.InvokeData;
import com.wenresearch.mogawe.util.Util;

/**
 * Standard API controller
 * 
 * This controller serve for URL <b>api/service</b>
 *  
 * @author Ali Irawan
 * @version 1.0
 */
@SuppressWarnings("restriction")
@Controller
@RequestMapping("api")
public class ApiController {
	
	private final static Logger log = LoggerFactory.getLogger(ApiController.class);
	
	private static ScriptEngineManager factory = new ScriptEngineManager();
	private static ScriptEngine engine = factory.getEngineByName("JavaScript");
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/service")
	@ResponseBody
	public Map executeService(HttpServletRequest request, HttpServletResponse response) throws IOException, MogaweException{
		log.info("Execute service");
		
		Map result = new HashMap();
		
		InvokeData invokeData = Util.parseJsonBody(request.getInputStream());
		
		log.debug("query: " +invokeData.toJson());
		
		// Run specified name
		
		
		return run(request, invokeData);
	}
	
	private Map run(HttpServletRequest request, InvokeData invokeData) throws MogaweException{
		
		String name = invokeData.getName();
		ServletContext application = request.getServletContext();
		
		String rhinoPath = application.getRealPath("WEB-INF/rhino/js.jar");
		String pathFile = application.getRealPath("WEB-INF/connector/simple/simple.js");

		String[] commands = {"java","-jar", rhinoPath, pathFile};
		try {
			Process process = Runtime.getRuntime().exec(commands);
			InputStream in = process.getInputStream();
			String output = Util.read(in);
			
			log.debug("Output: " + output);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new MogaweException(e.getMessage());
		}
		
		return null;
	}
}
