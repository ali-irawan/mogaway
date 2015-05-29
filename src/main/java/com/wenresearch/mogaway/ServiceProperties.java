package com.wenresearch.mogaway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ConfigurationProperties(prefix = "service", ignoreUnknownFields = false)
@ManagedResource
public class ServiceProperties {
	
	@Value("${mogaway.connector.dir}")
	private String connectorDir;

	@ManagedAttribute
	public String getConnectorDir() {
		return this.connectorDir;
	}

	@ManagedAttribute
	public void setConnectorDir(String connectorDir) {
		this.connectorDir = connectorDir;
	}

}
