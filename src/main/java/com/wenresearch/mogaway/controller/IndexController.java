package com.wenresearch.mogaway.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@EnableAutoConfiguration
public class IndexController implements ErrorController {

	private static final String PATH = "/error";
	
	@RequestMapping("/")
	public ModelAndView index(){
		return new ModelAndView("welcome");
	}

	@RequestMapping(value = PATH)
	@ExceptionHandler(Exception.class)
    public String error(Exception exception) {
        return "Error: " + exception.getMessage();
    }
	
	@Override
	public String getErrorPath() {
		return PATH;
	}
}
