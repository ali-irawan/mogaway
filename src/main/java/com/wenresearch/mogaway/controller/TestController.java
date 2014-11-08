package com.wenresearch.mogaway.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wenresearch.mogaway.util.Util;

@Controller
@EnableAutoConfiguration
@RequestMapping("test")
public class TestController {

	private final static Logger log = LoggerFactory.getLogger(TestController.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/index",method=RequestMethod.GET)
	@ResponseBody
	public Map index(){
		Map map = new HashMap<String, Object>();
		
		map.put("status", "OK");
		map.put("message", "This is for testing GET");
		
		return map;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/index",method=RequestMethod.POST)
	@ResponseBody
	public Map post(HttpServletRequest request){
		
		Map map = new HashMap<String, Object>();
		
		map.put("status", "OK");
		map.put("message", "This is for testing POST");
		
		Map payload = new HashMap<String, Object>();
		Enumeration<String> e = request.getParameterNames();
		while(e.hasMoreElements()){
			String name = e.nextElement();
			payload.put(name, request.getParameter(name));
		}
		map.put("payload", payload);
		
		return map;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/index/json",method=RequestMethod.POST)
	@ResponseBody
	public Map postJson(HttpServletRequest request) throws IOException{
		String json = Util.read(request.getInputStream());
		log.debug("JSON: " + json);
	
		Map map = new HashMap<String, Object>();
		
		map.put("status", "OK");
		map.put("json accepted", json.toString());
		
		return map;
	}
}
