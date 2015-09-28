package de.kbs.SO0373JB.business;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.kbs.SO0373JB.common.constants.Constants;
import de.kbs.SO0373JB.common.enums.Visibility;
import de.kbs.so1320jc.main.LoggingContainer;

/** Allgemeingueltige Klasse fuer die Generierung von Java-Klassen im Source-Format.
 *  Die Ausgabe des Codes erfolgt über die toString-Methode. 
 * @author rschneid
 *
 */
public class Clazz {
	
	private String              packageName;
	private String 				className;
	private Visibility 			visibility;
	private ArrayList<String> 	annotList	= new ArrayList<String>();
	private ArrayList<String> 	importList	= new ArrayList<String>();
	private ArrayList<String> 	interfList	= new ArrayList<String>();
	private ArrayList<Variable> varList		= new ArrayList<Variable>();
	private ArrayList<Method>   methodList	= new ArrayList<Method>();
	private ArrayList<String>   divList		= new ArrayList<String>();
	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();
	private static final String NEWLINE		= Constants.NEWLINE;
	
	public Clazz (String packageName, String className, Visibility visibility) {
		this(packageName, className, visibility, new String[0]);
	}
 
	public Clazz (String packageName, String className, Visibility visibility, String... imports) {
		this.packageName	= packageName;
		this.className		= className;
		this.visibility		= visibility;
		for (String im : imports) {
			importList.add			(im);
		}
	}

	public void addAnnot (String... annotations) {
		for (String annot : annotations) {
			logger.debug		("Annotation ["+annot+"]wird hinzugefügt");
			annotList.add		(annot);
		}
	}

	public void addImport (String... imports) {
		for (String im : imports) {
			logger.debug		("Import ["+im+"] wird hinzugefügt");
			importList.add		(im);
		}
	}

	public void addInterface (String... interf) {
		for (String in : interf) {
			logger.debug		("Interface ["+in+"] wird hinzugefügt");
			interfList.add		(in);
		}
	}

	public void addVariable (Variable variable) {
		logger.debug		("Variable ["+variable.getName()+"] wird hinzugefügt");
		varList.add			(variable);
	}

	public void addMethod (Method method) {
		logger.debug		("Methode ["+method.getName()+"] wird hinzugefügt");
		methodList.add		(method);
	}

	public void addDiv (String... div) {
		for (String d : div) {
			logger.debug		("Diverser Text ["+d+"] wird hinzugefügt");
			divList.add			(d);
		}
	}
	
	public String getName () {
		return  	className;
	}

	@Override
	public String toString () {
		StringBuffer buf		= new StringBuffer();
		buf.append				("package "+packageName+";"+NEWLINE+NEWLINE);
		for (String im : importList )
			buf.append				("import "+im+";"+NEWLINE);
		buf.append				(NEWLINE);
		for (String annot : annotList )
			buf.append				(annot+NEWLINE);
		buf.append				(visibility+" class "+className);
		if (interfList.size()>0)
			buf.append			(" implements ");
		boolean start		= true;
		for (String interf : interfList) {
			if (start)				{ buf.append	(interf); start = false;}
			else					{ buf.append	(", "+interf);			}
		}
		buf.append			(" {"+NEWLINE);
		for (Variable v : varList )
			buf.append				(v);
		for (String div : divList )
			buf.append				(div);
		for (Method m : methodList )
			buf.append				(m);
		buf.append				(NEWLINE+"}");
		return				buf.toString();
	}
}
