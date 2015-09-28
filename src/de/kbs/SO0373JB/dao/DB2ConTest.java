package de.kbs.SO0373JB.dao;

import java.util.ArrayList;

public class DB2ConTest {

	public static void main(String[] args) {
		ArrayList<SysColumns> list01	= SysColumns.read("F531", "TBSOF20");	
		for (SysColumns col : list01)
			System.out.println		(col.getName()+" "+col.getColtype());

		ArrayList<SysRels> list02	= SysRels.readParent("F531", "TBSOF26");
		for (SysRels rel : list02) {
			String relname			= rel.getRelname();
			String reftbcreator		= rel.getReftbcreator();
			String reftbname		= rel.getReftbname();
			System.out.println		(rel.getReftbname()+" "+relname);
			ArrayList<SysForeignkeys> list03 = SysForeignkeys.read("F531", "TBSOF26", relname);
			for (SysForeignkeys key : list03) {
				int keyseq			= key.getColseq();
				System.out.println("\t"+key.getColname());
				ArrayList<SysColumns> list04 = SysColumns.read(reftbcreator, reftbname, keyseq);
				for (SysColumns c : list04) 
					System.out.println("\t\t"+c.getName());
			}
		}
		
		ArrayList<SysRels> list05	= SysRels.readChild("F531", "TBSOF22");	
		for (SysRels rel : list05)
			System.out.println		(rel.getTbname());

		ArrayList<SysIndexes> list06	= SysIndexes.read("F531", "TBSOF22");	
		for (SysIndexes index : list06) {
			String ixcreator		= index.getCreator();
			String ixname			= index.getName();
			System.out.println		(ixcreator+" "+ixname+" "+index.getUniquerule());
			ArrayList<SysKeys> list07	= SysKeys.read(ixcreator, ixname);	
			for (SysKeys keys : list07) {
				System.out.println		("\t"+keys.getColname());
			}
		}
	}
}
