package de.kbs.so0373jb.dao.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SqlServerRel {

	private static final String SQL = 	"SELECT	child.TABLE_SCHEMA, 									" + 
										"    	child.TABLE_NAME,		 								" + 
										"    	parent.CONSTRAINT_NAME, 								" + 	
										"    	parent.TABLE_NAME,										" + 
										"       parent.TABLE_SCHEMA		 								" + 
										"FROM 	INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS a,			" + 
										"     	INFORMATION_SCHEMA.KEY_COLUMN_USAGE child,				" + 
										"     	INFORMATION_SCHEMA.KEY_COLUMN_USAGE parent				" + 
										"where 	child.CONSTRAINT_CATALOG = a.CONSTRAINT_CATALOG 		" + 
										"  AND  child.CONSTRAINT_SCHEMA = a.CONSTRAINT_SCHEMA 			" + 
										"  AND  child.CONSTRAINT_NAME = a.CONSTRAINT_NAME 				" + 
										"  AND  parent.CONSTRAINT_SCHEMA = a.UNIQUE_CONSTRAINT_SCHEMA	" + 
										"  AND  parent.CONSTRAINT_NAME = a.UNIQUE_CONSTRAINT_NAME 		" 
//										+"  AND  parent.ORDINAL_POSITION = child.ORDINAL_POSITION 		"
										;
	private String 	creator;
	private String 	tbname;
	private String 	relname;
	private String	reftbname;
	private String	reftbcreator;
	
	public SqlServerRel (String creator, String tbname, String relname, String reftbname, String reftbcreator) {
		this.creator		= creator;
		this.tbname			= tbname;
		this.relname		= relname;
		this.reftbname		= reftbname;
		this.reftbcreator	= reftbcreator;
	}
	
	public static ArrayList<SqlServerRel> readParent (String tbcreator, String tbname) {
		String sqlPlus	= " and child.TABLE_SCHEMA = ? and child.TABLE_NAME = ?"
						+ " order by parent.TABLE_NAME ";
		try {
			PreparedStatement stmt 			= SqlServerConnection.getStatement(SQL+sqlPlus);
			stmt.setString					(1, tbcreator);
			stmt.setString					(2, tbname);
			return processResultSet			(stmt.executeQuery());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}
	
	public static ArrayList<SqlServerRel> readChild (String reftbcreator, String reftbname) {
		String sqlPlus	= 	" and parent.TABLE_SCHEMA = ? and parent.TABLE_NAME = ?"  +
				 			" order by child.TABLE_NAME ";
		try {
			PreparedStatement stmt 			= SqlServerConnection.getStatement(SQL+sqlPlus);
			stmt.setString					(1, reftbcreator);
			stmt.setString					(2, reftbname);
			return processResultSet			(stmt.executeQuery());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}
	
	private static ArrayList<SqlServerRel> processResultSet (ResultSet rs) throws SQLException {
		ArrayList<SqlServerRel> list		= new ArrayList<SqlServerRel>();
		while (rs.next()) {
			list.add (new SqlServerRel		( rs.getString(1)
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
	
	public String toString () {
		StringBuffer buf		= new StringBuffer("SQL Server Rel=");
		buf.append				("[creator="+creator);
		buf.append				("],[tbname="+tbname);
		buf.append				("],[relname="+relname);
		buf.append				("],[reftbname="+reftbname);
		buf.append				("],[reftbcreator="+reftbcreator+"]");
		return					buf.toString();
	}
}
