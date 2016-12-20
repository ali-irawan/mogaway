package com.wenresearch.mogaway.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.wenresearch.mogaway.ServiceProperties;
import com.wenresearch.mogaway.model.Document;
import com.wenresearch.mogaway.model.DocumentMetadata;
import com.wenresearch.mogaway.service.ArchiveService;

public class ArchiveServiceClient implements ArchiveService {
	private static final Logger log = LoggerFactory
			.getLogger(ArchiveServiceClient.class);

	String protocol = "http";
	String hostname = "localhost";
	Integer port = 50000;
	String baseUrl = "archive";

	@Autowired
	private ServiceProperties serviceProperties;
	
	private RestTemplate restTemplate;

	@Override
	public DocumentMetadata save(Document document) {
		try {
			return doSave(document);
		} catch (RuntimeException e) {
			log.error("Error while uploading file", e);
			throw e;
		} catch (IOException e) {
			log.error("Error while uploading file", e);
			throw new RuntimeException("Error while uploading file", e);
		}
	}

	private DocumentMetadata doSave(Document document) throws IOException,
			FileNotFoundException {
		String tempFilePath = writeDocumentToTempFile(document);
		MultiValueMap<String, Object> parts = createMultipartFileParam(tempFilePath);
		String dateString = DocumentMetadata.DATE_FORMAT.format(document
				.getDocumentDate());
		DocumentMetadata documentMetadata = getRestTemplate().postForObject(
				getServiceUrl() + "/upload?person={name}&date={date}", parts,
				DocumentMetadata.class, document.getPersonName(), dateString);
		return documentMetadata;
	}

	private String writeDocumentToTempFile(Document document)
			throws IOException, FileNotFoundException {
		//Path path;
		//path = Files.createTempDirectory(document.getUuid());
		String tempDirPath = serviceProperties.getTempDir();
		File file = new File(tempDirPath, document.getFileName());
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(document.getFileData());
		fo.close();
		return file.getPath();
	}

	 public String getServiceUrl() {
	        StringBuilder sb = new StringBuilder();
	        sb.append(getProtocol()).append("://");
	        sb.append(getHostname());
	        if(getPort()!=null) {
	            sb.append(":").append(getPort());
	        }
	        sb.append("/").append(getBaseUrl()).append("/");
	        return sb.toString();
	    }
	 
	private MultiValueMap<String, Object> createMultipartFileParam(
			String tempFilePath) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource(tempFilePath));
		return parts;
	}

	public RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			restTemplate = createRestTemplate();
		}
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private RestTemplate createRestTemplate() {
		restTemplate = new RestTemplate();
		return restTemplate;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
