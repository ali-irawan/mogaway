package com.wenresearch.mogaway.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonParserUtil {

	private static JsonParser parser = new JsonParser();
	
	public static JsonElement parse(String jsonString){
		return parser.parse(jsonString);
	}
}
