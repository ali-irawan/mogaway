package com.wenresearch.mogaway.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class ImageController {

	@Value("${mogaway.upload.dir}")
	private String uploadDir;
	
	// GET /images
	@RequestMapping(value="/images/{fileName:.+}", method=RequestMethod.GET)
	public void showImage(
			 @PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException{
		
		response.setContentType("image/jpg");
		
		ServletOutputStream output = response.getOutputStream();
		
		File imageFile = new File(uploadDir + File.separator + fileName);
		FileInputStream fos = new FileInputStream(imageFile);
		
		byte[] bytes = new byte[1024];
		int read;
		while ( (read = fos.read(bytes,0, 1024)) != -1 ) {
			
			output.write(bytes, 0, read);
		}
		
		fos.close();
	}
}
