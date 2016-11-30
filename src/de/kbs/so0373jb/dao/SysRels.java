package de.kbs.so0373jb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SysRels {

	private static final String SQL = "select CREATOR, TBNAME, RELNAME, REFTBNAME, REFTBCREATOR"
									+ " from SYSIBM.SYSRELS ";
	private String 	creator;
	private String 	tbname;
	private String 	relname;
	private String	reftbname;
	private String	reftbcreator;
	
	public SysRels (String creator, String tbname, String relname, String reftbname, String reftbcreator) {
		this.creator		= creator;
		this.tbname			= tbname;
		this.relname		= relname;
		this.reftbname		= reftbname;
		this.reftbcreator	= reftbcreator;
	}
	
	public static ArrayList<SysRels> readParent (String tbcreator, String tbname) {
		String sqlPlus	= " where CREATOR = ? and TBNAME = ?"
						+ " order by REFTBNAME";
		try {
			PreparedStatement stmt 			= DB2Connection.getStatement(SQL+sqlPlus);
			stmt.setString					(1, tbcreator);
			stmt.setString					(2, tbname);
			return processResultSet			(stmt.executeQuery());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}
	
	public static ArrayList<SysRels> readChild (String reftbcreator, String reftbname) {
		String sqlPlus	= " where REFTBCREATOR = ? and REFTBNAME = ?"
						+ " order by TBNAME";
		try {
			PreparedStatement stmt 			= DB2Connection.getStatement(SQL+sqlPlus);
			stmt.setString					(1, reftbcreator);
			stmt.setString					(2, reftbname);
			return processResultSet			(stmt.executeQuery());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}
	
	private static ArrayList<SysRels> processResultSet (ResultSet rs) throws SQLException {
		ArrayList<SysRels> list		= new ArrayList<SysRels>();
		while (rs.next()) {
			list.add (new SysRels		( rs.getString(1)
										, rs.getString(2)
										, rs.getString(3)
										, rs.getString(4)
										, rs.getString(5)));
		}
		return 							list;
	}
	
	public String getCreator() 		{	return creator;		}
	public String getTbname() 		{	return tbname;		}
	public String getRelname() 		{	return relname;		}
	public String getReftbname() 	{	return reftbname;	}
	public String getReftbcreator() {	return reftbcreator;}

}
