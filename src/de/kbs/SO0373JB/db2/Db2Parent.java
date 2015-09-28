package de.kbs.SO0373JB.db2;

import java.util.ArrayList;

import de.kbs.SO0373JB.main.Main;

public class Db2Parent {

	private String				varName;
	private String				ccName;
	private String				relname;
	private boolean				comment = true;
	private ArrayList<String[]>	columns	= new ArrayList<String[]>();
	
	public Db2Parent (String table, String relname) {
		this.varName	= Db2Table.makeLowerCamelCase(table);
		this.ccName		= Db2Table.makeCamelCase(table);
		this.relname	= relname;
		this.comment	= !Main.tableExists(table); 
	}
	
	public void addColumns (String child, String parent) {
		String[] str		= new String[2];
		str[0]				= child;
		str[1]				= parent;
		columns.add			(str);
	}
	
	public String getVarName () {
		return varName;
	}
	
	public String getCcName () {
		return ccName;
	}
	
	public String[][] getColumns () {
		String[][] str		= new String[columns.size()][2];
		return				columns.toArray(str);
	}
	
	public String getRelname () {
		return relname;
	}
	
	public boolean isComment() {
		return this.comment;
	}
}
