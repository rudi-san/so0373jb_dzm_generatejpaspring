package de.kbs.so0373jb.dao.db2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Db2Key {

	private String 	ixcreator;
	private String 	ixname;
	private String 	colname;
	
	public Db2Key (String ixcreator, String ixname, String colname) {
		this.ixcreator		= ixcreator;
		this.ixname			= ixname;
		this.colname		= colname;
	}
	
	public static ArrayList<Db2Key> read (String ixcreator, String ixname) {
		String sql 		= "select COLNAME"
						+ " from SYSIBM.SYSKEYS"
						+ " where IXCREATOR = ? and IXNAME = ? "
						+ " order by COLSEQ";
		ArrayList<Db2Key> list		= new ArrayList<Db2Key>();
		try {
			PreparedStatement stmt 			= Db2Connection.getStatement(sql);
			stmt.setString					(1, ixcreator);
			stmt.setString					(2, ixname);
			ResultSet rs					= stmt.executeQuery();
			while (rs.next()) {
				list.add (new Db2Key		( ixcreator
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
