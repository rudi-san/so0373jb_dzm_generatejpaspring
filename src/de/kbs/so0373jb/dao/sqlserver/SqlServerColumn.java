package de.kbs.so0373jb.dao.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SqlServerColumn {

	private static final String SQL	= "select NAME, TBNAME, TBCREATOR, COLNO, COLTYPE, LENGTH, LENGTH2, SCALE, NULLS, DEFAULT, KEYSEQ"
									+ " from SYSIBM.SYSCOLUMNS ";

	private String 	name;
	private String 	tbname;
	private String 	tbcreator;
	private int	 	colno;
	private String	coltype;
	private int		length;
	private int		length2;
	private int		scale;
	private String	nulls;
	private String  defaultx;
	private int		keyseq;
	
	public SqlServerColumn (String name, String tbname, String tbcreator, int colno, String coltype,
						int length, int length2, int scale, String nulls, String defaultx, int keyseq) {
		this.name		= name;
		this.tbname		= tbname;
		this.tbcreator	= tbcreator;
		this.colno		= colno;
		this.coltype	= coltype;
		this.length		= length;
		this.length2		= length2;
		this.scale		= scale;
		this.nulls		= nulls;
		this.defaultx	= defaultx;
		this.keyseq		= keyseq;
	}
	
	public static ArrayList<SqlServerColumn> read (String tbcreator, String tbname) {
		String sqlPlus 	= " where TBCREATOR = ? and TBNAME = ?";
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
										, rs.getInt(8)
										, rs.getString(9)
										, rs.getString(10)
										, rs.getInt(11)));
		}
		return 							list;
		
	}
	
	public String getName() 	{	return name;		}
	public String getTbname() 	{	return tbname;		}
	public String getTbcreator(){	return tbcreator;	}
	public int getColno() 		{	return colno;		}
	public String getColtype() 	{	return coltype;		}
	public int getLength() 		{	return length;		}
	public int getLength2()		{	return length2;		}
	public int getScale() 		{	return scale;		}
	public String getNulls() 	{	return nulls;		}
	public String getDefault() 	{	return defaultx;	}
	public int getKeyseq() 		{	return keyseq;		}
	public boolean isKey()		{	return keyseq>0;	}
	
	public String toString () {
		StringBuffer buf		= new StringBuffer("DB2Columns=");
		buf.append				("[name="+name);
		buf.append				("],[tbname="+tbname);
		buf.append				("],[tbcreator="+tbcreator);
		buf.append				("],[colno="+colno);
		buf.append				("],[coltype="+coltype);
		buf.append				("],[length="+length);
		buf.append				("],[length2="+length2);
		buf.append				("],[scale="+scale);
		buf.append				("],[nulls="+nulls);
		buf.append				("],[defaultx="+defaultx);
		buf.append				("],[keyseq="+keyseq+"]");
		return					buf.toString();
	}
}
