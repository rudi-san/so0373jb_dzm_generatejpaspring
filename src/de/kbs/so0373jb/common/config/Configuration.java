package de.kbs.so0373jb.common.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.kbs.SO1300JC.PWDecode;
import de.kbs.so0003jc.main.PropertyContainer;
import de.kbs.so1320jc.main.LoggingContainer;

public class Configuration {

	private static Configuration 		configuration 		= null;	
	private static PropertyContainer 	propertyContainer;
	private static Logger 				logger				= null;
	
	private static final String			LOG_FILE			= "log.file";
	private static final String			LOG_LEVEL			= "log.level";
	private static final String			WORK_PROJECT		= "work.project";
	private static final String			WORK_ROOTPATH		= "work.rootpath";
	private static final String			WORK_TABLE			= "work.table";
	private static final String			JDBC_USER			= "jdbc.user";
	private static final String			JDBC_PW				= "jdbc.pw";
	private static final String			JDBC_PWCODE			= "jdbc.pwcode";
	private static final String			IS_JAVAFX			= "fx.select";
	
	private Configuration(String fileName)  {
		initialisiereKBSPropertyHandler		(fileName);
		getRootLogger						();
		logger.info							("Configuration gestartet");
	}
	
	public static Configuration getConfiguration (String fileName) {
		if (configuration==null) 
			configuration			= new Configuration(fileName);
		return 					configuration;
	}
	
	public static Configuration getConfiguration () {
		if (configuration==null) {
			System.out.println 	("\n-----------------   FEHLER   ---------------------\n");
			System.out.println 	("       Configuration nicht initialisiert");
			System.out.println 	("\n--------------------------------------------------\n");
			System.exit			(0);			
		}
		return					configuration;
	}
	
	public Logger getRootLogger () {
		if (logger==null) {
			LoggingContainer lCont	= LoggingContainer.getLoggerInstance();
			lCont.addFileAppenderToRootLogger
									(getLogFile());
			lCont.setDefaultLevelOfRootLogger
									(getLogLevel());
			logger					= lCont.getRootLogger();
		}
		return 					logger;	
	}
	
	public String getLogFile () {
		String fileName		= propertyContainer.getProperty(LOG_FILE);
		if (fileName==null) {
			try {
				Files.createDirectories	(Paths.get("z:/", "logs"));
				fileName				= "z:/logs/SO0373JB.log";
			} catch (IOException e) {}
		}
		return fileName;
	}
	
	public Level getLogLevel () {
		String level 		= propertyContainer.getProperty(LOG_LEVEL);
		if (level==null)
			return				Level.INFO;
		else
			return				Level.toLevel(level);
	}
	public String getJpaPath() {
		return				getSourcePath() +File.separator+ "jpa";
	}
	public String getReposPath() {
		return				getSourcePath() +File.separator+ "repos";
	}
	public String getProjectPath () {
		String rootPath		= propertyContainer.getProperty(WORK_ROOTPATH);
		String project		= propertyContainer.getProperty(WORK_PROJECT);
		return				rootPath + File.separator + project;
	}
	public String getPackage () {
		return				"de.kbs." + getModuleName().toLowerCase() + ".persistence";
		
	}
	public String getSourcePath () {
		return				getProjectPath() 	+ File.separator	+ 
							"src"				+ File.separator	+ 
							getPackage().replace('.', File.separatorChar);
	}
	public String getModuleName() {
		String[] project	= propertyContainer.getProperty(WORK_PROJECT).split("_");
		return				project[0];
	}
	public String getJdbcUser () {
		return				propertyContainer.getProperty(JDBC_USER);
	}
	public boolean isJavaFx () {
		String	getString	= propertyContainer.getProperty(IS_JAVAFX);
		if 	(getString==null)
			return false;
		return  getString.matches("[JjYy].*");
	}
	public String getJdbcPw () {
		String pw			= propertyContainer.getProperty(JDBC_PW);
		if (pw==null) {
			pw					= propertyContainer.getProperty(JDBC_PWCODE);
			if (pw==null) {
				pw					= PWDecode.getPwDecoded(getJdbcUser());
				if (pw.toUpperCase().startsWith("NULL"))
					pw					= null;
			}
			else
				pw					= PWDecode.decode(pw);
		}
		return 				pw;
	}
	public String[][] getTables () {
		Properties prop		= propertyContainer.getProperties();
		ArrayList<String[]> list
							= new ArrayList<>();
		for (Object key : prop.keySet()) {
			String sKey			= (String)key;
			if (sKey.startsWith(WORK_TABLE)) {
				String[] split		= prop.getProperty(sKey).split("[.]");
				list.add			(split);
			}
		}
		String[][] retString	= new String[list.size()][];
		return					list.toArray(retString);
	}

	private static PropertyContainer initialisiereKBSPropertyHandler(String file) {	
		try {
			propertyContainer				= PropertyContainer.getPropertyInstance();
			propertyContainer.loadProperties(file);
		} catch (IOException e) {
			System.out.println 	("\n-----------------   FEHLER   ---------------------\n");
			System.out.println 	("Fehler beim Initialisieren des KBSPropertyHandlers");
			System.out.println  (e.getMessage());
			System.out.println 	("\n--------------------------------------------------\n");
			System.exit			(0);
		}
		return 					propertyContainer;
	}

}
