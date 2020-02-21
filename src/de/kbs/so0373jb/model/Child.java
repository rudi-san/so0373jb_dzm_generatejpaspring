package de.kbs.so0373jb.model;

import de.kbs.so0373jb.common.util.Utils;

public class Child {

	private String				table;
	private String 				var;
	private String				relname;

	public Child (String table, String relname) {
		this.var		= table.toLowerCase();
		this.table		= Utils.upperCaseStart(var);
		this.relname	= relname;
	}
	public String getTable () 	{		return table;	}
	public String getVar () 	{		return var;		}
	public String getRelname () {		return relname;	}
}
