package de.kbs.SO0373JB.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SysIndexes {

	private static final String SQL	= "select NAME, CREATOR, UNIQUERULE, TBNAME, TBCREATOR"
									+ " from SYSIBM.SYSINDEXES ";

	private String 	name;
	private String  creator;
	private String  uniquerule;
	private String 	tbname;
	private String 	tbcreator;
	
	public SysIndexes (String name, String creator, String uniquerule, String tbname, String tbcreator) {
		this.name		= name;
		this.creator	= creator;
		this.uniquerule	= uniquerule;
		this.tbname		= tbname;
		this.tbcreator	= tbcreator;
	}
	
	public static ArrayList<SysIndexes> read (String tbcreator, String tbname) {
		String sqlPlus 	= " where TBCREATOR = ? and TBNAME = ?"
						+ " and      (   UNIQUERULE = 'U'      "
						+ "           or UNIQUERULE = 'D')     "
						+ "order by NAME                       ";
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
		
	private static ArrayList<SysIndexes> processResultSet (ResultSet rs) throws SQLException {
		ArrayList<SysIndexes> list		= new ArrayList<SysIndexes>();
		while (rs.next()) {
			list.add (new SysIndexes	( rs.getString(1)
										, rs.getString(2)
										, rs.getString(3)
										, rs.getString(4)
										, rs.getString(5)));
		}
		return 							list;
		
	}
	
	public String getName() 		{	return name;		}
	public String getCreator()		{	return creator;		}
	public String getUniquerule()	{	return uniquerule;	}
	public String getTbname() 		{	return tbname;		}
	public String getTbcreator()	{	return tbcreator;	}
}
