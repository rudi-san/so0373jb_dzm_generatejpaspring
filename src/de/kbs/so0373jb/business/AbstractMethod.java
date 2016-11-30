package de.kbs.so0373jb.business;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.kbs.so0373jb.common.constants.Constants;
import de.kbs.so1320jc.main.LoggingContainer;

public class AbstractMethod {

	private String 					methodName;
	private String					type;
	private ArrayList<Parameter> 	paraList	= new ArrayList<Parameter>();
	private ArrayList<String>       throwList	= new ArrayList<String>();
	private static Logger logger	= LoggingContainer.getLoggerInstance().getRootLogger();
	private static final String		NEWLINE		= Constants.NEWLINE;
  
	public AbstractMethod (String methodName, String type, Parameter... parameters) {
		this.methodName		= methodName;
		this.type			= type;
		for (Parameter p : parameters) {
			paraList.add		(p);
		}
	}
	public void addThrow (String... throwss) {
		logger.debug		("throw wird hinzugefügt");
		for (String th : throwss)
			throwList.add		(th);
	}

	public String getName () {
		return methodName;
	}
	
	@Override
	public String toString () {
		StringBuffer buf		= new StringBuffer();
		buf.append				("\t"+type+" "+methodName+" (");
		buf.append				(Parameter.toList(paraList)+") ");
		if  (throwList.size()>0)
			buf.append				("throws ");
		boolean start			= true;
		for (String th : throwList) {
			if (start) {
				buf.append				(th);
				start					= false;
			}
			else
				buf.append				(", "+th);
		}
		buf.append					(" ;"+NEWLINE);
		return				buf.toString();
	}

}
