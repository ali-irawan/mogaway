package com.wenresearch.mogaway.model;

import java.util.List;

public class ConnectorModel {

	private String type;
	private Configuration configuration;
	private List<Procedure> procedures;

	public ConnectorModel(String xmlString){
		// TODO parse xml string
		
	}
	
	public class Configuration {
		private String host;
		private int port;
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		
	}
	
	public class Procedure {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public List<Procedure> getProcedures() {
		return procedures;
	}

	public void setProcedures(List<Procedure> procedures) {
		this.procedures = procedures;
	}
}
