package com.wenresearch.mogaway.core;

import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class ServerProperties extends Properties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7774755865143646535L;

	public ServerProperties() throws IOException{
		load(getClass().getClassLoader().getResourceAsStream("config.properties"));
	}
}
