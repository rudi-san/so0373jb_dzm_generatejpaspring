package de.kbs.so0373jb.common.util;

public class Utils {

	public static String makeCamelCase (String name) {
		String splitName[]		= name.split("_");
		String camelCase		= "";
		for (String nName : splitName)
			camelCase 				= camelCase
									+ nName.substring(0,1).toUpperCase()
									+ nName.substring(1).toLowerCase();
		return 					camelCase;
	}

	public static String makeLowerCamelCase (String name) {
		String s		= makeCamelCase(name);
		return 			s.substring(0, 1).toLowerCase()+s.substring(1);
	}

	public static String upperCaseStart (String s) {
		return 			s.substring(0, 1).toUpperCase()+s.substring(1);
	}

}
