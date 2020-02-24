package de.kbs.so0373jb.dao.sqlserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.kbs.so0373jb.common.config.Configuration;

public class SqlServerConnection {

	private static SqlServerConnection connection = null;
	private static Connection con;
	private static final String DRIVER 		= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static Configuration config		= Configuration.getConfiguration();
	
	private SqlServerConnection() throws SQLException {
		
		try {
			Class.forName		(DRIVER);
		} catch (ClassNotFoundException e) {}		
		
		String user				= config.getJdbcUser();
		String pw				= config.getJdbcPw();
		if  (user==null||pw==null) {
			System.out.println 	("\n-----------------   FEHLER   ---------------------\n");
			System.out.println 	("Fehler beim Aufbau der Verbindung zum SQL Server");
			System.out.println 	("User ["+user+"] oder Passwort ["+pw+"] nicht korrekt");
			System.out.println 	("\n--------------------------------------------------\n");
			System.exit			(0);

		}
		con 					= DriverManager.getConnection	(config.getUrl(), user, pw);
	}
	
	public static PreparedStatement getStatement (String sql) throws SQLException {
			if (connection==null)
				connection			= new SqlServerConnection();
			return					con.prepareStatement(sql);
	}
	
	public static boolean existsTable (String creator, String name) throws SQLException {
			PreparedStatement stmt 		= getStatement(	"select 0 							" + 
														"from 	INFORMATION_SCHEMA.TABLES 	" + 
														"where	TABLE_SCHEMA = ?			" + 
														"  and	TABLE_NAME = ?				");
			stmt.setString					(1, creator);
			stmt.setString					(2, name);
			ResultSet rs					= stmt.executeQuery();
			if (rs.next())
				return true;
			else
				return false;
	}
}
