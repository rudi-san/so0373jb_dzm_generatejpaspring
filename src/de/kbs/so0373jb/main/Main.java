package de.kbs.so0373jb.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.Properties;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

//import de.kbs.SO0373JB.xml.BuildXML;
//import de.kbs.SO0373JB.xml.IvyXML;



import de.kbs.so0373jb.business.Clazz;
//import de.kbs.so0373jb.business.FXClazz;
import de.kbs.so0373jb.business.JPAClazz;
import de.kbs.so0373jb.business.SpringRepository;
import de.kbs.so0373jb.common.config.Configuration;
import de.kbs.so0373jb.db2.Db2Table;

public class Main {
	
	private static Configuration 	configuration;
	private static String[][] 		tables;
	private static Logger 			logger;

	public static void main(String[] args) {
		
		try {
			doGener				("resources/config/TBTST.xml");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "JPA-Gener", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	
	public static void doGener (String config) throws SQLException {

		configuration			= Configuration.getConfiguration(config);
		logger					= configuration.getRootLogger();

		tables					= configuration.getTables();

		String message			= null;
//		if (configuration.isJavaFx())
//			message				= "<html>Es wurden JPA-Klassen (JavaFX-Binding) erstellt für die Tabellen<br><br>";
//		else
			message				= "<html>Es wurden JPA-Klassen erstellt für die Tabellen<br><br>";

		for (String[] split : tables) {
			Db2Table table			= Db2Table.createTable(split[0], split[1]);
			Clazz clazz				= null;
//			if (configuration.isJavaFx())
//				clazz					= new FXClazz(table);
//			else
				clazz					= new JPAClazz(table);
			writeFile				(configuration.getJpaPath(), table.getCcName(), clazz.toString());
			SpringRepository repo	= new SpringRepository(table);
			writeFile				(configuration.getReposPath(), table.getCcName()+"Repository", repo.toString());
			message 				= message + "<i>"+split[1]+"</i><br>";
		}
 		JOptionPane.showMessageDialog
 								(null, message);

// 		IvyXML.createIvyXml		(configuration.getProjectPath()+"\\ivy.xml");
//		IvyXML.createIvySettings(configuration.getProjectPath()+"\\ivyjpasettings.xml");
//		BuildXML.createBuildXml	(configuration.getProjectPath()+"\\build.xml", configuration.getPackage());
//		writeBuildProperties	();
		
//		JOptionPane.showMessageDialog
//								(null, "<html>Konfigurationsdateien wurden erstellt<br><br>"
//								+ "<i>ivy.xml</html>");
	}
	
	public static boolean tableExists (String tbna) {
		boolean ret		= false;
		for (String[] split : tables) {
			if (split[1].equalsIgnoreCase(tbna))  ret = true;
		}
		return 			ret;
	}

	public static void writeFile (String path, String fileName, String content) {
		try {
			new File(path).mkdirs	();
			FileWriter writer		= new FileWriter(path+File.separator+fileName+".java");
			writer.write			(content);
			writer.flush			();
			writer.close			();
		} catch (IOException e) {
			logger.fatal			(e.getMessage());
			System.exit				(0);
		}
	}

//	private static void writeBuildProperties() {
//		Properties prop		= new Properties();
//		prop.setProperty	("ivy.organisation", "de.kbs");
//		prop.setProperty	("ivy.module", configuration.getModuleName());
//		prop.setProperty	("ivy.revision", "1.0.0");
//		prop.setProperty	("ivy.settings.file", "ivyjpasettings.xml");
//		try {
//			prop.store			( new FileWriter(configuration.getProjectPath()+File.separator+"build.properties")
//								, "Ant-build-File\n"
//								+ "Properties für das Build-File mit jar-Erzeugung und Ivy-Anbindung");
//		} catch (IOException e) {
//			JOptionPane.showMessageDialog(null, "Fehler: "+e.getMessage());
//		}	
//	}
}
