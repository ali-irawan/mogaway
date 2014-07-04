package com.wenresearch.mogawe.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("test")
public class TestController {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/index")
	@ResponseBody
	public Map index(){
		Map map = new HashMap<String, Object>();
		
		map.put("status", "OK");
		map.put("message", "This is for testing");
		
		return map;
	}
}
