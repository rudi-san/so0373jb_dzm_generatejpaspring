package de.kbs.SO0373JB.business;

import de.kbs.SO0373JB.common.config.Configuration;
import de.kbs.SO0373JB.db2.Db2Table;

public class SpringRepository extends Interfaze {

	public SpringRepository(Db2Table table) {
		super			( Configuration.getConfiguration().getPackage()+".repos"
						, table.getCcName()+"Repository"
						, "JpaRepository <"+table.getCcName()+", "+table.getPkType()+">"
						, "org.springframework.data.jpa.repository.JpaRepository"
						, Configuration.getConfiguration().getPackage()+".jpa."+table.getCcName());
		String pkType	= table.getPkType();
		if (pkType.endsWith("Pk"))
			addImport		(Configuration.getConfiguration().getPackage()+".jpa."+pkType);
	}

}
