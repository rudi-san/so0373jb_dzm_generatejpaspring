package de.kbs.so0373jb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SysColumns {

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
	
	public SysColumns (String name, String tbname, String tbcreator, int colno, String coltype,
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
	
	public static ArrayList<SysColumns> read (String tbcreator, String tbname) {
		String sqlPlus 	= " where TBCREATOR = ? and TBNAME = ?";
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
	
	public static ArrayList<SysColumns> read (String tbcreator, String tbname, int keyseq) {
		String sqlPlus				= " where TBCREATOR = ? and TBNAME = ? and KEYSEQ = ?";
		try {
			PreparedStatement stmt 			= DB2Connection.getStatement(SQL+sqlPlus);
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
	
	private static ArrayList<SysColumns> processResultSet (ResultSet rs) throws SQLException {
		ArrayList<SysColumns> list		= new ArrayList<SysColumns>();
		while (rs.next()) {
			list.add (new SysColumns	( rs.getString(1)
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
}
