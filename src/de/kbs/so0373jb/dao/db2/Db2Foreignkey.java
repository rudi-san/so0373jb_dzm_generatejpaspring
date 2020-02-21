package de.kbs.so0373jb.dao.db2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Db2Foreignkey {

	private String 	creator;
	private String 	tbname;
	private String 	colname;
	private int		colseq;
	private String	relname;
	
	public Db2Foreignkey (String creator, String tbname, String relname, String colname, int colseq) {
		this.creator		= creator;
		this.tbname			= tbname;
		this.colname		= colname;
		this.colseq			= colseq;
		this.relname		= relname;
	}
	
	public static ArrayList<Db2Foreignkey> read (String tbcreator, String tbname, String relname) {
		String sql 		= "select COLNAME, COLSEQ"
						+ " from SYSIBM.SYSFOREIGNKEYS"
						+ " where CREATOR = ? and TBNAME = ? and RELNAME = ?"
						+ " order by COLNO";
		ArrayList<Db2Foreignkey> list		= new ArrayList<Db2Foreignkey>();
		try {
			PreparedStatement stmt 			= Db2Connection.getStatement(sql);
			stmt.setString					(1, tbcreator);
			stmt.setString					(2, tbname);
			stmt.setString					(3, relname);
			ResultSet rs					= stmt.executeQuery();
			while (rs.next()) {
				list.add (new Db2Foreignkey	( tbcreator
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
