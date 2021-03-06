package de.kbs.so0373jb.dao.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class SqlServerForeignkey {

	private String 	creator;
	private String 	tbname;
	private String 	colname;
	private int		colseq;
	private String	relname;
	
	public SqlServerForeignkey (String creator, String tbname, String relname, String colname, int colseq) {
		this.creator		= creator;
		this.tbname			= tbname;
		this.colname		= colname;
		this.colseq			= colseq;
		this.relname		= relname;
	}
	
	public static ArrayList<SqlServerForeignkey> read (String tbcreator, String tbname, String relname) {
		String sql 		= "select 	COLUMN_NAME, ORDINAL_POSITION          "
						+ "from 	INFORMATION_SCHEMA.KEY_COLUMN_USAGE    "
						+ "where 	TABLE_SCHEMA = ?                       "
						+ "  and 	TABLE_NAME = ?                         "
						+ "  and 	CONSTRAINT_NAME = ?                    "
						+ "order by ORDINAL_POSITION                       ";
		ArrayList<SqlServerForeignkey> list		= new ArrayList<SqlServerForeignkey>();
		try {
			PreparedStatement stmt 			= SqlServerConnection.getStatement(sql);
			stmt.setString					(1, tbcreator);
			stmt.setString					(2, tbname);
			stmt.setString					(3, relname);
			ResultSet rs					= stmt.executeQuery();
			while (rs.next()) {
				list.add (new SqlServerForeignkey	( tbcreator
											, tbname
											, relname
											, rs.getString(1)
											, rs.getInt(2)));
			}
			return 							list;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog	(null, e.getMessage());
			System.exit						(0);
		}
		return null;
	}

	public String getCreator() 		{	return creator;		}
	public String getTbname() 		{	return tbname;		}
	public String getColname() 		{	return colname;		}
	public int getColseq() 			{	return colseq;		}
	public String getRelname() 		{	return relname;		}

}
