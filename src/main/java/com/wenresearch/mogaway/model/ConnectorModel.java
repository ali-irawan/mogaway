package com.wenresearch.mogaway.model;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConnectorModel {

	private final static Logger log = LoggerFactory.getLogger(ConnectorModel.class);
	
	private String type;
	private Map<String,String> configuration;
	private List<Procedure> procedures;

	public ConnectorModel(String xmlString) throws Exception{
		// TODO parse xml string
		log.debug("Parse xml: " + xmlString);

		configuration = new HashMap<String, String>();
		procedures = new ArrayList<ConnectorModel.Procedure>();
		
		loadConnectorModelFromXml(xmlString);
		
	}
	
	private void loadConnectorModelFromXml(String xmlString) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(xmlString.getBytes());
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder =  builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(is);
        
		XPath xPath =  XPathFactory.newInstance().newXPath();
		
		// Read connector type
		String expression = "/connector/type";
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		
		this.type = nodeList.item(0).getTextContent();
		log.debug("Connector type: " + this.type);
		
		expression = "/connector/configuration/*";
		nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		
		// Read configuration
		log.debug("Configuration found: " + nodeList.getLength());
		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			
			configuration.put(node.getNodeName(), node.getTextContent());
		}
		
		// Read procedure list
		expression = "/connector/procedures/*";
		nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		
		log.debug("Procedure found: " + nodeList.getLength());
		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			String procedureName = node.getAttributes().getNamedItem("name").toString();
			log.debug(" procedure -> " + procedureName);
			procedures.add(new Procedure(procedureName));
		}
	}
	public Map<String, String> getConfiguration() {
		return configuration;
	}

	public class Procedure {
		private String name;

		public Procedure(String name){
			this.name = name;
		}
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

	public List<Procedure> getProcedures() {
		return procedures;
	}

	public void setProcedures(List<Procedure> procedures) {
		this.procedures = procedures;
	}
}
