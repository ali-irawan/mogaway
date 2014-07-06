function testHttpGet(){
	
	Log.debug("testHttpGet");
	
	var request = {
		url: "mogaway/test/index"	
	};
	return Mogaway.server.callHttp(request);
}

function testHttpPost(){
	
	Log.debug("testHttpPost");
	
	var request = {
		url: "mogaway/test/index",
		method: "POST",
		data: {
			user: "user123",
			code: "code123"
		}
	};
	return Mogaway.server.callHttp(request);
}

function testHttpPostJson(){
	
	Log.debug("testHttpPostJson");
	
	var request = {
		url: "mogaway/test/index/json",
		method: "POST",
		contentType: "application/json",
		data: {
			user: "user123",
			code: "code123"
		}
	};
	return Mogaway.server.callHttp(request);
}