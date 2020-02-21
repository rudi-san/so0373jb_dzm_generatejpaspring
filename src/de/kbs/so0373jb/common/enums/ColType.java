package de.kbs.so0373jb.common.enums;

public enum ColType {

	TYPE_DATE, 		TYPE_TIME, 		TYPE_TIMESTMP, 		TYPE_SMALLINT,	TYPE_INTEGER, 
	TYPE_BIGINT, 	TYPE_FLOAT, 	TYPE_DOUBLE,     	TYPE_DECIMAL, 	TYPE_CHAR,
	TYPE_VARCHAR, 	TYPE_XML, 		TYPE_BLOB;
	
	private static String[] db2 = 		{ "DATE", 		"TIME", 	"TIMESTMP",  	"SMALLINT", 	"INTEGER"
										, "BIGINT", 	"FLOAT",  	"DOUBLE", 		"DECIMAL",    	"CHAR"
										, "VARCHAR",	"XML",		"BLOB" };
	private static String[] sqlServer = { "datetime",	"datetime", "timestamp",  	"smallint", 	"int"
										, "bigint", 	"real",  	"float", 		"decimal",    	"char"
										, "varchar",	"text",		"image" };
	private static String[] java = 		{ "Date", 		"Time", 	"Timestamp", 	"Short",    	"Integer"
										, "Long",   	"Double", 	"Double", 		"BigDecimal", 	"String"
										, "String", 	"String",	"byte[]" };
	
	public static ColType findTypeDb2 (String search) {
		search			= search.trim();
		for (int i=0;i<db2.length;i++) {
			if (search.equalsIgnoreCase(db2[i]))
				return ColType.values()[i];
		}
//		System.out.println(search);
		return null;
	}
	
	public static ColType findTypeSqlServer (String search) {
		search			= search.trim();
		for (int i=0;i<sqlServer.length;i++) {
			if (search.equalsIgnoreCase(sqlServer[i]))
				return ColType.values()[i];
		}
//		System.out.println(search);
		return null;
	}
	
	public boolean isNumeric () {
		if (ordinal()>2&&ordinal()<8)
			return true;
		else
			return false;
	}
		
	public String getJavaString () {
		return java[ordinal()];
	}

}
