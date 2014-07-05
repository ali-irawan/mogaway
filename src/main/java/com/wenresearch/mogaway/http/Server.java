package com.wenresearch.mogaway.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;

import com.wenresearch.mogaway.core.NullCallable;
import com.wenresearch.mogaway.util.Util;

public class Server {

	public static Object invokeHttp() throws MalformedURLException, IOException {
		String url = "http://localhost:8080/mogaway/test/index";

		HttpURLConnection httpConnection = (HttpURLConnection) new URL(url)
				.openConnection();
		httpConnection.setRequestMethod("GET");
		
		Context ctx = Context.enter();
		Scriptable scope = ctx.initStandardObjects();
		InputStream in = httpConnection.getInputStream();
		String response = Util.read(in);
		Object result = NativeJSON.parse(ctx, scope, response, new NullCallable());
		return result;
	}
}
