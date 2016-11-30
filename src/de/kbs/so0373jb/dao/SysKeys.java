package de.kbs.so0373jb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SysKeys {

	private String 	ixcreator;
	private String 	ixname;
	private String 	colname;
	
	public SysKeys (String ixcreator, String ixname, String colname) {
		this.ixcreator		= ixcreator;
		this.ixname			= ixname;
		this.colname		= colname;
	}
	
	public static ArrayList<SysKeys> read (String ixcreator, String ixname) {
		String sql 		= "select COLNAME"
						+ " from SYSIBM.SYSKEYS"
						+ " where IXCREATOR = ? and IXNAME = ? "
						+ " order by COLSEQ";
		ArrayList<SysKeys> list		= new ArrayList<SysKeys>();
		try {
			PreparedStatement stmt 			= DB2Connection.getStatement(sql);
			stmt.setString					(1, ixcreator);
			stmt.setString					(2, ixname);
			ResultSet rs					= stmt.executeQuery();
			while (rs.next()) {
				list.add (new SysKeys		( ixcreator
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
