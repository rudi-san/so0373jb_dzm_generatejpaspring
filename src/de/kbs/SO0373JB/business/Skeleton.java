package de.kbs.SO0373JB.business;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.kbs.SO0373JB.common.constants.Constants;
import de.kbs.so1320jc.main.LoggingContainer;

public class Skeleton {

	private HashMap<String, String>	skelList	= new HashMap<String, String>();
	
	private static Skeleton			skeletons	= null;
	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();
	

	private Skeleton () {
		logger.info					("Laden der Skeletons");

		try {
			DocumentBuilderFactory docBuilderFactory 	= DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder 					= docBuilderFactory.newDocumentBuilder();
			File file									= new File(Constants.XML_SKELFILE);	
			Document doc 								= docBuilder.parse(file);

			doc.getDocumentElement().normalize			();

			NodeList listOfRows 						= doc.getElementsByTagName(Constants.XML_SKELS);
			int totalSkels 								= listOfRows.getLength();
			logger.debug								("Anzahl Skeletons : " + totalSkels);

			for (int s = 0; s < totalSkels; s++) {

				Element node 			= (Element)listOfRows.item(s);
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					String skelName			= node.getAttribute(Constants.XML_SKELNAME);
					String skelText			= node.getTextContent().trim();
					skelList.put			(skelName, skelText);
				}
			}
		} catch (SAXParseException e) {
			logger.fatal						("** Parsing error" + ", line "	+ e.getLineNumber() + ", uri " + e.getSystemId());
			logger.fatal						(" " + e.getMessage());
		} catch (SAXException e) {
			logger.fatal 						(e.getMessage());
		} catch (ParserConfigurationException e) {
			logger.fatal 						(e.getMessage());
		} catch (IOException e) {
			logger.fatal 						(e.getMessage());
		} 
	}
	
	public static String getSkeleton (String skelName) {
		if (skeletons==null) {
			skeletons 		= new Skeleton();
		}
		return 		skeletons.skelList.get(skelName);
	}
	
	public static void main (String[] args) {
		System.out.println(getSkeleton("SKEL001"));
	}
}
