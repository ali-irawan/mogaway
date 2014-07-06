package com.wenresearch.mogaway.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wenresearch.mogaway.core.MogawayContants;
import com.wenresearch.mogaway.core.NullCallable;
import com.wenresearch.mogaway.util.JsonParserUtil;
import com.wenresearch.mogaway.util.Util;

/**
 * Class for server side code. This will be mapped into Mogaway.server in
 * Javascript
 * 
 * @author Ali Irawan
 * 
 */
@Component
@Scope("prototype")
public class Server {
	private final static Logger log = LoggerFactory.getLogger(Server.class);

	private Map<String, String> configuration;

	public Server() {
		configuration = new HashMap<String, String>();
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	private void validateCallHttpJsonRequest(JsonObject jsonObject) {

	}

	public Object callHttp(Object json) throws MalformedURLException,
			IOException {
		Context ctx = Context.enter();
		Scriptable scope = ctx.initStandardObjects();

		log.debug("callHttp() invoked");
		log.debug("JsonObject: " + json.toString());
		log.debug("Class: " + json.getClass().getName());

		Object jsonString = NativeJSON.stringify(ctx, scope, json, null, null);
		JsonObject jsonObject = (JsonObject) JsonParserUtil.parse(jsonString
				.toString());

		Context.exit();

		// Validate call Http
		validateCallHttpJsonRequest(jsonObject);

		String protocol = configuration.get("protocol");
		String host = configuration.get("host");
		String port = configuration.get("port");
		String prefix = protocol + "://" + host + ":" + port + "/";
		
		String url = prefix
				+ jsonObject.get(MogawayContants.PARAM_URL).getAsString();
		String method = (jsonObject.has(MogawayContants.PARAM_METHOD)) ? jsonObject
				.get(MogawayContants.PARAM_METHOD).getAsString() : "GET";

		HttpURLConnection httpConnection = (HttpURLConnection) new URL(url)
				.openConnection();
		httpConnection.setRequestMethod(method);

		if (MogawayContants.REQUEST_METHOD_POST.equals(method)) {
			StringBuilder postPayload = new StringBuilder();

			// Setting contentType
			if (jsonObject.has(MogawayContants.PARAM_CONTENT_TYPE)) {

				httpConnection.setRequestProperty("Content-Type", jsonObject
						.get(MogawayContants.PARAM_CONTENT_TYPE).getAsString());
				if (jsonObject.has(MogawayContants.PARAM_DATA)) {
					JsonObject data = jsonObject
							.get(MogawayContants.PARAM_DATA).getAsJsonObject();
					postPayload.append(data.toString());
				} else {
					JsonObject data = new JsonObject();
					postPayload.append(data.toString());
				}
			} else {
				httpConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				if (jsonObject.has(MogawayContants.PARAM_DATA)) {
					JsonObject data = jsonObject
							.get(MogawayContants.PARAM_DATA).getAsJsonObject();

					log.debug("param data: " + data);

					Set<Entry<String, JsonElement>> set = data.entrySet();
					Iterator<Entry<String, JsonElement>> it = set.iterator();

					// Populate postPayload
					while (it.hasNext()) {
						Entry<String, JsonElement> entry = it.next();
						String key = entry.getKey();
						JsonElement el = entry.getValue();
						postPayload.append(key);
						postPayload.append("=");
						postPayload.append(el.getAsString());
						if (it.hasNext())
							postPayload.append("&");
					}
				}
			}

			String postPayloadString = postPayload.toString();
			log.debug("Post payload: " + postPayloadString);

			httpConnection.setDoOutput(true);

			// Write the bytes
			OutputStream out = httpConnection.getOutputStream();
			out.write(postPayloadString.getBytes());
			out.flush();
		}

		InputStream in = httpConnection.getInputStream();
		String response = Util.read(in);
		Object result = NativeJSON.parse(ctx, scope, response,
				new NullCallable());
		return result;
	}

}
