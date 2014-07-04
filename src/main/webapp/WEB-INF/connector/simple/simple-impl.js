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
	
	return com.wenresearch.mogawe.http.Server.invokeHttp();
}