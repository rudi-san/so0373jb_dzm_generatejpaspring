package de.kbs.SO0373JB.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.kbs.SO0373JB.common.config.Configuration;
import de.kbs.SO0373JB.common.constants.Constants;

public class DB2Connection {

	private static DB2Connection connection = null;
	private static Connection con;
	private static final String DRIVER 		= "com.ibm.db2.jcc.DB2Driver";
	
	private DB2Connection() throws SQLException {
		
		try {
			Class.forName		(DRIVER);
		} catch (ClassNotFoundException e) {}		
		
		String user				= Configuration.getConfiguration().getJdbcUser();
		String pw				= Configuration.getConfiguration().getJdbcPw();
		if  (user==null||pw==null) {
			System.out.println 	("\n-----------------   FEHLER   ---------------------\n");
			System.out.println 	("Fehler beim Aufbau der DB2-Verbindung");
			System.out.println 	("User ["+user+"] oder Passwort ["+pw+"] nicht korrekt");
			System.out.println 	("\n--------------------------------------------------\n");
			System.exit			(0);

		}
		con 					= DriverManager.getConnection	(Constants.DBURL, user, pw);
	}
	
	public static PreparedStatement getStatement (String sql) throws SQLException {
			if (connection==null)
				connection			= new DB2Connection();
			return					con.prepareStatement(sql);
	}
}
