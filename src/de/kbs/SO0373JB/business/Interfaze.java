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
public class Interfaze {
	
	private String              packageName;
	private String 				interfName;
	private String				parent		= null;
	private ArrayList<String> 	importList	= new ArrayList<>();
	private ArrayList<String> 	interfList	= new ArrayList<>();
	private ArrayList<Variable> varList		= new ArrayList<>();
	private ArrayList<AbstractMethod>   methodList	= new ArrayList<>();
	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();
	private static final String NEWLINE		= Constants.NEWLINE;
	
	public Interfaze (String packageName, String className, String parent) {
		this(packageName, className, parent, new String[0]);
	}
 
	public Interfaze (String packageName, String interfName, String parent, String... imports) {
		this.packageName	= packageName;
		this.interfName		= interfName;
		this.parent			= parent;
		for (String im : imports) {
			importList.add			(im);
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

	public void addMethod (AbstractMethod method) {
		logger.debug		("Methode ["+method.getName()+"] wird hinzugefügt");
		methodList.add		(method);
	}

	
	public String getName () {
		return  	interfName;
	}

	@Override
	public String toString () {
		StringBuffer buf		= new StringBuffer();
		buf.append				("package "+packageName+";"+NEWLINE+NEWLINE);
		for (String im : importList )
			buf.append				("import "+im+";"+NEWLINE);
		buf.append				(NEWLINE);
		buf.append				(Visibility.visibility_public+" interface "+interfName);
		if (parent!=null)
			buf.append			(" extends "+parent);
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
		for (AbstractMethod m : methodList )
			buf.append				(m);
		buf.append				(NEWLINE+"}");
		return				buf.toString();
	}
}
