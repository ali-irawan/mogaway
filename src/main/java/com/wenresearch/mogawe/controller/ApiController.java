package com.wenresearch.mogawe.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wenresearch.mogawe.core.MogawayException;
import com.wenresearch.mogawe.model.InvokeData;
import com.wenresearch.mogawe.util.Util;

/**
 * Standard API controller
 * 
 * This controller serve for URL <b>api/service</b>
 * 
 * @author Ali Irawan
 * @version 1.0
 */
@Controller
@RequestMapping("api")
public class ApiController {

	private final static Logger log = LoggerFactory
			.getLogger(ApiController.class);

	@SuppressWarnings("rawtypes")
	@RequestMapping("/service")
	@ResponseBody
	public Map executeService(HttpServletRequest request,
			HttpServletResponse response) throws IOException, MogawayException {
		log.info("Execute service");

		InvokeData invokeData = Util.parseJsonBody(request.getInputStream());

		log.debug("query: " + invokeData.toJson());

		// Run specified name

		return run(request, invokeData);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map run(HttpServletRequest request, InvokeData invokeData)
			throws MogawayException {

		String name = invokeData.getName();
		ServletContext application = request.getServletContext();

		String rhinoPath = application.getRealPath("WEB-INF/rhino/js.jar");
		String pathFile = application
				.getRealPath("WEB-INF/connector/"+name+"/"+name+"-impl.js");


		Map<String,String> map = new HashMap<String,String>();

		String[] commands = { "java", "-jar", rhinoPath };
		InputStream in = null;
		OutputStream out = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(commands);
			in = process.getInputStream();
			out = process.getOutputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String args = StringUtils.arrayToCommaDelimitedString(invokeData.getParameters());
			 
			PrintWriter pw = new PrintWriter(out);
			pw.println("load(\"" + pathFile + "\")");
			pw.print("print(JSON.stringify("); 
			pw.print(invokeData.getProcedure());
			pw.print("(");
			pw.print(args);
			pw.print(")))");
			
			pw.close();
			out.close();

			String output = null;
			output = br.readLine();

			log.debug("> " + output);

			// Convert output to Map

			ObjectMapper mapper = new ObjectMapper();

			map = mapper.readValue(output, HashMap.class);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new MogawayException(e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			process.destroy();
		}

		return map;
	}
}
