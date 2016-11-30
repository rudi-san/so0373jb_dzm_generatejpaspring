package de.kbs.so0373jb.business;

import org.apache.log4j.Logger;

import de.kbs.so0373jb.common.config.Configuration;
import de.kbs.so0373jb.common.enums.Visibility;
import de.kbs.so0373jb.db2.Db2Child;
import de.kbs.so0373jb.db2.Db2Column;
import de.kbs.so0373jb.db2.Db2Parent;
import de.kbs.so0373jb.db2.Db2Table;
import de.kbs.so0373jb.main.Main;
import de.kbs.so1320jc.main.LoggingContainer;

/** Klasse für die Generierung einer Java-Klasse für die JPA-Verarbeitung.
 * 
 * @author rschneid
 *
 */
public class JPAClazz extends Clazz {

	private static Logger logger		= LoggingContainer.getLoggerInstance().getRootLogger();

	public JPAClazz (Db2Table table) {
		super					( Configuration.getConfiguration().getPackage()+".jpa"
								, table.getCcName()
								, Visibility.visibility_public
								, "javax.persistence.*"
								, "java.io.Serializable");
		logger.info				("Start der Verarbeitung der Tabelle "+table.getOrigName());
		if (table.getFkChild().length>0) {
			addImport				("java.util.List");			
			addImport				("java.util.ArrayList");			
		}
		boolean containsDec			= false;
		boolean containsDate		= false;
		boolean containsTime		= false;		
		boolean containsTimestamp	= false;
		for (Db2Column c : table.getColumnsOhneParent()) {
			if (c.getColtype().name().equals("db2type_decimal"))		containsDec			= true;
			if (c.getColtype().name().equals("db2type_date"))			containsDate		= true;
			if (c.getColtype().name().equals("db2type_time"))			containsTime		= true;
			if (c.getColtype().name().equals("db2type_timestmp"))		containsTimestamp	= true;
		}
		for (Db2Column c : table.getPk()) {
			if (c.getColtype().name().equals("db2type_decimal"))		containsDec			= true;
			if (c.getColtype().name().equals("db2type_date"))			containsDate		= true;
			if (c.getColtype().name().equals("db2type_time"))			containsTime		= true;
			if (c.getColtype().name().equals("db2type_timestmp"))		containsTimestamp	= true;
		}
		if (containsDec)			addImport				("java.math.BigDecimal");
		if (containsDate)			addImport				("java.util.Date");
		if (containsTime)			addImport				("java.sql.Time");
		if (containsTimestamp)		addImport				("java.sql.Timestamp");

//		Annotations
		addAnnot				( "@Entity", "@Table(name=\""+table.getOrigName()+"\")");
//		CamelCase Name
		String ccName			= table.getCcName();

//		Interfaces
		addInterface			( "Serializable" ); // , "Comparable<"+ccName+">");
//		Variable      serialVersionUID
		Variable variable		= new Variable("serialVersionUID", Visibility.visibility_private, "long", "1L");
		variable.setFinalStatic	();
		addVariable				(variable);

		for (Db2Column col : table.getColumnsOhneParent()) {
			Variable var			= new Variable(col.getVarName().toUpperCase()
									, Visibility.visibility_public, "String", "\""+col.getVarName()+"\"");
			var.setFinalStatic		();
			addVariable				(var);
		}
//	    Variablen für den Paged Read
//		Variable pageQuery		= new Variable("pageQuery", Visibility.visibility_private, "TypedQuery<"+ccName+">", "null");
//		pageQuery.setStatic		();
//		pageQuery.addAnnot		("@Transient");
//		addVariable				(pageQuery);
//		Variable pageCount		= new Variable("pageCount", Visibility.visibility_private, "int", "0");
//		pageCount.setStatic		();
//		pageCount.addAnnot		("@Transient");
//		addVariable				(pageCount);
		
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
		for (Db2Column col : table.getColumnsOhneParent())	
			complConstructor.addSkeleton("SKEL002", col.getVarName());
		for (Db2Parent parent : table.getParent())	{
			if (!parent.isComment()) {
				String type = (table.isDoubleParent(parent)) ? parent.getRelname() :  parent.getCcName(); 		
				complConstructor.addSkeleton("SKEL002", Db2Table.makeLowerCamelCase(type));
			}
		}
		addMethod				(complConstructor);

//		PK einfügen
		Db2Column[] pkCol		= table.getPk();
		String pkName			= "";
		String pkVarName		= "";	
		if  (pkCol.length>0) {
			if  (pkCol.length>1) {
				pkName					= ccName+"Pk";
				table.setPkType			(pkName);
				pkVarName				= "id";
				Variable pkVar			= new Variable(pkVarName, Visibility.visibility_private, pkName, null);
				pkVar.addAnnot			("@EmbeddedId");
				addVariable				(pkVar);
				Main.writeFile			(Configuration.getConfiguration().getJpaPath(), pkName, addPkClass	(pkName, pkCol));
				createGetAndSet			(pkName, pkVarName, this, false);
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
		for (Db2Column col : table.getColumnsOhneParent()) 
			addVariable				(col, this, false);
//		die Parent-Verbindungen hinzufügen
		for (Db2Parent parent : table.getParent()) {
			boolean doubleParent = table.isDoubleParent(parent);
			String type			= parent.getCcName();
			String paName		= (doubleParent) ? parent.getRelname() : type;
			paName				= Db2Table.makeLowerCamelCase(paName);
			Variable paVar		= new Variable	( paName
												, Visibility.visibility_private
												, type
												, null);
			paVar.addAnnot		("@ManyToOne");
			paVar.addAnnot		("@JoinColumns({");
			boolean start		= true;
			for (String[] col : parent.getColumns()) {
				String annot		=   "@JoinColumn(name=\""		+
										col[0]						+
										"\", referencedColumnName=\"" +
										col[1]						+
										"\"";
				if (table.isPk(col[0]))
					annot				= annot+", insertable=false, updatable=false)";
				else
					annot				= annot+")";
				if (start) {
					start				= false;
					paVar.addAnnot		("\t "+annot);
				}
				else
					paVar.addAnnot		("\t,"+annot);
			}
			paVar.addAnnot("\t})");
			paVar.setComment	(parent.isComment());
			addVariable			(paVar);
			createGetAndSet		(type, paName, this, parent.isComment());
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
			String mapName			= (doubleChild) ? chName1 : table.getVarName();
			fkVar.addAnnot			("@OneToMany(mappedBy=\""+mapName+"\")");
			boolean comment			= !Main.tableExists(fkChild.getTable());
			fkVar.setComment		(comment);
			addVariable				(fkVar);
			createGetAndSet			(type, chName2, this, comment);
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
			toStringMethod.addSkeleton("SKEL006", col.getName(), col.getVarName());
		toStringMethod.addSkeleton	("SKEL007");
		addMethod					(toStringMethod);

//		Jetzt kommen die ganzen statischen Methoden zum Zugriff auf die Daten
//		1. Persist
//		addTransactMethods			("persist", table.getVarName(), ccName);
//		2. Remove
//		addTransactMethods			("remove", table.getVarName(), ccName);

//		3. GetUnique
//		Parameter[] guPara			= new Parameter[pkCol.length];
//		for (int i=0;i<pkCol.length;i++) 
//			guPara[i]					= new Parameter(pkCol[i].getVarName(), pkCol[i].getColtype().getJavaString());
//		Method guMethod				= new Method("getUnique", ccName, guPara);
//		guMethod.setStatic	 		();
//		guMethod.addThrow			("JPAException");
//		if  (pkCol.length==1) 
//			guMethod.addSkeleton		("SKEL012A", ccName, pkCol[0].getVarName());
//		else {
//			guMethod.addSkeleton		("SKEL012", pkName);
//			for (Db2Column pkC : pkCol) 
//				guMethod.addSkeleton 		("SKEL013", pkC.getCamelCase(), pkC.getVarName());
//			guMethod.addSkeleton 		("SKEL014", ccName);
//		}
//		addMethod					(guMethod);
//		4. ExistsUnique
//		Parameter[] euPara			= new Parameter[pkCol.length];
//		for (int i=0;i<pkCol.length;i++) 
//			euPara[i]					= new Parameter(pkCol[i].getVarName(), pkCol[i].getColtype().getJavaString());
//		Method euMethod				= new Method("existsUnique", "boolean", euPara);
//		euMethod.setStatic	 		();
//		euMethod.addThrow			("JPAException");
//		euMethod.addSkeleton		("SKEL015", ccName);
//		boolean start				= true;
//		for (int i=0;i<pkCol.length;i++) {
//			if (start) {
//				euMethod.addSkeleton	("SKEL016", "", pkCol[i].getVarName());
//				start					= false;
//			}
//			else
//				euMethod.addSkeleton	("SKEL016", ",", pkCol[i].getVarName());
//		}
//		euMethod.addSkeleton		("SKEL017");
//		addMethod					(euMethod);
//		Und jetzt noch die ganzen Zugriffe über Cursor
//		den Aufruf für selbstgeschriebene Abfragen
//		Method cursorMethod			= new Method("getCursor", ccName+"[]", new Parameter("queryString", "String")
//																		, new Parameter("para", "Object..."));
//		cursorMethod.setStatic	 	();
//		cursorMethod.addThrow		("JPAException");
//		cursorMethod.addSkeleton	("SKEL040", ccName, "createQuery");
//		addMethod					(cursorMethod);	
//		
//		den Aufruf für selbstgeschriebene Count-Abfragen
//		Method countMethod			= new Method("getCount", "long", new Parameter("queryString", "String")
//																   , new Parameter("para", "Object..."));
//		countMethod.setStatic	 	();
//		countMethod.addThrow		("JPAException");
//		countMethod.addSkeleton		("SKEL050", "createQuery");
//		addMethod					(countMethod);	

//		Paged Abfragen
//		Method pageInitMethod		= new Method("initPageQuery", "void", new Parameter("queryString", "String")
//																   , new Parameter("para", "Object..."));
//		pageInitMethod.setStatic	();
//		pageInitMethod.addThrow		("JPAException");
//		pageInitMethod.addSkeleton	("SKEL045", "createQuery", ccName);
//		addMethod					(pageInitMethod);	

//		NamedQueries Abfragen
//		den Aufruf für die NamedQueries Abfragen
//		Method namedMethod			= new Method("getNamedCursor", ccName+"[]", new Parameter("queryString", "String")
//																		, new Parameter("para", "Object..."));
//		namedMethod.setStatic	 	();
//		namedMethod.addThrow		("JPAException");
//		namedMethod.addSkeleton		("SKEL040", ccName, "createNamedQuery");
//		addMethod					(namedMethod);	

//		den Aufruf für NamedQuery Count-Abfragen
//		Method nmdCntMethod			= new Method("getNamedCount", "long", new Parameter("queryString", "String")
//																   , new Parameter("para", "Object..."));
//		nmdCntMethod.setStatic	 	();
//		nmdCntMethod.addThrow		("JPAException");
//		nmdCntMethod.addSkeleton	("SKEL050", "createNamedQuery");
//		addMethod					(nmdCntMethod);	

//		Paged Abfragen
//		Method namedPageIMeth		= new Method("initNamedPageQuery", "void", new Parameter("queryString", "String")
//																   , new Parameter("para", "Object..."));
//		namedPageIMeth.setStatic	();
//		namedPageIMeth.addThrow		("JPAException");
//		namedPageIMeth.addSkeleton	("SKEL045", "createNamedQuery", ccName);
//		addMethod					(namedPageIMeth);	

//		Paged Read
//		Method getPageMethod		= new Method("getPage", "int", new Parameter(table.getVarName(), ccName+"[]"));
//		getPageMethod.setStatic		();
//		getPageMethod.addThrow		("JPAException");
//		getPageMethod.addSkeleton	("SKEL046", table.getVarName(), ccName);
//		addMethod					(getPageMethod);	

//		erst den Beginn der NamedQueries-Annotations
//		addAnnot					("@NamedQueries({");
//		1. Cursor GS
//		Method cursor1Method		= new Method("getCursor_GS", ccName+"[]");
//		cursor1Method.setStatic	 	();
//		cursor1Method.addThrow		("JPAException");
//		cursor1Method.addSkeleton	("SKEL041", ccName, "GS");
//		addMethod					(cursor1Method);	
//		addAnnot					("	@NamedQuery(name=\""+ccName+".GS\",query=\"select t from "+ccName+" t\")");
//		2. Count GS
//		Method countGsMethod		= new Method("getCount_GS", "long");
//		countGsMethod.setStatic	 	();
//		countGsMethod.addThrow		("JPAException");
//		countGsMethod.addSkeleton	("SKEL051", ccName, "CNT_GS");
//		addMethod					(countGsMethod);	
//		addAnnot					(",	@NamedQuery(name=\""+ccName+".CNT_GS\",query=\"select count(*) from "+ccName+" t\")");
//		3. Cursor GSSO
//		Method cursor2Method		= new Method("getCursor_GSSO", ccName+"[]");
//		cursor2Method.setStatic	 	();
//		cursor2Method.addThrow		("JPAException");
//		cursor2Method.addSkeleton	("SKEL041", ccName, "GSSO");
//		addMethod					(cursor2Method);	
//		addAnnot					(",	@NamedQuery(name=\""+ccName+".GSSO\",query=\"select t from "+ccName+" t \"");
//		addAnnot					("			+ \" order by t.id\")");
//		Am Ende die NamedQueries abschließen
//		addAnnot					("})");
	}
	
	
//	private void addTransactMethods (String action, String lcName, String ccName) {
//		Method method01			= new Method(action, "void"	, new Parameter(lcName,ccName));
//		method01.setStatic		();
//		method01.addThrow		("JPAException");
//		method01.addSkeleton	("SKEL010", lcName, action);
//		addMethod				(method01);
//		Method method02			= new Method(action, "void"	, new Parameter(lcName,ccName)
//															, new Parameter("commit", "boolean"));
//		method02.setStatic		();
//		method02.addThrow		("JPAException");
//		method02.addSkeleton	("SKEL011", lcName, ccName, action);
//		addMethod				(method02);
//	}
	
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
			if (c.getColtype().name().equals("db2type_decimal"))		containsDec			= true;
			if (c.getColtype().name().equals("db2type_date"))			containsDate		= true;
			if (c.getColtype().name().equals("db2type_time"))			containsTime		= true;
			if (c.getColtype().name().equals("db2type_timestmp"))		containsTimestamp	= true;
		}
		if (containsDec)			pkClass.addImport				("java.math.BigDecimal");
		if (containsDate)			pkClass.addImport				("java.util.Date");
		if (containsTime)			pkClass.addImport				("java.sql.Time");
		if (containsTimestamp)		pkClass.addImport				("java.sql.Timestamp");

		Variable variable		= new Variable("serialVersionUID", Visibility.visibility_private, "long", "1L");
		variable.setFinalStatic	();
		pkClass.addVariable		(variable);
		for (Db2Column col : columns) 
			addVariable				(col, pkClass, false);
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
		String name				= col.getName();
		String varName			= col.getVarName();
//		Variablentyp ermitteln
		String type				= col.getColtype().getJavaString();
//		Variable anlegen
		Variable var			= new Variable(varName, Visibility.visibility_private, type, null);
//		Annotations - ggf. ID
		if  (isId)
			var.addAnnot				("@Id");
		if  (col.isGeneratedKey()) 
			var.addAnnot				("@GeneratedValue");			
//		Annotation - bei Date-Attributen
		if (type.equalsIgnoreCase("Date"))
			var.addAnnot("@Temporal( TemporalType.DATE)");
//		Annotations - Column
		if (type.equalsIgnoreCase("String"))
			var.addAnnot			("@Column(name=\""+name+"\",columnDefinition=\"char["+col.getLength()+"]\")");
		else
			var.addAnnot			("@Column(name=\""+name+"\")");
		clazz.addVariable		(var);
//		get- und set-Methoden
		createGetAndSet			(type.toString(), varName, clazz, false);
	}
	
	private static void createGetAndSet (String type, String varName, Clazz clazz, boolean isComment) {
		Method getMethod		= new Method	( "get"+Db2Table.upperCaseStart(varName)
												, type
												);
		getMethod.addSkeleton	("SKEL001", "this."+varName);
		if (isComment) 			getMethod.setComment();
		clazz.addMethod			(getMethod);
		Method setMethod		= new Method	( "set"+Db2Table.upperCaseStart(varName)
												, "void"
												, new Parameter(varName, type)
												);
		setMethod.addSkeleton	("SKEL002", varName);
		if (isComment) 			setMethod.setComment();
		clazz.addMethod			(setMethod);
	}
}
