package de.kbs.SO0373JB.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.kbs.SO0373JB.common.config.Configuration;

public class IvyXML {

	public static final String[][] attrRoot		= 	{ { "version", "2.0" }
													, { "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" }
													, { "xsi:noNamespaceSchemaLocation", "xsi:noNamespaceSchemaLocation" }
	};
	public static final String[][] attrInfo		= 	{ { "organisation", "de.kbs" }
	};
//	public static final String[][] attrConf		= 	{ { "name", "default" }
//	};
	public static final String[][] attrPubl		= 	{ { "type", "jar" }
//													, { "conf", "default" }
													, { "ext", "jar" }
	};
	public static final String[][] attrDep		= 	{ { "org", "de.kbs.springdatajpa" }
													, { "name", "SO0374JC" }
													, { "rev", "latest.integration" }
	};

	
	public static void createIvyXml (String xmlFilePath) {

		try {

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document document = documentBuilder.newDocument();

			// root element
			Element root = document.createElement("ivy-module");
			for  (String[] attr : attrRoot)
				root.setAttribute(attr[0], attr[1]);
			document.appendChild(root);

			// info element
			Element info = document.createElement("info");
			for  (String[] attr : attrInfo)
				info.setAttribute(attr[0], attr[1]);
			info.setAttribute("module", Configuration.getConfiguration().getModuleName());
			info.setAttribute("revision", "1.0.0");
			root.appendChild(info);

//			// configurations element
//			Element configurations = document.createElement("configurations");
//			// conf element
//			Element conf = document.createElement("conf");
//			for  (String[] attr : attrConf)
//				conf.setAttribute(attr[0], attr[1]);
//			configurations.appendChild(conf);
//			root.appendChild(configurations);

			// publications element
			Element publications = document.createElement("publications");
			// artifact element
			Element artifact = document.createElement("artifact");
			for  (String[] attr : attrPubl)
				artifact.setAttribute(attr[0], attr[1]);
			artifact.setAttribute("name", Configuration.getConfiguration().getModuleName());
			publications.appendChild(artifact);
			root.appendChild(publications);

			// dependencies element
			Element dependencies = document.createElement("dependencies");
			// conf element
			Element dependency = document.createElement("dependency");
			for  (String[] attr : attrDep)
				dependency.setAttribute(attr[0], attr[1]);
			dependencies.appendChild(dependency);
			root.appendChild(dependencies);

			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory 	= TransformerFactory.newInstance();
			Transformer transformer 				= transformerFactory.newTransformer();
			transformer.setOutputProperty			(OutputKeys.INDENT, "yes");
			DOMSource domSource 					= new DOMSource(document);
			
			StreamResult streamResult 				= new StreamResult(new File(xmlFilePath));

			// If you use
			// StreamResult result = new StreamResult(System.out);
			// the output will be pushed to the standard output ...
			// You can use that for debugging 

			transformer.transform(domSource, streamResult);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public static final String[][] attrProp		= 	{ { "name", "jpa.dir" }
													, { "value", "X:/Arbeitsgruppen/Java Batch on zOs/SoftwareSupport/ivyrepo" }
													};
	public static final String[][] attrSett		= 	{ { "defaultResolver", "local-chain" }
													};
	public static final String[][] attrFileSys	= 	{ { "name", "jpa" }
													};
	public static final String[][] attrIvy		= 	{ { "pattern", "${jpa.dir}/[module]/ivy-[revision].xml" }
													};
	public static final String[][] attrArtifact	= 	{ { "pattern", "${jpa.dir}/[module]/[artifact]-[revision].[ext]" }
	};
	public static final String[][] attrChain	= 	{ { "name", "local-chain" }
	};
	public static final String[][] attrResolver	= 	{ { "ref", "jpa" }
	};

	public static void createIvySettings (String xmlFilePath) {

		try {

			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

			Document document = documentBuilder.newDocument();

			// root element
			Element root = document.createElement("ivysettings");
			document.appendChild(root);

			// property element
			Element prop = document.createElement("property");
			for  (String[] attr : attrProp)
				prop.setAttribute(attr[0], attr[1]);
			root.appendChild(prop);

			// settings element
			Element settings = document.createElement("settings");
			for  (String[] attr : attrSett)
				settings.setAttribute(attr[0], attr[1]);
			root.appendChild(settings);
			// resolvers element
			Element resolvers = document.createElement("resolvers");
			// filesystem element
			Element filesystem = document.createElement("filesystem");
			for  (String[] attr : attrFileSys)
				filesystem.setAttribute(attr[0], attr[1]);
			// ivy element
			Element ivy = document.createElement("ivy");
			for  (String[] attr : attrIvy)
				ivy.setAttribute(attr[0], attr[1]);
			filesystem.appendChild(ivy);
			// artifact element
			Element artifact = document.createElement("artifact");
			for  (String[] attr : attrArtifact)
				artifact.setAttribute(attr[0], attr[1]);
			filesystem.appendChild(artifact);
			resolvers.appendChild(filesystem);
			// chain element
			Element chain = document.createElement("chain");
			for  (String[] attr : attrChain)
				chain.setAttribute(attr[0], attr[1]);
			// resolver element
			Element resolver = document.createElement("resolver");
			for  (String[] attr : attrResolver)
				resolver.setAttribute(attr[0], attr[1]);
			chain.appendChild(resolver);
			resolvers.appendChild(chain);
			root.appendChild(resolvers);


			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory 	= TransformerFactory.newInstance();
			Transformer transformer 				= transformerFactory.newTransformer();
			transformer.setOutputProperty			(OutputKeys.INDENT, "yes");
			DOMSource domSource 					= new DOMSource(document);
			
			StreamResult streamResult 				= new StreamResult(new File(xmlFilePath));

			// If you use
			// StreamResult result = new StreamResult(System.out);
			// the output will be pushed to the standard output ...
			// You can use that for debugging 

			transformer.transform(domSource, streamResult);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public static void main(String argv[]) {
		createIvySettings("ivysettingsneu.xml");
	}
}