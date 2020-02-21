package de.kbs.so0373jb.dao.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SqlServerKey {

	private String 	ixcreator;
	private String 	ixname;
	private String 	colname;
	
	public SqlServerKey (String ixcreator, String ixname, String colname) {
		this.ixcreator		= ixcreator;
		this.ixname			= ixname;
		this.colname		= colname;
	}
	
	public static ArrayList<SqlServerKey> read (String ixcreator, String ixname) {
		String sql 		= "select COLNAME"
						+ " from SYSIBM.SYSKEYS"
						+ " where IXCREATOR = ? and IXNAME = ? "
						+ " order by COLSEQ";
		ArrayList<SqlServerKey> list		= new ArrayList<SqlServerKey>();
		try {
			PreparedStatement stmt 			= SqlServerConnection.getStatement(sql);
			stmt.setString					(1, ixcreator);
			stmt.setString					(2, ixname);
			ResultSet rs					= stmt.executeQuery();
			while (rs.next()) {
				list.add (new SqlServerKey		( ixcreator
											, ixname
											, rs.getString(1)));
			}
			return 							list;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}

	public String getIxcreator() 		{	return ixcreator;		}
	public String getIxname() 		{	return ixname;		}
	public String getColname() 		{	return colname;		}

}
