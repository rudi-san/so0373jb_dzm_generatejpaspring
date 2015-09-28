package de.kbs.SO0373JB.db2;

public class Db2Child {

	private String				table;
	private String 				var;
	private String				relname;

	public Db2Child (String table, String relname) {
		this.var		= table.toLowerCase();
		this.table		= Db2Table.upperCaseStart(var);
		this.relname	= relname;
	}
	public String getTable () 	{		return table;	}
	public String getVar () 	{		return var;		}
	public String getRelname () {		return relname;	}
}
