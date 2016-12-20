package com.wenresearch.mogaway.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wenresearch.mogaway.core.MogawayException;

@Controller
public class FileUploadController {
	private static final Logger log = LoggerFactory
			.getLogger(FileUploadController.class);
	
	@Value("${mogaway.upload.dir}")
	private String uploadDir;
		
//	for test only	
//    @RequestMapping(value="/upload", method=RequestMethod.GET)
//    public ModelAndView provideUploadInfo() {
//        return new ModelAndView("simple-upload");
//    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(HttpServletRequest request, 
    		HttpServletResponse response, 
    		@RequestParam("file") MultipartFile file) throws Exception{
    	
    	String result = "";
    	
    	 try {
         	if(uploadDir != null && !uploadDir.isEmpty()){
         	} else {
				throw new MogawayException("Upload dir not configured");
			}

	        if (file.isEmpty()) {
	        	throw new MogawayException("Upload File was empty");       	                
	        }
        
	        File uploadDirFile = new File(uploadDir);
	        if(!uploadDirFile.exists()){
	        	uploadDirFile.mkdirs();
	        }
	        
	        // random 
	        String fileName = generateFileName(file.getOriginalFilename());
	        String filePath = uploadDirFile.getAbsolutePath().concat(File.separator).concat(fileName);
	        File uploadedFile = new File(filePath);
	        
	        byte[] bytes = file.getBytes();
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(uploadedFile));
            stream.write(bytes);
            stream.close();

			result =  uploadedFile.getAbsolutePath();
	    } catch (Exception e) {
	    	log.error("Error upload",e);
	    	response.setHeader("errorMessage", e.getMessage());
	    	response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    }
    	 
    	return result;
    }
    
    private String generateFileName(String originName){
    	
    	// get extension
    	String ext = FilenameUtils.getExtension(originName);
    	
    	String fileName = UUID.randomUUID().toString();
    	
    	return fileName.concat(ext.isEmpty()?"":".").concat(ext);
    }
    
}