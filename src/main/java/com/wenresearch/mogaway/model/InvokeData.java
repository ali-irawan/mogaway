package com.wenresearch.mogaway.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class InvokeData {

	private String name;
	private String procedure;
	private Object[] parameters;
	
	public InvokeData(String jsonString){
		JsonParser parser = new JsonParser();
		JsonElement el = (JsonElement) parser.parse(jsonString);
		if(el instanceof JsonObject){
			JsonObject json = (JsonObject) el;
			this.name = json.get(InvokeConstants.NAME).getAsString();
			this.procedure = json.get(InvokeConstants.PROC).getAsString();
			
			JsonArray arr = json.get(InvokeConstants.PARAMS).getAsJsonArray();
			if(arr!=null){
				this.parameters = new Object[arr.size()];
				for(int i=0;i<arr.size();i++){
					this.parameters[i] = arr.get(i);
				}
			}
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
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public String toJson(){
		return new Gson().toJson(this);
	}
}
