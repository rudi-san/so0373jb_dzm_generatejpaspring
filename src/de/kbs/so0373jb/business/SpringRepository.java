package de.kbs.so0373jb.business;

import de.kbs.so0373jb.common.config.Configuration;
import de.kbs.so0373jb.model.Table;

public class SpringRepository extends Interfaze {

	public SpringRepository(Table table) {
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
