package de.kbs.so0373jb.business;

import java.util.ArrayList;

//import org.apache.log4j.Logger;
//
//import de.kbs.so1320jc.main.LoggingContainer;

public class Parameter {

	private String 				paraName;
	private String				type;
//	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();

	public Parameter (String paraName, String type) {
		this.paraName	= paraName;
		this.type		= type;
	}
	
	public String getParaName () {
		return this.paraName;
	}
	
	public static String toList (ArrayList<Parameter> list) {
//		logger.debug		("Methode Parameter.toList() aufgerufen");
		StringBuffer buf	= new StringBuffer();
		boolean start		= true;
		for (Parameter p : list) {
			if (start) {
				start		= false;
				buf.append	(p.type+" "+p.paraName);
			}
			else
				buf.append	(", "+p.type+" "+p.paraName);
		}
		return 		buf.toString();
	}
}
