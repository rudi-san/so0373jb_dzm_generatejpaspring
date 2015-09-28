package de.kbs.SO0373JB.common.enums;

public enum DB2Type {

	db2type_date, 		db2type_time, 		db2type_timestmp, 	db2type_smallint,	db2type_integer, 
	db2type_bigint, 	db2type_float, 		db2type_double,     db2type_decimal, 	db2type_char,		db2type_varchar;
	
	private static String[] db2 = 	{ "DATE", "TIME", "TIMESTMP",  "SMALLINT", "INTEGER", "BIGINT", "FLOAT",  "DOUBLE", "DECIMAL",    "CHAR",   "VARCHAR" };
	private static String[] java = 	{ "Date", "Time", "Timestamp", "Short",    "Integer", "Long",   "Double", "Double", "BigDecimal", "String", "String" };
	
	public static DB2Type findType (String search) {
		search			= search.trim();
		for (int i=0;i<db2.length;i++) {
			if (search.equalsIgnoreCase(db2[i]))
				return DB2Type.values()[i];
		}
		System.out.println(search);
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
