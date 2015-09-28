package de.kbs.SO0373JB.db2;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import de.kbs.SO0373JB.business.Parameter;
import de.kbs.SO0373JB.common.enums.DB2Type;
import de.kbs.SO0373JB.dao.SysColumns;
import de.kbs.SO0373JB.dao.SysForeignkeys;
import de.kbs.SO0373JB.dao.SysIndexes;
import de.kbs.SO0373JB.dao.SysKeys;
import de.kbs.SO0373JB.dao.SysRels;
import de.kbs.so1320jc.main.LoggingContainer;

public class Db2Table {

	private String							origName;
	private String							varName;
	private String							ccName;
	private String							pkType;
	private boolean							generatedKey	= false;
	private ArrayList<Db2Column> 			columnList		= new ArrayList<Db2Column>();
	private ArrayList<Db2Column> 			pkList			= new ArrayList<Db2Column>();
	private ArrayList<Db2Child>				fkChild			= new ArrayList<Db2Child>();
	private ArrayList<Db2Parent>			parentList		= new ArrayList<Db2Parent>();
	private ArrayList<Db2Index> 			indexList		= new ArrayList<Db2Index>();
	private Hashtable<String, Db2Column>	colHash			= new Hashtable<String, Db2Column>();
	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();
	
	public Db2Table	(String name) {
		this.origName		= name;
		this.ccName			= makeCamelCase(name);
		this.varName		= makeLowerCamelCase(name);
	}					

	public static Db2Table createTable (String creator, String tbname)  {
		
		creator						= creator.toUpperCase();
		tbname						= tbname.toUpperCase();
//		String tbna					= tbname.substring(0,1)+tbname.substring(1).toLowerCase();

		Db2Table table				= new Db2Table(tbname);
		ArrayList<SysColumns> list01= SysColumns.read(creator, tbname);	
		logger.info					("Anzahl Columns : " + list01.size());
		for (SysColumns col : list01) {
			Db2Column column			= new Db2Column	( col.getName()
														, col.getColno()
														, DB2Type.findType(col.getColtype())
														, col.getLength()
														, col.getScale()
														, col.getNulls()
														, col.getDefault()
														, col.getKeyseq());
			if  (col.getKeyseq()>0) 
				table.addPk					(column);
			else
				table.addColumn				(column);
			if  (col.getDefault().equalsIgnoreCase("I")||col.getDefault().equalsIgnoreCase("J"))
				table.setGeneratedKey		(true);
		}

		ArrayList<SysRels> list02	= SysRels.readParent(creator, tbname);
		logger.info					("Anzahl parents : " + list02.size());
		for (SysRels rel : list02) {
			String relname				= rel.getRelname();
			String reftbcreator			= rel.getReftbcreator();
			String reftbname			= rel.getReftbname();
			Db2Parent parent			= new Db2Parent(reftbname, relname);
			ArrayList<SysForeignkeys> list03 
										= SysForeignkeys.read(creator, tbname, relname);
			for (SysForeignkeys key : list03) {
				int keyseq					= key.getColseq();
				ArrayList<SysColumns> list04 
											= SysColumns.read(reftbcreator, reftbname, keyseq);
				parent.addColumns			(key.getColname(), list04.get(0).getName());
			}
			table.addParent				(parent);
		}
			
		ArrayList<SysRels> list05		= SysRels.readChild(creator, tbname);	
		logger.info						("Anzahl Childs : " + list05.size());
		for (SysRels rel : list05) {
			Db2Child child					= new Db2Child(rel.getTbname(), rel.getRelname());
			table.addFkChild				(child);
		}

		ArrayList<SysIndexes> list06	= SysIndexes.read(creator, tbname);	
		logger.info						("Anzahl Index : " + list06.size());
		for (SysIndexes index : list06) {
			String indexName				= index.getName();
			String indexCreator				= index.getCreator();
			String uniqueRule				= index.getUniquerule();
			Db2Index db2Index				= new Db2Index(indexName, uniqueRule);
			ArrayList<SysKeys> list07		= SysKeys.read(indexCreator, indexName);	
			for (SysKeys keys : list07) 
				db2Index.addColumn 				(keys.getColname());
			table.addIndex					(db2Index);
		}
		
		return 						table;
	}
	
	
	public void addColumn	(Db2Column column) {
		columnList.add		(column);
		colHash.put			(column.getName(), column);
	}
	
	public void addPk	(Db2Column column) {
		column.setPk		();
		pkList.add			(column);
		colHash.put			(column.getName(), column);
	}
	
	public void setPkType (String type) {
		pkType				= type;
	}
	
	public String getPkType () {
		return				pkType;
	}
	
	public Db2Column getColumn	(String name) {
		return				colHash.get(name);
	}
	
	public void addFkChild	(Db2Child table) {
		fkChild.add			(table);
	}

	public boolean isDoubleChild (Db2Child thisChild) {
		boolean ret 	= false;
		for (Db2Child thatChild : fkChild)
			if (!(thisChild==thatChild))
				if (thisChild.getTable().equalsIgnoreCase(thatChild.getTable()))
					ret		= true;
		return 			ret;
	}

	public void addParent	(Db2Parent parent) {
		parentList.add		(parent);
	}
	
	public boolean isDoubleParent (Db2Parent thisParent) {
		boolean ret 	= false;
		for (Db2Parent thatParent : parentList)
			if (!(thisParent==thatParent))
				if (thisParent.getVarName().equalsIgnoreCase(thatParent.getVarName()))
					ret		= true;
		return 			ret;
	}
	
	public boolean isParent (String col) {
		boolean ret 	= false;
		for (Db2Parent parent : parentList)
			for (String[] parentCol : parent.getColumns())
				if (parentCol[0].equalsIgnoreCase(col))
					ret		= true;
		return 			ret;
		
	}
	
	public void addIndex	(Db2Index index) {
		indexList.add		(index);
	}
	
	public String getOrigName	() {
		return origName;
	}
	
	public String getCcName	() {
		return ccName;
	}
	
	public String getVarName	() {
		return varName;
	}
	
	public Db2Column[] getColumns () {
		ArrayList<Db2Column> list = new ArrayList<Db2Column>();
		for (Db2Column col : columnList) {
			if (!isParent(col.getName())) 
				list.add		(col);
		}
		Db2Column[] col			= new Db2Column[list.size()];
		return					list.toArray(col);
	}
	
	public Parameter[] getColumnsAsParameter () {
		ArrayList<Parameter> para		= new ArrayList<Parameter>();
		if (!isGeneratedKey()) {
			Db2Column[] pk			= getPk();
			if (pk.length>1) 
				para.add			(new Parameter("id", ccName+"Pk"));
			else
				para.add			(new Parameter(pk[0].getVarName(), pk[0].getColtype().getJavaString()));
		}
		for (Db2Column col : getColumns()) 
			para.add			(new Parameter(col.getVarName(), col.getColtype().getJavaString()));
		for (Db2Parent parent : parentList) {
			if (!parent.isComment()) {
				String parentName  = (isDoubleParent(parent)) ? parent.getRelname() : parent.getCcName();
				para.add			(new Parameter(Db2Table.makeLowerCamelCase(parentName), parent.getCcName()));
			}
		}
		Parameter[] ret			= new Parameter[para.size()];
		return 					para.toArray(ret);
	}
	
	public Db2Column[] getPk () {
		Db2Column[] col			= new Db2Column[pkList.size()];
		return					pkList.toArray(col);
	}
	
	public boolean isPk (String col) {
		boolean ret 	= false;
		for (Db2Column pk : getPk())
			if (pk.getName().equalsIgnoreCase(col))
				ret		= true;
		return 			ret;
		
	}

	public Db2Child[] getFkChild () {
		Db2Child[] fk			= new Db2Child[fkChild.size()];
		return					fkChild.toArray(fk);
	}
	
	public Db2Parent[] getParent () {
		Db2Parent[] parent		= new Db2Parent[parentList.size()];
		return					parentList.toArray(parent);
	}
	
	public Db2Index[] getIndex () {
		Db2Index[] index		= new Db2Index[indexList.size()];
		return					indexList.toArray(index);
	}
	
	public boolean isGeneratedKey() {
		return generatedKey;
	}

	public void setGeneratedKey(boolean generatedKey) {
		this.generatedKey = generatedKey;
	}

	
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
