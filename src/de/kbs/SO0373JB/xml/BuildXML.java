package de.kbs.SO0373JB.xml;

import java.io.File;

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

public class BuildXML {
	
	public static 		Document 	document;

	public static final String[][] attrRoot		= 	{ { "name", "JPA Klassen" }
													, { "default", "jarBuilder" }
													, { "basedir", "." }
													, { "xmlns:ivy", "antlib:org.apache.ivy.ant" }
	};
	public static final String[][] attrTarg		= 	{ { "name", "jarBuilder" }
													, { "description", "jar-Archiv für Ivy erzeugen" }
	};
	public static final String[][] attrJar		= 	{ { "jarfile", "${ivy.module}.jar" }
													, { "basedir", "." }
	};
	public static final String[][] attrPublish	= 	{ { "resolver", "jpa" }
													, { "overwrite", "true" }
	};
	public static final String[][] attrArtifacts= 	{ { "pattern", "[artifact].[ext]" }
	};

	
	public static void createBuildXml (String xmlFilePath, String packageName) {

		try {

			document 				= DocumentBuilderFactory
								  	  .newInstance()
								  	  .newDocumentBuilder()
								  	  .newDocument();

			// root element
			Element root 			= createElement("project", attrRoot);
			document.appendChild	(root);

			// property elements
			Element prop 			= document.createElement("property");
			prop.setAttribute		("file", "build.properties");
			root.appendChild		(prop);

			// configurations element
			Element target 			= createElement("target", attrTarg);
			// conf element
			Element jar 			= createElement("jar", attrJar);
			jar.setAttribute		("includes", packageName.replace(".", "/")+"/**/*.class" );
			//manifest element
			Element manifest 		= document.createElement("manifest");
			Element attribute		= document.createElement("attribute");
			attribute.setAttribute	("name", "Implementation-Title");
			attribute.setAttribute	("value", "${ivy.module}");
			manifest.appendChild	(attribute);
			attribute				= document.createElement("attribute");
			attribute.setAttribute	("name", "Implementation-Vendor");
			attribute.setAttribute	("value", "DRV KBS");
			manifest.appendChild	(attribute);
			attribute				= document.createElement("attribute");
			attribute.setAttribute	("name", "Implementation-Vendor-Id");
			attribute.setAttribute	("value", "de.kbs");
			manifest.appendChild	(attribute);
			attribute				= document.createElement("attribute");
			attribute.setAttribute	("name", "Implementation-Version");
			attribute.setAttribute	("value", "${revision}");
			manifest.appendChild	(attribute);
			
			jar.appendChild			(manifest);
			target.appendChild		(jar);
			
			Element publish			= createElement("ivy:publish", attrPublish);
			target.appendChild		(publish);
			Element artifacts		= createElement("artifacts", attrArtifacts);
			publish.appendChild		(artifacts);

			root.appendChild		(target);

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
	
	private static Element createElement (String name, String[][] attributes) {
		Element elem 				= document.createElement(name);
		for  (String[] attr : attributes)
			elem.setAttribute			(attr[0], attr[1]);
		return elem;
	}

	public static void main(String argv[]) {
		createBuildXml			(Configuration.getConfiguration().getProjectPath()+File.separator+"build.xml","de.kbs.xxx");
	}
}