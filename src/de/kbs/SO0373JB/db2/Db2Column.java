package de.kbs.SO0373JB.db2;

import de.kbs.SO0373JB.common.enums.DB2Type;

public class Db2Column {
	
	private String name;
	private int colno;
	private DB2Type coltype;
	private int length;
	private int scale;
	private String nulls;
	private String deflt;
	private int keyseq;
	private String camelCase;
	private String varName;
	private boolean isPk	= false;
	
	public Db2Column (String name, int colno, DB2Type coltype, int length, int scale, String nulls, String deflt, int keyseq ) {
		this.name			= name;
		this.colno			= colno;
		this.coltype		= coltype;
		this.length			= length;
		this.scale			= scale;
		this.nulls			= nulls;
		this.deflt			= deflt;
		this.keyseq			= keyseq;
		camelCase			= Db2Table.makeCamelCase(name);
//      Variablenname = CamelCase-Name mit kleinem Anfangsbuchstaben
		varName				= Db2Table.makeLowerCamelCase(name);
	}

	public String getName() {
		return name;
	}

	public int getColno() {
		return colno;
	}

	public DB2Type getColtype() {
		return coltype;
	}

	public int getLength() {
		return length;
	}

	public int getScale() {
		return scale;
	}

	public String getNulls() {
		return nulls;
	}

	public String getDefault() {
		return deflt;
	}
	
	public String getCamelCase () {
		return camelCase;
	}
	
	public String getVarName () {
		return varName;
	}
	
	public boolean isGeneratedKey () {
		if  (keyseq>0 && ( deflt.equalsIgnoreCase("I") || deflt.equalsIgnoreCase("J") ) )
			return true;
		else
			return false;
	}
	
	public void setPk () {
		isPk		= true;
	}
	
	public boolean isPk () {
		return this.isPk;
	}
	
	public int getKeyseq() {
		return keyseq;
	}
}