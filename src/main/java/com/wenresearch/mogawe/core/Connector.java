package com.wenresearch.mogawe.core;

import com.google.gson.JsonObject;

public interface Connector {

	public JsonObject execute(String name, String procedure, String[] parameters);
	
}
