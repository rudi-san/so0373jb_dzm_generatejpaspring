package de.kbs.SO0373JB.db2;

import java.util.ArrayList;

public class Db2Index {

	private String indexName;
	private String uniqueRule;
	private ArrayList<String> columns	= new ArrayList<String>();
	
	public Db2Index (String indexName, String uniqueRule) {
		this.indexName		= indexName;
		this.uniqueRule		= uniqueRule;
	}
	
	public void addColumn (String column) {
		columns.add			(column);
	}
	
	public String getIndexName () {
		return this.indexName;
	}
	
	public String getUniqueRule () {
		return this.uniqueRule;
	}
	
	public boolean isDuplicateIndex () {
		return this.uniqueRule.equalsIgnoreCase("D");
	}
	
	public String[] getColumns () {
		String[] col			= new String[columns.size()];
		return					columns.toArray(col);
	}
}
