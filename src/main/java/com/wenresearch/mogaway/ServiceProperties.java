package com.wenresearch.mogaway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ConfigurationProperties(prefix = "service", ignoreUnknownFields = false)
@ManagedResource
public class ServiceProperties {
	
	@Value("${mogaway.skip.default}")
	private String skipDefault;
	
	@Value("${mogaway.connector.dir}")
	private String connectorDir;
	
	@Value("${mogaway.archive.dir}")
	private String archiveDir;
	
	@Value("${mogaway.temp.dir}")
	private String tempDir;

	@ManagedAttribute
	public String getConnectorDir() {
		return this.connectorDir;
	}

	@ManagedAttribute
	public void setConnectorDir(String connectorDir) {
		this.connectorDir = connectorDir;
	}

	@ManagedAttribute
	public String getSkipDefault() {
		return skipDefault;
	}

	@ManagedAttribute
	public void setSkipDefault(String skipDefault) {
		this.skipDefault = skipDefault;
	}

	@ManagedAttribute
	public String getArchiveDir() {
		return archiveDir;
	}

	@ManagedAttribute
	public void setArchiveDir(String archiveDir) {
		this.archiveDir = archiveDir;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

}
