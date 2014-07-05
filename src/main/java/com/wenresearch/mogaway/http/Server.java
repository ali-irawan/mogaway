package com.wenresearch.mogaway.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.wenresearch.mogaway.core.NullCallable;
import com.wenresearch.mogaway.util.JsonParserUtil;
import com.wenresearch.mogaway.util.Util;

@Component
@Scope("prototype")
public class Server {
	private final static Logger log = LoggerFactory.getLogger(Server.class);
	
	private String protocol; // http or https
	private String host;
	private int port;
	
	private void validateCallHttpJsonRequest(JsonObject jsonObject){
		
	}
	public Object callHttp(Object json) throws MalformedURLException, IOException {
		Context ctx = Context.enter();
		Scriptable scope = ctx.initStandardObjects();
		
		log.debug("callHttp() invoked");
		log.debug("JsonObject: " + json.toString());
		log.debug("Class: " + json.getClass().getName());
		
		Object jsonString = NativeJSON.stringify(ctx, scope, json, null, null);
		JsonObject jsonObject = (JsonObject) JsonParserUtil.parse(jsonString.toString());

		Context.exit();
		
		// Validate call Http
		validateCallHttpJsonRequest(jsonObject);
		
		String prefix = this.protocol + "://" + this.host + ":" + this.port + "/";
		String url = prefix + jsonObject.get("url").getAsString();
		String method = (jsonObject.has("method")) ? jsonObject.get("method").getAsString() : "GET";

		HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
		httpConnection.setRequestMethod(method);
		
		InputStream in = httpConnection.getInputStream();
		String response = Util.read(in);
		Object result = NativeJSON.parse(ctx, scope, response, new NullCallable());
		return result;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
