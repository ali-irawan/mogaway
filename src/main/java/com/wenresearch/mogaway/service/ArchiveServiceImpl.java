package com.wenresearch.mogaway.service;


import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wenresearch.mogaway.ServiceProperties;
import com.wenresearch.mogaway.model.Document;
import com.wenresearch.mogaway.model.DocumentMetadata;

@Service
public class ArchiveServiceImpl implements ArchiveService {

	@Autowired
	private ServiceProperties serviceProperties;
	
	public DocumentMetadata save (Document document){
		String archiveDir = serviceProperties.getArchiveDir();
		String fileName = document.getFileName();
		
		File file = new File(archiveDir);
		if(!file.exists()){
			file.mkdirs();
		}
		
		String destinationFilePath = archiveDir + fileName;
		
		return document.getMetadata();
	}
}
