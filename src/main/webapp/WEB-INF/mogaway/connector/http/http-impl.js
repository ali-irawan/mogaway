function testHttpGet(){
	
	Log.debug("testHttpGet");
	
	var request = {
		url: "test/index"	
	};
	return Mogaway.server.callHttp(request);
}

function testHttpPost(){
	
	Log.debug("testHttpPost");
	
	var request = {
		url: "test/index",
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
		url: "test/index/json",
		method: "POST",
		contentType: "application/json",
		data: {
			user: "user123",
			code: "code123"
		}
	};
	return Mogaway.server.callHttp(request);
}

function testHttpPostJsonWithHeader(){
	Log.debug("testHttpPostJsonWithHeader");
	
	var request = {
		url: "test/index/json",
		method: "POST",
		contentType: "application/json",
		data: {
			user: "user123",
			code: "code123"
		}
	};
	return Mogaway.server.callHttp(request, {
		header: {
			'testing': 15600,
		}
	});
}