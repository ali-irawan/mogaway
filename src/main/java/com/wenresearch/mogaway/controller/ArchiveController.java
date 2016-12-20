package com.wenresearch.mogaway.controller;

import java.sql.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wenresearch.mogaway.model.Document;
import com.wenresearch.mogaway.model.DocumentMetadata;
import com.wenresearch.mogaway.service.ArchiveService;

@Controller
@RequestMapping(value = "/archive")
public class ArchiveController {
	private final static Logger log = LoggerFactory.getLogger(ArchiveController.class);
	
	@Autowired
	private ArchiveService archiveService;
	
    /**
     * Adds a document to the archive.
     * 
     * Url: /archive/upload?file={file}&person={person}&date={date} [POST]
     * 
     * @param file A file posted in a multipart request
     * @param person The name of the uploading person
     * @param date The date of the document
     * @return The meta data of the added document
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody DocumentMetadata handleFileUpload(
            @RequestParam(value="file", required=true) MultipartFile file ,
            @RequestParam(value="person", required=true) String person,
            @RequestParam(value="date", required=true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        
        try {
        	String originalFilename = file.getOriginalFilename();
        	String extension = originalFilename.substring(originalFilename.lastIndexOf('.')+1);
        	String generatedFileName = UUID.randomUUID().toString() + "." + extension;
        	
            Document document = new Document(file.getBytes(), generatedFileName, date, person );
            
            archiveService.save(document);
            return document.getMetadata();
        	
        } catch (RuntimeException e) {
            log.error("Error while uploading.", e);
            throw e;
        } catch (Exception e) {
            log.error("Error while uploading.", e);
            throw new RuntimeException(e);
        }      
    }
}
