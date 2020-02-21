package de.kbs.so0373jb.model;

import java.awt.HeadlessException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

//import de.kbs.SO0373JB.dao.SysIndexes;
//import de.kbs.SO0373JB.dao.SysKeys;



import de.kbs.so0373jb.business.Parameter;
import de.kbs.so0373jb.common.constants.Constants;
import de.kbs.so0373jb.common.enums.ColType;
import de.kbs.so0373jb.common.enums.Dbms;
import de.kbs.so0373jb.common.util.Utils;
import de.kbs.so0373jb.dao.db2.Db2Connection;
import de.kbs.so0373jb.dao.db2.Db2Column;
import de.kbs.so0373jb.dao.db2.Db2Foreignkey;
import de.kbs.so0373jb.dao.db2.Db2Rel;
import de.kbs.so0373jb.dao.sqlserver.SqlServerColumn;
import de.kbs.so0373jb.dao.sqlserver.SqlServerConnection;
import de.kbs.so0373jb.dao.sqlserver.SqlServerForeignkey;
import de.kbs.so0373jb.dao.sqlserver.SqlServerRel;
import de.kbs.so1320jc.main.LoggingContainer;

public class Table {

	private String						origName;
	private String						varName;
	private String						ccName;
	private String						pkType;
	private boolean						generatedKey	= false;
	private ArrayList<Column> 			columnList		= new ArrayList<Column>();
	private ArrayList<Column> 			pkList			= new ArrayList<Column>();
	private ArrayList<Child>			fkChild			= new ArrayList<Child>();
	private ArrayList<Parent>			parentList		= new ArrayList<Parent>();
//	private ArrayList<Db2Index> 		indexList		= new ArrayList<Db2Index>();
	private Hashtable<String, Column>	colHash			= new Hashtable<String, Column>();
	private static Logger 				logger			= LoggingContainer.getLoggerInstance().getRootLogger();
	
	public Table	(String name) {
		this.origName		= name;
		this.ccName			= Utils.makeCamelCase(name);
		this.varName		= Utils.makeLowerCamelCase(name);
	}					

	
	/** Lesen der Systemtabellen für die Generierung
	 * @param creator
	 * @param tbname
	 * @param dbms 
	 * @return
	 * @throws SQLException 
	 * @throws HeadlessException 
	 */
	public static Table createTable (String creator, String tbname, Dbms dbms) throws HeadlessException, SQLException  {
		
		if (dbms==Dbms.DB2)
			return 				createDb2Table (creator, tbname);
		
		if (dbms==Dbms.SQL_SERVER)
			return 				createSqlServerTable(creator, tbname);

		return null;
	}
		
	/** Lesen der DB2-Systemtabellen für die Generierung
	 * @param creator
	 * @param tbname
	 * @return
	 * @throws SQLException 
	 * @throws HeadlessException 
	 */
	private static Table createDb2Table (String creator, String tbname) throws HeadlessException, SQLException  {
		
		creator						= creator.toUpperCase();
		tbname						= tbname.toUpperCase();
		
		if (!Db2Connection.existsTable(creator, tbname)) {
			JOptionPane.showMessageDialog	(null, "Tabelle \""+creator+"."+tbname+"\" existiert nicht");
			System.exit(0);
		}

		Table table				= new Table(tbname);
		
//		Lesen der SYSCOLUMNS, um die Attribute der Tabelle zu ermitteln
		ArrayList<Db2Column> list01= Db2Column.read(creator, tbname);	
		logger.info					("Anzahl Columns : " + list01.size());
		for (Db2Column col : list01) {
			if (!col.getName().startsWith(Constants.DB2_GENER)) {
				ColType type				= ColType.findTypeDb2(col.getColtype());
				if (type!=null) {
					Column column			= new Column	( col.getName()
																, col.getColno()
																, type
																, col.getLength()
																, col.getLength2()
																, col.getScale()
																, col.getNulls()
																, col.getDefault()
																, col.isKey());
		//			Wenn die Column Teil des PKs ist, wird die Information in pkList abgelegt
					if  (col.isKey()) 
						table.addPk					(column);
					else
		//				alle anderen Felder laufen in die columnList
						table.addColumn				(column);
					if  (col.getDefault().equalsIgnoreCase("I")||col.getDefault().equalsIgnoreCase("J"))
						table.setGeneratedKey		(true);
				}
			}
		}

//		die Parent-Verbindungen werden in der parentList abgelegt
		ArrayList<Db2Rel> list02	= Db2Rel.readParent(creator, tbname);
		logger.info					("Anzahl parents : " + list02.size());
		for (Db2Rel rel : list02) {
			String relname				= rel.getRelname();
			String reftbcreator			= rel.getReftbcreator();
			String reftbname			= rel.getReftbname();
			Parent parent			= new Parent(reftbname, relname);
			ArrayList<Db2Foreignkey> list03 
										= Db2Foreignkey.read(creator, tbname, relname);
			for (Db2Foreignkey key : list03) {
				int keyseq					= key.getColseq();
				ArrayList<Db2Column> list04 
											= Db2Column.read(reftbcreator, reftbname, keyseq);
				parent.addColumns			(key.getColname(), list04.get(0).getName());
			}
			table.addParent				(parent);
		}
			
		ArrayList<Db2Rel> list05		= Db2Rel.readChild(creator, tbname);	
		logger.info						("Anzahl Childs : " + list05.size());
		for (Db2Rel rel : list05) {
			Child child					= new Child(rel.getTbname(), rel.getRelname());
			table.addFkChild				(child);
		}

//		ArrayList<SysIndexes> list06	= SysIndexes.read(creator, tbname);	
//		logger.info						("Anzahl Index : " + list06.size());
//		for (SysIndexes index : list06) {
//			String indexName				= index.getName();
//			String indexCreator				= index.getCreator();
//			String uniqueRule				= index.getUniquerule();
//			Db2Index db2Index				= new Db2Index(indexName, uniqueRule);
//			ArrayList<Db2Keys> list07		= Db2Keys.read(indexCreator, indexName);	
//			for (Db2Keys keys : list07) 
//				db2Index.addColumn 				(keys.getColname());
//			table.addIndex					(db2Index);
//		}
		
		return 						table;
	}
	
	/** Lesen der SqlServer-Systemtabellen für die Generierung
	 * @param creator
	 * @param tbname
	 * @return
	 * @throws SQLException 
	 * @throws HeadlessException 
	 */
	private static Table createSqlServerTable (String creator, String tbname) throws HeadlessException, SQLException  {
		
		creator						= creator.toUpperCase();
		tbname						= tbname.toUpperCase();
		
		if (!SqlServerConnection.existsTable(creator, tbname)) {
			JOptionPane.showMessageDialog	(null, "Tabelle \""+creator+"."+tbname+"\" existiert nicht");
			System.exit(0);
		}

		Table table				= new Table(tbname);
		
//		Lesen der SYSCOLUMNS, um die Attribute der Tabelle zu ermitteln
		ArrayList<SqlServerColumn> list01= SqlServerColumn.read(creator, tbname);	
		logger.info					("Anzahl Columns : " + list01.size());
		for (SqlServerColumn col : list01) {
			if (!col.getName().startsWith(Constants.DB2_GENER)) {
				ColType type				= ColType.findTypeDb2(col.getColtype());
				if (type!=null) {
					Column column			= new Column	( col.getName()
																, col.getColno()
																, type
																, col.getLength()
																, col.getLength()
																, col.getScale()
																, col.getNulls()
																, col.getDefault()
																, col.isKey());
		//			Wenn die Column Teil des PKs ist, wird die Information in pkList abgelegt
					if  (col.isKey()) 
						table.addPk					(column);
					else
		//				alle anderen Felder laufen in die columnList
						table.addColumn				(column);
					if  (col.getDefault().equalsIgnoreCase("I")||col.getDefault().equalsIgnoreCase("J"))
						table.setGeneratedKey		(true);
				}
			}
		}

//		die Parent-Verbindungen werden in der parentList abgelegt
		ArrayList<SqlServerRel> list02	= SqlServerRel.readParent(creator, tbname);
		logger.info					("Anzahl parents : " + list02.size());
		for (SqlServerRel rel : list02) {
			String relname				= rel.getRelname();
			String reftbcreator			= rel.getReftbcreator();
			String reftbname			= rel.getReftbname();
			Parent parent			= new Parent(reftbname, relname);
			ArrayList<SqlServerForeignkey> list03 
										= SqlServerForeignkey.read(creator, tbname, relname);
			for (SqlServerForeignkey key : list03) {
				int keyseq					= key.getColseq();
				ArrayList<SqlServerColumn> list04 
											= SqlServerColumn.read(reftbcreator, reftbname, keyseq);
				parent.addColumns			(key.getColname(), list04.get(0).getName());
			}
			table.addParent				(parent);
		}
			
		ArrayList<SqlServerRel> list05		= SqlServerRel.readChild(creator, tbname);	
		logger.info						("Anzahl Childs : " + list05.size());
		for (SqlServerRel rel : list05) {
			Child child					= new Child(rel.getTbname(), rel.getRelname());
			table.addFkChild				(child);
		}
		
		return 						table;
	}
	
	public Column[] getPk () {
		Column[] col			= new Column[pkList.size()];
		return					pkList.toArray(col);
	}
	
	public boolean isPk (String col) {
		boolean ret 	= false;
		for (Column pk : getPk())
			if (pk.getName().equalsIgnoreCase(col))
				ret		= true;
		return 			ret;
		
	}

	public void setPkType (String type) {
		pkType				= type;
	}
	
	public String getPkType () {
		return				pkType;
	}
	
	public boolean isDoubleChild (Child thisChild) {
		boolean ret 	= false;
		for (Child thatChild : fkChild)
			if (!(thisChild==thatChild))
				if (thisChild.getTable().equalsIgnoreCase(thatChild.getTable()))
					ret		= true;
		return 			ret;
	}

	public boolean isDoubleParent (Parent thisParent) {
		boolean ret 	= false;
		for (Parent thatParent : parentList)
			if (!(thisParent==thatParent))
				if (thisParent.getVarName().equalsIgnoreCase(thatParent.getVarName()))
					ret		= true;
		return 			ret;
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
	
	public Column[] getColumnsOhneParent () {
		ArrayList<Column> list = new ArrayList<Column>();
		for (Column col : columnList) {
			if (!isParent(col.getName())) 
				list.add		(col);
		}
		Column[] col			= new Column[list.size()];
		return					list.toArray(col);
	}
	
	public Parameter[] getColumnsAsParameter () {
		ArrayList<Parameter> para		= new ArrayList<Parameter>();
		if (!isGeneratedKey()) {
			Column[] pk			= getPk();
			if (pk.length>1) 
				para.add			(new Parameter("id", ccName+"Pk"));
			else
				para.add			(new Parameter(pk[0].getVarName(), pk[0].getColtype().getJavaString()));
		}
		for (Column col : getColumnsOhneParent()) 
			para.add			(new Parameter(col.getVarName(), col.getColtype().getJavaString()));
		for (Parent parent : parentList) {
			if (!parent.isComment()) {
				String parentName  = (isDoubleParent(parent)) ? parent.getRelname() : parent.getCcName();
				para.add			(new Parameter(Utils.makeLowerCamelCase(parentName), parent.getCcName()));
			}
		}
		Parameter[] ret			= new Parameter[para.size()];
		return 					para.toArray(ret);
	}
	
	public Child[] getFkChild () {
		Child[] fk			= new Child[fkChild.size()];
		return					fkChild.toArray(fk);
	}
	
	public Parent[] getParent () {
		Parent[] parent		= new Parent[parentList.size()];
		return					parentList.toArray(parent);
	}
	
	public boolean isGeneratedKey() {
		return generatedKey;
	}

	private void addColumn	(Column column) {
		columnList.add		(column);
		colHash.put			(column.getName(), column);
	}
	
	private void addPk	(Column column) {
		column.setPk		();
		pkList.add			(column);
		colHash.put			(column.getName(), column);
	}
	
	private void addParent	(Parent parent) {
		parentList.add		(parent);
	}
	
	private void addFkChild	(Child table) {
		fkChild.add			(table);
	}

	private boolean isParent (String col) {
		boolean ret 	= false;
		for (Parent parent : parentList)
			for (String[] parentCol : parent.getColumns())
				if (parentCol[0].equalsIgnoreCase(col))
					ret		= true;
		return 			ret;
		
	}
	
	private void setGeneratedKey(boolean generatedKey) {
		this.generatedKey = generatedKey;
	}
}


