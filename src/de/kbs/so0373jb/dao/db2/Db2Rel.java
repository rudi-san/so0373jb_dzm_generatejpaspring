package de.kbs.so0373jb.dao.db2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Db2Rel {

	private static final String SQL = "select CREATOR, TBNAME, RELNAME, REFTBNAME, REFTBCREATOR"
									+ " from SYSIBM.SYSRELS ";
	private String 	creator;
	private String 	tbname;
	private String 	relname;
	private String	reftbname;
	private String	reftbcreator;
	
	public Db2Rel (String creator, String tbname, String relname, String reftbname, String reftbcreator) {
		this.creator		= creator;
		this.tbname			= tbname;
		this.relname		= relname;
		this.reftbname		= reftbname;
		this.reftbcreator	= reftbcreator;
	}
	
	public static ArrayList<Db2Rel> readParent (String tbcreator, String tbname) {
		String sqlPlus	= " where CREATOR = ? and TBNAME = ?"
						+ " order by REFTBNAME";
		try {
			PreparedStatement stmt 			= Db2Connection.getStatement(SQL+sqlPlus);
			stmt.setString					(1, tbcreator);
			stmt.setString					(2, tbname);
			return processResultSet			(stmt.executeQuery());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}
	
	public static ArrayList<Db2Rel> readChild (String reftbcreator, String reftbname) {
		String sqlPlus	= " where REFTBCREATOR = ? and REFTBNAME = ?"
						+ " order by TBNAME";
		try {
			PreparedStatement stmt 			= Db2Connection.getStatement(SQL+sqlPlus);
			stmt.setString					(1, reftbcreator);
			stmt.setString					(2, reftbname);
			return processResultSet			(stmt.executeQuery());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}
	
	private static ArrayList<Db2Rel> processResultSet (ResultSet rs) throws SQLException {
		ArrayList<Db2Rel> list		= new ArrayList<Db2Rel>();
		while (rs.next()) {
			list.add (new Db2Rel		( rs.getString(1)
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
