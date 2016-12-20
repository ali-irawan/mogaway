package com.wenresearch.mogaway.util;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wenresearch.mogaway.ServiceProperties;
import com.wenresearch.mogaway.model.ConnectorInfo;

@Component
public class ConnectorHelper {

	@Autowired
	private ServiceProperties properties;

	public ConnectorInfo getConnectorInfo(ServletContext application,
			String name) {

		String skipDefault = properties.getSkipDefault();

		if(skipDefault.equals("false")) {
			// Try to find from default
			String xmlFilePath = application.getRealPath("WEB-INF/mogaway/connector/" + name + "/" + name + ".xml");
			String pathFilePath = application.getRealPath("WEB-INF/mogaway/connector/" + name + "/" + name	+ "-impl.js");
	
			File xmlFile = new File(xmlFilePath);
			File pathFile = new File(pathFilePath);
	
			if (xmlFile.exists() && pathFile.exists()) {
				ConnectorInfo connInfo = new ConnectorInfo();
				connInfo.setXmlFilePath(xmlFile.getAbsolutePath());
				connInfo.setJsFilePath(pathFile.getAbsolutePath());
				return connInfo;
			} else {
				return null;
			}
		}
		
		// Find in another director specified in properties
		// This can be comma separated
		String connectorDir = properties.getConnectorDir();

		System.out.println("ConnectorDir: " + connectorDir);

		if (!connectorDir.equals("")) {
			String[] connectorPaths = connectorDir.split(",");
			if (connectorPaths != null) {
				for (String path : connectorPaths) {
					File xmlCon = new File(path + name + "/" + name + ".xml");
					File pathCon = new File(path + name + "/" + name
							+ "-impl.js");

					System.out.println("xml: " + xmlCon.getAbsolutePath());

					if (xmlCon.exists() && pathCon.exists()) {
						ConnectorInfo connInfo = new ConnectorInfo();
						connInfo.setXmlFilePath(xmlCon.getAbsolutePath());
						connInfo.setJsFilePath(pathCon.getAbsolutePath());
						return connInfo;
					}
				}
			}
		}

		return null;
	}
}
