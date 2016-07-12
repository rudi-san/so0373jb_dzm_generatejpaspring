package de.kbs.SO0373JB.business;

import org.apache.log4j.Logger;

import de.kbs.SO0373JB.common.config.Configuration;
import de.kbs.SO0373JB.common.enums.DB2Type;
import de.kbs.SO0373JB.common.enums.Visibility;
import de.kbs.SO0373JB.db2.Db2Child;
import de.kbs.SO0373JB.db2.Db2Column;
import de.kbs.SO0373JB.db2.Db2Parent;
import de.kbs.SO0373JB.db2.Db2Table;
import de.kbs.SO0373JB.main.Main;
import de.kbs.so1320jc.main.LoggingContainer;

/** Klasse für die Generierung einer Java-Klasse für die JPA-Spring-Verarbeitung.
 * 
 * @author rschneid
 *
 */
public class FXClazz extends Clazz {

	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();

	/**
	 * @param table
	 */
	public FXClazz (Db2Table table) {
		super					( Configuration.getConfiguration().getPackage()+".jpa"
								, table.getCcName()
								, Visibility.visibility_public);
		
		logger.info				("Start der Verarbeitung der Tabelle "+table.getOrigName());
		
		addImports				(this, table);

//		CamelCase Name
		String ccName			= table.getCcName();

//		JPA-Annotations
		addAnnot				( "@Entity", "@Table(name=\""+table.getOrigName()+"\")");

//		Interfaces
		addInterface			( "Serializable" );  
		
//		Variable      serialVersionUID
		Variable variable		= new Variable("serialVersionUID", Visibility.visibility_private, "long", "1L");
		variable.setFinalStatic	();
		addVariable				(variable);

//		Klarschrift der columns (nur convenience, für JPA oder Spring nicht erforderlich)
		for (Db2Column col : table.getColumnsOhneParent()) {
			Variable var			= new Variable(col.getVarName().toUpperCase()
									, Visibility.visibility_public, "String", "\""+col.getVarName()+"\"");
			var.setFinalStatic		();
			addVariable				(var);
		}
		
//      KONSTRUKTOREN
//		Default Konstruktor
		addMethod				(new Method 	( ccName, Visibility.visibility_public, ""));
//		Komplett Konstruktor
		Method complConstructor	= new Method 	( ccName, Visibility.visibility_public, "", table.getColumnsAsParameter()); 
		if (!table.isGeneratedKey()) {
			Db2Column[] pk			= table.getPk();
			if (pk.length>1) 
				complConstructor.addSkeleton("SKEL002", "id");
			else
				complConstructor.addSkeleton("SKEL002", pk[0].getVarName());
		}
		for (Db2Column col : table.getColumnsOhneParent()) {	
			String varName2					= col.getVarName();
			String varName1                 = varName2.substring(0, 1).toUpperCase()+varName2.substring(1);
			complConstructor.addSkeleton("SKEL101", varName1, varName2);
		}
		for (Db2Parent parent : table.getParent())	{
			if (!parent.isComment()) {
				String type = (table.isDoubleParent(parent)) ? parent.getRelname() :  parent.getCcName(); 		
				complConstructor.addSkeleton("SKEL002", Db2Table.makeLowerCamelCase(type));
			}
		}
		addMethod				(complConstructor);
//      Ende Komplett-Konstruktor
		
//		PK einfügen
		Db2Column[] pkCol		= table.getPk();
		String pkName			= "";
		String pkVarName		= "";	
		if  (pkCol.length>0) {
//		Wenn der PK mehr als ein Attribut umfasst, muss eine zusätzliche Klasse "<Tablename>Pk" gebildet werden
			if  (pkCol.length>1) {
				pkName					= ccName+"Pk";
				table.setPkType			(pkName);
				pkVarName				= "id";
				Variable pkVar			= new Variable(pkVarName, Visibility.visibility_private, pkName, null);
				pkVar.addAnnot			("@EmbeddedId");
				addVariable				(pkVar);
				Main.writeFile			(Configuration.getConfiguration().getJpaPath(), pkName, addPkClass	(pkName, pkCol));
				createGetAndSet			(null, false, pkName, pkVarName, this, false);
			}
			else {
				pkName					= pkCol[0].getCamelCase();
				pkVarName				= pkCol[0].getVarName();
				addVariable				(pkCol[0], this, true);
				table.setPkType			(pkCol[0].getColtype().getJavaString());
			}
			Variable var			= new Variable(pkVarName.toUpperCase()
												, Visibility.visibility_public, "String", "\""+pkVarName+"\"");
			var.setFinalStatic		();
			addVariable				(var);
		}


//      Columns hinzufügen		
		for (Db2Column col : table.getColumnsOhneParent()) {
			addVariable				(col, this, false);
			Method propertyGet		= new Method(col.getVarName()+"Property", col.getColtype().getJavaFxString());
			propertyGet.addSkeleton	("SKEL001", col.getVarName());
			addMethod				(propertyGet);
		}
//		die Parent-Verbindungen hinzufügen
		for (Db2Parent parent : table.getParent()) {
			boolean doubleParent 	= table.isDoubleParent(parent);
			String type				= parent.getCcName();
			String paName			= (doubleParent) ? parent.getRelname() : type;
			paName					= Db2Table.makeLowerCamelCase(paName);
			Variable paVar			= new Variable	( paName
													, Visibility.visibility_private
													, type
													, null);
//			paVar.setComment		(parent.isComment());
			addVariable				(paVar);
			
			Method getMethod		= new Method	( "get"+Db2Table.upperCaseStart(paName), type);
			getMethod.addAnnot		("@ManyToOne");
			getMethod.addAnnot		("@JoinColumns({");
			boolean start			= true;
			for (String[] col : parent.getColumns()) {
				String annot			=   "@JoinColumn(name=\""		+
											col[0]						+
											"\", referencedColumnName=\"" +
											col[1]						+
											"\"";
				if (table.isPk(col[0]))
					annot					= annot+", insertable=false, updatable=false)";
				else
					annot					= annot+")";
				if (start) {
					start					= false;
					getMethod.addAnnot		("\t "+annot);
				}
				else
					getMethod.addAnnot		("\t,"+annot);
			}
			getMethod.addAnnot		("\t})");
			getMethod.addSkeleton	("SKEL001", "this."+paName);
			getMethod.setComment	(parent.isComment());
			addMethod				(getMethod);
			
			Method setMethod		= new Method	( "set"+Db2Table.upperCaseStart(paName)
													, "void"
													, new Parameter(paName, type)
													);
			setMethod.addSkeleton	("SKEL002", paName);
			setMethod.setComment	(parent.isComment());
			addMethod				(setMethod);

//			createGetAndSet			(null, false, type, paName, this, parent.isComment());
		}
		
//		die Lists für die Childs hinzufügen
		for (Db2Child fkChild :  table.getFkChild()) {
			boolean doubleChild 	= table.isDoubleChild(fkChild);
			String fkTable			= fkChild.getTable();
			String type				= "List<"+fkTable+">";
			String chName1			= (doubleChild) ? Db2Table.makeLowerCamelCase(fkChild.getRelname()) : fkChild.getVar();
			String chName2			= chName1+"s";
			Variable fkVar			= new Variable	( chName2
													, Visibility.visibility_private
													, type
													, null);
			boolean comment			= !Main.tableExists(fkChild.getTable());
			fkVar.setComment		(comment);
			addVariable				(fkVar);
			Method getMethod		= new Method	( "get"+Db2Table.upperCaseStart(chName2), type);
			String mapName			= (doubleChild) ? chName1 : table.getVarName();
			getMethod.addAnnot		("@OneToMany(mappedBy=\""+mapName+"\")");
			getMethod.addSkeleton	("SKEL001", "this."+chName2);
			getMethod.setComment	(comment);
			addMethod				(getMethod);
			Method setMethod		= new Method("set"+Db2Table.upperCaseStart(chName2), "void", new Parameter(chName2, type));
			setMethod.addSkeleton	("SKEL002", chName2);
			setMethod.setComment	(comment);
			addMethod				(setMethod);
//			createGetAndSet			(null, false, type, chName2, this, comment);
			if  (comment)
				complConstructor.addSkeleton	("SKEL003", "//", chName2, fkTable);				
			else
				complConstructor.addSkeleton	("SKEL003", "", chName2, fkTable);
			Method fkAddMethod		= new Method( "add"+Db2Table.upperCaseStart(chName1)
												, "void"
												, new Parameter(fkChild.getVar(), fkTable));
			fkAddMethod.addSkeleton	("SKEL004", chName2, "add", fkChild.getVar());
			if (comment) 			fkAddMethod.setComment();
			addMethod				(fkAddMethod);
			Method fkRemoveMethod	= new Method( "remove"+Db2Table.upperCaseStart(chName1)
												, "void"
												, new Parameter(fkChild.getVar(), fkTable));
			fkRemoveMethod.addSkeleton	
									("SKEL004", chName2, "remove", fkChild.getVar());
			if (comment) 			fkRemoveMethod.setComment();
			addMethod				(fkRemoveMethod);
		}

//		Beans-Methode "hashCode"
		Method hashMethod		= new Method	( "hashCode", "int");
		hashMethod.addSkeleton	("SKEL030", "1");
		if 	(pkCol.length==1) {
			if  (pkCol[0].getColtype().isNumeric())
				hashMethod.addSkeleton("SKEL031", pkCol[0].getVarName());
			else
				hashMethod.addSkeleton	("SKEL032", pkCol[0].getVarName());
		}
		else
			hashMethod.addSkeleton	("SKEL032", "id");
		hashMethod.addSkeleton	("SKEL033");
		hashMethod.addAnnot		("@Override");
		addMethod				(hashMethod);
//		Beans-Methode "equals"
		Method equalsMethod		= new Method	( "equals"
												, "boolean"
												, new Parameter("obj", "Object")
												);
		String equalsPk				= "id";
		if	(pkCol.length==1) 
			equalsPk					= pkCol[0].getVarName();
		
		equalsMethod.addSkeleton	("SKEL034", ccName, equalsPk);
		if  (pkCol.length==1&&pkCol[0].getColtype().isNumeric()) 
			equalsMethod.addSkeleton	("SKEL036", ccName, equalsPk);			
		else	
			equalsMethod.addSkeleton	("SKEL035", ccName, equalsPk);
		equalsMethod.addAnnot		("@Override");
		addMethod					(equalsMethod);
		
//		Die "toString()"-Methode, um eine Zeile insgesamt auszugeben (z. B. im Test)		
		Method toStringMethod		= new Method	("toString", "String");
		toStringMethod.addSkeleton	("SKEL005");
		Db2Column[] pk				= table.getPk();
		if (pk.length>1) 
			toStringMethod.addSkeleton	("SKEL006", ccName+"Pk", "id");
		else
			toStringMethod.addSkeleton	("SKEL006", pk[0].getName(), pk[0].getVarName());
		for (Db2Column col : table.getColumnsOhneParent())	
			toStringMethod.addSkeleton("SKEL006", col.getName(), "get"+col.getCamelCase()+"()");
		toStringMethod.addSkeleton	("SKEL007");
		addMethod					(toStringMethod);

	}

	private static void addImports  (Clazz clazz, Db2Table table) {
		clazz.addImport				( "javax.persistence.*"
									, "java.io.Serializable");
		if (table.getFkChild().length>0) {
			clazz.addImport				( "java.util.List"
					     				, "java.util.ArrayList");			
		}
		boolean containsString		= false;
		boolean containsInteger		= false;
		boolean containsLong		= false;
		boolean containsDouble		= false;
		boolean containsDec			= false;
		boolean containsDate		= false;
		boolean containsTime		= false;		
		boolean containsTimestamp	= false;
		for (Db2Column c : table.getColumnsOhneParent()) {
			if (c.getColtype()==DB2Type.db2type_bigint)			containsLong		= true;
			if (c.getColtype()==DB2Type.db2type_char)			containsString		= true;
			if (c.getColtype()==DB2Type.db2type_date)       	containsDate		= true;
			if (c.getColtype()==DB2Type.db2type_decimal)		containsDec			= true;
			if (c.getColtype()==DB2Type.db2type_double)			containsDouble		= true;
			if (c.getColtype()==DB2Type.db2type_float)			containsDouble		= true;
			if (c.getColtype()==DB2Type.db2type_integer)		containsInteger		= true;
			if (c.getColtype()==DB2Type.db2type_smallint)		containsInteger		= true;
			if (c.getColtype()==DB2Type.db2type_time)			containsTime		= true;
			if (c.getColtype()==DB2Type.db2type_timestmp)		containsTimestamp	= true;
			if (c.getColtype()==DB2Type.db2type_varchar)		containsString		= true;
		}
		for (Db2Column c : table.getPk()) {
			if (c.getColtype()==DB2Type.db2type_date)         	containsDate		= true;
			if (c.getColtype()==DB2Type.db2type_time)			containsTime		= true;
			if (c.getColtype()==DB2Type.db2type_timestmp)		containsTimestamp	= true;
		}
		if  (containsDate||containsTime||containsTimestamp||containsString) {
			clazz.addImport				( "javafx.beans.property.StringProperty"
										, "javafx.beans.property.SimpleStringProperty");
		}
		if  (containsInteger) {
			clazz.addImport				( "javafx.beans.property.IntegerProperty"
										, "javafx.beans.property.SimpleIntegerProperty");
		}
		if  (containsLong) {
			clazz.addImport				( "javafx.beans.property.LongProperty"
										, "javafx.beans.property.SimpleLongProperty");
		}
		if  (containsDouble) {
			clazz.addImport				( "javafx.beans.property.DoubleProperty"
					   					, "javafx.beans.property.SimpleDoubleProperty");
		}
		if (containsDec)			
			clazz.addImport				( "java.math.BigDecimal");
		if (containsDate) {
			clazz.addImport				( "java.util.Date"
										, "java.text.DateFormat"
										, "java.text.ParseException");
		}
		if (containsTime)			
			clazz.addImport				( "java.sql.Time");
		if (containsTimestamp)		
			clazz.addImport				( "java.sql.Timestamp");
	}
	
	private String addPkClass	(String className, Db2Column[] columns) {
		Clazz pkClass			= new Clazz( Configuration.getConfiguration().getPackage()+".jpa"
								, className
								, Visibility.visibility_public
								, "java.io.Serializable"	
								, "javax.persistence.*");
		pkClass.addAnnot		( "@Embeddable");
		pkClass.addInterface	( "Serializable" ) ; //, "Comparable<"+tbna+">");
		boolean containsDec			= false;
		boolean containsDate		= false;
		boolean containsTime		= false;		
		boolean containsTimestamp	= false;
		for (Db2Column c : columns) {
			if (c.getColtype()==DB2Type.db2type_decimal)		containsDec			= true;
			if (c.getColtype()==DB2Type.db2type_date)			containsDate		= true;
			if (c.getColtype()==DB2Type.db2type_time)			containsTime		= true;
			if (c.getColtype()==DB2Type.db2type_timestmp)		containsTimestamp	= true;
		}
		if (containsDec)			pkClass.addImport				("java.math.BigDecimal");
		if (containsDate)			pkClass.addImport				("java.util.Date");
		if (containsTime)			pkClass.addImport				("java.sql.Time");
		if (containsTimestamp)		pkClass.addImport				("java.sql.Timestamp");

		Variable variable		= new Variable("serialVersionUID", Visibility.visibility_private, "long", "1L");
		variable.setFinalStatic	();
		pkClass.addVariable		(variable);
		for (Db2Column col : columns) 
			addVariable				(col, pkClass, true);
//		Standard Konstruktor
		pkClass.addMethod		(new Method 	( className, Visibility.visibility_public, ""));
//		Komplett Konstruktor
		Parameter[] para		= new Parameter[columns.length];
		for (int i=0;i<para.length;i++) 
			para[i]					= new Parameter(columns[i].getVarName(), columns[i].getColtype().getJavaString());
		Method complConstructor	= new Method 	( className, Visibility.visibility_public, "", para); 
		for (Db2Column col : columns)	
			complConstructor.addSkeleton("SKEL002", col.getVarName());
		pkClass.addMethod			(complConstructor);

//		Beans-Methode "hashCode"
		Method hashMethod		= new Method	( "hashCode"
												, "int"
												);
		hashMethod.addSkeleton	("SKEL030", "17");
		for (Db2Column col : columns) {
			if  (col.getColtype().isNumeric())
				hashMethod.addSkeleton("SKEL031", col.getVarName());
			else
				hashMethod.addSkeleton	("SKEL032", col.getVarName());
		}
		hashMethod.addSkeleton	("SKEL033");
		hashMethod.addAnnot		("@Override");
		pkClass.addMethod		(hashMethod);
//		Beans-Methode "equals"
		Method equalsMethod		= new Method	( "equals"
												, "boolean"
												, new Parameter("other", "Object")
												);
		equalsMethod.addSkeleton("SKEL037", className);
		boolean start			= true;
		for (Db2Column col : columns) {
			if (start) {
				if  (col.getColtype().isNumeric())
					equalsMethod.addSkeleton	("SKEL038A", "", col.getVarName());
				else
					equalsMethod.addSkeleton	("SKEL038", "", col.getVarName());
				start					= false;
			}
			else
				if  (col.getColtype().isNumeric())
					equalsMethod.addSkeleton	("SKEL038A", "&&", col.getVarName());
				else
				equalsMethod.addSkeleton	("SKEL038", "&&", col.getVarName());
		}
		equalsMethod.addSkeleton("SKEL039");
		equalsMethod.addAnnot	("@Override");
		pkClass.addMethod		(equalsMethod);
		
//		Die "toString()"-Methode, um eine Zeile insgesamt auszugeben (z. B. im Test)		
		Method toStringMethod		= new Method	("toString", "String");
		toStringMethod.addSkeleton	("SKEL005");
		for (Db2Column col : columns)	
			toStringMethod.addSkeleton	("SKEL006", col.getName(), col.getVarName());
		toStringMethod.addSkeleton	("SKEL007");
		pkClass.addMethod			(toStringMethod);

		return 					pkClass.toString();
	}
		
	public static void addVariable (Db2Column col, Clazz clazz, boolean isId) {
//		die verschiedenen Namen einlesen
		String varName			= col.getVarName();
//		Variablentyp ermitteln
		String type				= (isId) ? col.getColtype().getJavaString() : col.getColtype().getJavaFxString();
//		Variable anlegen
		String initType			= (isId) ? null : "new Simple"+type+"()";
		Variable var			= new Variable(varName, Visibility.visibility_private, type, initType);
		clazz.addVariable		(var);
//		get- und set-Methoden
		createGetAndSet			(col, isId, col.getColtype().getJavaString(), varName, clazz, false);
	}
	
	private static void createGetAndSet (Db2Column col, boolean isId, String type, String varName, Clazz clazz, boolean isComment) {
		Method getMethod		= new Method	( "get"+Db2Table.upperCaseStart(varName)
				, type
				);
		if  (col!=null) {
//		Annotations - ggf. ID
			if  (isId)
				getMethod.addAnnot				("@Id");
			if  (col.isGeneratedKey()) 
				getMethod.addAnnot				("@GeneratedValue");			
//		Annotation - bei Date-Attributen
			if (type.equalsIgnoreCase("Date"))
				getMethod.addAnnot("@Temporal( TemporalType.DATE)");
//		Annotations - Column
			if (type.equalsIgnoreCase("String"))
				getMethod.addAnnot			("@Column(name=\""+col.getName()+"\",columnDefinition=\"char["+col.getLength()+"]\")");
			else
				getMethod.addAnnot			("@Column(name=\""+col.getName()+"\")");
		}
		
		if (col==null || isId)
			getMethod.addSkeleton	("SKEL001", "this."+varName);
		else
		if (col.getColtype()==DB2Type.db2type_decimal)
			getMethod.addSkeleton	("SKEL001", "new BigDecimal(this."+varName+".get())");
		else
		if (col.getColtype()==DB2Type.db2type_smallint)
			getMethod.addSkeleton	("SKEL001", "new Short((short)this."+varName+".get())");
		else
		if (col.getColtype()==DB2Type.db2type_date)
			getMethod.addSkeleton	("SKEL103", varName);
		else
		if (col.getColtype()==DB2Type.db2type_time)
			getMethod.addSkeleton	("SKEL001", "Time.valueOf(this."+varName+".get())");
		else
		if (col.getColtype()==DB2Type.db2type_timestmp)
			getMethod.addSkeleton	("SKEL001", "Timestamp.valueOf(this."+varName+".get())");
		else
			getMethod.addSkeleton	("SKEL001", "this."+varName+".get()");
		
		getMethod.setFinal		();
		if (isComment) 			getMethod.setComment();
		clazz.addMethod			(getMethod);
		
		Method setMethod		= new Method	( "set"+Db2Table.upperCaseStart(varName)
												, "void"
												, new Parameter(varName, type)
												);
		if  (col==null || isId)
			setMethod.addSkeleton	("SKEL002", varName);
		else
		if  (col.getColtype()==DB2Type.db2type_date)
			setMethod.addSkeleton	("SKEL102", varName, "DateFormat.getDateInstance().format("+varName+")");
		else
		if  (col.getColtype()==DB2Type.db2type_decimal      || 
		     col.getColtype()==DB2Type.db2type_time			||
		     col.getColtype()==DB2Type.db2type_timestmp          )
			setMethod.addSkeleton	("SKEL102", varName, varName+".toString()");
		else
			setMethod.addSkeleton	("SKEL102", varName, varName);
		
		setMethod.setFinal		();
		if (isComment) 			setMethod.setComment();
		clazz.addMethod			(setMethod);
	}
}
