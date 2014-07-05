package com.wenresearch.mogaway.core;

import com.google.gson.JsonObject;

public interface Connector {

	public JsonObject execute(String name, String procedure, String[] parameters);
	
}
