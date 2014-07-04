package com.wenresearch.mogawe.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class InvokeData {

	private String name;
	private String procedure;
	private JsonArray parameters;
	
	public InvokeData(String jsonString){
		JsonParser parser = new JsonParser();
		JsonElement el = (JsonElement) parser.parse(jsonString);
		if(el instanceof JsonObject){
			JsonObject json = (JsonObject) el;
			this.name = json.get(InvokeConstants.NAME).getAsString();
			this.procedure = json.get(InvokeConstants.PROC).getAsString();
			this.parameters = json.get(InvokeConstants.PARAMS).getAsJsonArray();
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProcedure() {
		return procedure;
	}
	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}
	public JsonArray getParameters() {
		return parameters;
	}
	public void setParameters(JsonArray parameters) {
		this.parameters = parameters;
	}
	public String toJson(){
		return new Gson().toJson(this);
	}
}
