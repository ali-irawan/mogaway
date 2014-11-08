package com.wenresearch.mogaway;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.wenresearch.mogaway.core.ServerPropertiesConstants;

@ConfigurationProperties(prefix = "service", ignoreUnknownFields = false)
@ManagedResource
public class ServiceProperties {
	
	private static final Logger log = LoggerFactory.getLogger(ServiceProperties.class);
	
	private String connectorDir = "";

	public ServiceProperties(){
		Properties properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
			this.connectorDir = properties.getProperty(ServerPropertiesConstants.CONNECTOR_DIR);
		} catch (IOException e) {
			// e.printStackTrace();
			log.warn("Could not load config.properties in classpath");
		}
	}
	@ManagedAttribute
	public String getConnectorDir() {
		return this.connectorDir;
	}

	@ManagedAttribute
	public void setConnectorDir(String connectorDir) {
		this.connectorDir = connectorDir;
	}

}
