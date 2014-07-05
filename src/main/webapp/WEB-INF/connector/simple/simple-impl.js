function simpleProc(param1, param2){
	
	return {
	   status: "OK",
	   payload: {
		   test: param1,
	   	   data: param2
	   }
	};
}

function testHttp(){
	var request = {
		url: "mogaway/test/index"	
	};
	return Mogaway.getServer().callHttp(request);
}