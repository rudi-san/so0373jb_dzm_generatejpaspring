package de.kbs.so0373jb.model;

import de.kbs.so0373jb.common.enums.ColType;
import de.kbs.so0373jb.common.util.Utils;

public class Column {
	
	private String name;
	private int colno;
	private ColType coltype;
	private int length;
	private int length2;
	private int scale;
	private String nulls;
	private String deflt;
	private boolean isKey;
	private String camelCase;
	private String varName;
	private boolean isPk	= false;
	private boolean isGenerated;
	
	public Column (String name, int colno, ColType coltype, int length, int length2, int scale, String nulls, String deflt, boolean isKey, boolean isGenerated ) {
		this.name			= name;
		this.colno			= colno;
		this.coltype		= coltype;
		this.length			= length;
		this.length2		= length2;
		this.scale			= scale;
		this.nulls			= nulls;
		this.deflt			= deflt;
		this.isKey			= isKey;
		this.isGenerated	= isGenerated;
		camelCase			= Utils.makeCamelCase(name);
//      Variablenname = CamelCase-Name mit kleinem Anfangsbuchstaben
		varName				= Utils.makeLowerCamelCase(name);
	}

	public String getName() 		{	return name;		}
	public int getColno() 			{	return colno;		}	// nicht genutzt
	public ColType getColtype() 	{	return coltype;		}
	public int getLength() 			{	return length;		}
	public int getLength2() 		{	return length2;		}
	public int getScale() 			{	return scale;		}	// nicht genutzt
	public String getNulls() 		{	return nulls;		}	// nicht genutzt
	public String getDefault() 		{	return deflt;		}	// nicht genutzt
	public String getCamelCase () 	{	return camelCase;	}
	public String getVarName () 	{	return varName;		}

	public void setPk () 			{	isPk = true;		}
	public boolean isPk () 			{	return this.isPk;	}
	public boolean isGenerated ()	{	return isGenerated;	}
	
//	public boolean isGeneratedKey () {		
//		if  (isKey && ( deflt.equalsIgnoreCase("I") || deflt.equalsIgnoreCase("J") ) )
//			return true;
//		else
//			return false;
//	}
	
	
	
	public boolean isKey() {		return isKey;
	}
}
