mogaway
======

Lightweight Mobile Gateway

mogaway is lightweight server that will serve mobile clients using REST service

Basic Feature
======
- Lightweight web server for handling mobile request
- Connector to back-end web service
- Upload File
- Connector to database (available soon)

Getting Started
======

Clone the source

```
git clone https://github.com/ali-irawan/mogaway.git
```

Run

```
mvn package spring-boot:run 
```

Build JAR Standalone

```
mvn package 
```

Open in browser

```
http://localhost:50000
```

Send POST request to

```
http://localhost:50000/api/service
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

Upload File
======

Do http POST form multipart to 
```
http://localhost:50000/upload
```

**Success Response** 
```
Path file stored in server

/home/user/magaway-upload/abcabc-abcbabc-abcabba-abcbabc-acbabcb.txt

```

**Failed Response**
```
HTTP Error Code 500

To get error get header properties with key errorMessage
```

**Configuration**
Set configuration related to upload servlet here **src/main/resources/application.properties**
```
# Target Upload directory 
mogaway.upload.dir=/home/sts/mogaway-upload/

# Max file size permit to upload
multipart.maxFileSize=10MB
```

Using actuator
http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready