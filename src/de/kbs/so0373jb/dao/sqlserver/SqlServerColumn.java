package de.kbs.so0373jb.dao.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SqlServerColumn {

	private static final String SQL	= "select 	COLUMN_NAME, TABLE_NAME, TABLE_SCHEMA, ORDINAL_POSITION, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_SCALE, IS_NULLABLE, COLUMN_DEFAULT " + 
									  "from 	INFORMATION_SCHEMA.COLUMNS ";

	private String 	name;
	private String 	tbname;
	private String 	tbcreator;
	private int	 	colno;
	private String	coltype;
	private int		length;
	private int		scale;
	private String	nulls;
	private String  defaultx;
	private boolean	isKey;
	private boolean	isGenerated;
	
	public SqlServerColumn (String name, String tbname, String tbcreator, int colno, String coltype,
						int length, int scale, String nulls, String defaultx, boolean isKey, boolean isGenerated) {
		this.name		= name;
		this.tbname		= tbname;
		this.tbcreator	= tbcreator;
		this.colno		= colno;
		this.coltype	= coltype;
		this.length		= length;
		this.scale		= scale;
		this.nulls		= nulls;
		this.defaultx	= defaultx;
		this.isKey		= isKey;
		this.isGenerated = isGenerated;
	}
	
	public static ArrayList<SqlServerColumn> read (String tbcreator, String tbname) {
		String sqlPlus 	= "where 	TABLE_SCHEMA = ? and TABLE_NAME = ?";
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
	
	public static ArrayList<SqlServerColumn> read (String tbcreator, String tbname, int keyseq) {
		String sqlPlus				= " where TBCREATOR = ? and TBNAME = ? and KEYSEQ = ?";
		try {
			PreparedStatement stmt 			= SqlServerConnection.getStatement(SQL+sqlPlus);
			stmt.setString					(1, tbcreator);
			stmt.setString					(2, tbname);
			stmt.setInt						(3, keyseq);
			return processResultSet			(stmt.executeQuery());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}
	
	private static ArrayList<SqlServerColumn> processResultSet (ResultSet rs) throws SQLException {
		ArrayList<SqlServerColumn> list		= new ArrayList<SqlServerColumn>();
		while (rs.next()) {
			list.add (new SqlServerColumn	( rs.getString(1)
										, rs.getString(2)
										, rs.getString(3)
										, rs.getInt(4)
										, rs.getString(5)
										, rs.getInt(6)
										, rs.getInt(7)
										, rs.getString(8)
										, rs.getString(9)
										, isKey(rs.getString(1),rs.getString(2),rs.getString(3))
										, isGenerated(rs.getString(1),rs.getString(2),rs.getString(3))));
		}
		return 							list;		
	}
	
	private static boolean isKey (String colName, String tabName, String schema) {
		String sqlKey					= "select 	0 " + 
										  "from 	INFORMATION_SCHEMA.TABLE_CONSTRAINTS a, "	+ 
										  "		    INFORMATION_SCHEMA.KEY_COLUMN_USAGE b " 		+ 
										  "where	a.TABLE_SCHEMA = ? " 						+ 
										  "  and	a.TABLE_NAME = ? " 							+ 
										  "  and	a.CONSTRAINT_TYPE = 'PRIMARY KEY' " 		+ 
										  "  and    a.CONSTRAINT_NAME = b.CONSTRAINT_NAME " 		+ 
										  "  and    b.COLUMN_NAME = ?";

		try {
			PreparedStatement stmt 			= SqlServerConnection.getStatement(sqlKey);
			stmt.setString					(1, schema);
			stmt.setString					(2, tabName);
			stmt.setString					(3, colName);
			ResultSet rs					= stmt.executeQuery();
		
			if  (rs.next())					return true;
			else							return false;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return false;
	}

	private static boolean isGenerated (String colName, String tabName, String schema) {
		String sqlKey					= "SELECT 	0                             " 
										+ "FROM 	INFORMATION_SCHEMA.COLUMNS    " 
										+ "WHERE	TABLE_SCHEMA = ?              " 
										+ "  and	TABLE_NAME = ?                " 
										+ "  and    COLUMN_NAME = ?               " 
										+ "  AND	COLUMNPROPERTY( OBJECT_ID(TABLE_NAME),COLUMN_NAME,'ISIdentity') = 1";

		try {
			PreparedStatement stmt 			= SqlServerConnection.getStatement(sqlKey);
			stmt.setString					(1, schema);
			stmt.setString					(2, tabName);
			stmt.setString					(3, colName);
			ResultSet rs					= stmt.executeQuery();
		
			if  (rs.next())					return true;
			else							return false;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return false;
	}
	
	public String 	getName() 		{	return name;		}
	public String 	getTbname() 	{	return tbname;		}
	public String 	getTbcreator()	{	return tbcreator;	}
	public int 		getColno() 		{	return colno;		}
	public String 	getColtype() 	{	return coltype;		}
	public int 		getLength() 	{	return length;		}
	public int 		getScale() 		{	return scale;		}
	public String 	getNulls() 		{	return nulls;		}
	public String 	getDefault() 	{	return defaultx;	}
	public boolean 	isKey()	 		{	return isKey;		}
	public boolean 	isGenerated()	{	return isGenerated;	}
	
	public String toString () {
		StringBuffer buf		= new StringBuffer("SQLServerColumns=");
		buf.append				("[name="+name);
		buf.append				("],[tbname="+tbname);
		buf.append				("],[tbcreator="+tbcreator);
		buf.append				("],[colno="+colno);
		buf.append				("],[coltype="+coltype);
		buf.append				("],[length="+length);
		buf.append				("],[length2="+length);
		buf.append				("],[scale="+scale);
		buf.append				("],[nulls="+nulls);
		buf.append				("],[defaultx="+defaultx);
		buf.append				("],[isKey="+isKey+"]");
		buf.append				("],[isGenerated="+isGenerated+"]");
		return					buf.toString();
	}
}
