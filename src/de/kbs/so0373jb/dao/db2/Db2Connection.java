package de.kbs.so0373jb.dao.db2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.kbs.so0373jb.common.config.Configuration;
import de.kbs.so0373jb.common.constants.Constants;

public class Db2Connection {

	private static Db2Connection connection = null;
	private static Connection con;
	private static final String DRIVER 		= "com.ibm.db2.jcc.DB2Driver";
	
	private Db2Connection() throws SQLException {
		
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
		con 					= DriverManager.getConnection	(Constants.DB2_URL, user, pw);
	}
	
	public static PreparedStatement getStatement (String sql) throws SQLException {
			if (connection==null)
				connection			= new Db2Connection();
			return					con.prepareStatement(sql);
	}
	
	public static boolean existsTable (String creator, String name) throws SQLException {
			PreparedStatement stmt 			= getStatement("select NAME from SYSIBM.SYSTABLES where CREATOR = ? and NAME = ?");
			stmt.setString					(1, creator);
			stmt.setString					(2, name);
			ResultSet rs					= stmt.executeQuery();
			if (rs.next())
				return true;
			else
				return false;
	}
}
