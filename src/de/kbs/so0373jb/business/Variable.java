package de.kbs.so0373jb.business;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.kbs.so0373jb.common.constants.Constants;
import de.kbs.so0373jb.common.enums.Visibility;
import de.kbs.so1320jc.main.LoggingContainer;

public class Variable {

	private 			String 		varName;
	private 			Visibility	visibility;
	private 			boolean		isStatic	= false;
	private				boolean		isFinal		= false;
	private				boolean		isComment	= false;
	private 		 	String		type;
	private 		 	String		initial;
	private ArrayList<String>		annotList	= new ArrayList<String>();
	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();
	private static final String 	NEWLINE		= Constants.NEWLINE;
	
	public Variable (String varName, Visibility visibility, String type, String initial) {
		this.varName		= varName;
		this.visibility		= visibility;
		this.type			= type;
		this.initial		= initial;
	}
	
	public void addAnnot (String... annotations) {
		for (String annot : annotations) {
			logger.debug		("Variablen-Annotation ["+annot+"] wird hinzugefügt");
			annotList.add		(annot);
		}
	}
	
	public void setFinalStatic () {
		isFinal			= true;
		isStatic		= true;
	}

	public void setStatic () {
		isStatic		= true;
	}
	
	public void setComment (boolean comment) {
		isComment		= comment;
	}
	
	public String getName () {
		return varName;
	}
	
	@Override
	public String toString () {
		StringBuffer buf		= new StringBuffer();
		if (isComment)
			buf.append			(NEWLINE+"/*");
		buf.append				(NEWLINE);
		for (String annot : annotList )
			buf.append				("\t"+annot+NEWLINE);
		String settings			= visibility.toString();
		settings				= settings + ( (isStatic) ? " static" : "" );
		settings				= settings + ( (isFinal) ? " final" : "" );
		buf.append				("\t"+settings+" "+type+" "+varName);
		if (initial!=null)
			buf.append				(" = "+initial);
		buf.append				(";"+NEWLINE);
		if (isComment)
			buf.append			("*/");
		return				buf.toString();
	}
}
