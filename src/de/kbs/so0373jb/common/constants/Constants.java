package de.kbs.so0373jb.common.constants;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Constants {

	public static final String NEWLINE		= System.getProperty("line.separator");
	public static final String WILDCARD		= "%_%";
	public static final String XML_SKELS	= "SKELS";
	public static final String XML_SKELNAME	= "SKELNAME";
	public static final String XML_SKELFILE	= "resources/skeletons/DZMSKELS.xml";
	public static final String DBURL 		= "jdbc:db2://hostbkn:7002/DBT2LN";

	/**
	 * @return Pfadangabe zum Applikationsverzeichnis (Basisverzeichnis)
	 * 
	 * */
	@SuppressWarnings("rawtypes")
	public static final String getAPPLICATION_PATH(Class clazz) 
	{	
		try
		{
			return Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
		}
		catch (URISyntaxException e)
		{
			throw new RuntimeException("Failed to create \"APPLICATION_PATH\" instance in static block.", e);
		}
	}

	
}
