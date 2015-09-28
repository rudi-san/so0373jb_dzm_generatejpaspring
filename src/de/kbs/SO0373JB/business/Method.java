package de.kbs.SO0373JB.business;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import de.kbs.SO0373JB.common.constants.Constants;
import de.kbs.SO0373JB.common.enums.Visibility;
import de.kbs.so1320jc.main.LoggingContainer;

public class Method {

	private String 					methodName;
	private Visibility				visibility;
	private String					type;
	private boolean					isStatic	= false;
	private boolean					isComment	= false;
	private ArrayList<String>		annotList	= new ArrayList<String>();
	private ArrayList<Parameter> 	paraList	= new ArrayList<Parameter>();
	private ArrayList<String>		body		= new ArrayList<String>();
	private ArrayList<String>       throwList	= new ArrayList<String>();
	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();
	private static final String		NEWLINE		= Constants.NEWLINE;

	public Method (String methodName, String type, Parameter... parameters) {
		this(methodName, Visibility.visibility_public, type, parameters);
	}
	
	public Method (String methodName, Visibility visibility, String type, Parameter... parameters) {
		this.methodName		= methodName;
		this.visibility		= visibility;
		this.type			= type;
		for (Parameter p : parameters) {
			paraList.add		(p);
		}
	}
	
	public void setStatic () {
		isStatic		= true;
	}
	
	public void setComment () {
		isComment		= true;
	}
	
	public void addSkeleton (String skelName, String... fill) {
		String skeleton		= Skeleton.getSkeleton(skelName);
		int start			= 0;
		if (skeleton==null) {
			System.out.println ("Skeleton "+skelName+" nicht gefunden.");
			System.exit(0);
		}
		while (skeleton.substring(start).contains(Constants.WILDCARD)) {
			int find		= skeleton.substring(start).indexOf(Constants.WILDCARD)+start+3;
			int end			= skeleton.substring(find).indexOf('%')+find;
			String luecke	= skeleton.substring(find, end);
			int index		= Integer.parseInt(luecke);
			if (index>=fill.length) {
				String msg						= "Skeleton "+skelName+" hat zu wenig Wildcards";
				logger.fatal					(msg);
				JOptionPane.showMessageDialog	(null, msg);
				System.exit						(0);
			}
			else {
				String teil1				= skeleton.substring(0, find-3)+fill[index];
				start						= teil1.length();
				skeleton					= teil1+skeleton.substring(end+1);
			}
		}
		body.add					("\t"+skeleton+NEWLINE);
	}
	
	public void addAnnot (String... annotations) {
		logger.debug		("Annotation wird hinzugefügt");
		for (String annot : annotations)
			annotList.add		(annot);
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
		if (isComment)
			buf.append			(NEWLINE+"/*");
		buf.append				(NEWLINE);
		for (String annot : annotList )
			buf.append				("\t"+annot+NEWLINE);
		String staticKenn		= (isStatic) ? " static " : " ";
		buf.append				("\t"+visibility+staticKenn+type+" "+methodName+" (");
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
		buf.append					(" {"+NEWLINE);
		for (String s : body) 
			buf.append				("\t"+s);
		buf.append			("\t}"+NEWLINE);
		if (isComment)
			buf.append			("*/");
		return				buf.toString();
	}
	
	public static void main (String[] args) {
		Parameter p		= new Parameter("testparameter", "String");
		Method m		= new Method("testMethod",Visibility.visibility_public,"int",p);
		m.addSkeleton	("SKEL001", "TESTVARIABLE");
		System.out.println(m);
	}
}
