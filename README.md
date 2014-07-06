mogaway
======

Lightweight Mobile Gateway

mogaway is lightweight server that will serve mobile clients using REST service

Basic Feature
======
- Lightweight web server for handling mobile request
- Connector to back-end web service
- Connector to database (available soon)

Getting Started
======

Clone the source

```
git clone https://github.com/ali-irawan/mogaway.git
```

Run

```
mvn package jetty:run 
```

Open in browser

```
http://localhost:8080/mogaway
```

Send POST request to

```
http://localhost:8080/mogaway/api/service
```

With JSON data

```javascript
{
   "name": "simple",
   "proc": "simpleProc",
   "params": [1,6]
}
```
This will call the **simple** connector with procedure **simpleProc**
The simpleProc is implemented as follow

```
function simpleProc(param1, param2){
	
	return {
	   status: "OK",
	   payload: {
		   test: param1,
	   	   data: param2
	   }
	};
}
```
The output returned will be as follow

```
{
   status: "OK",
   payload: {
      test: 1,
      data: 6
   }
}
```