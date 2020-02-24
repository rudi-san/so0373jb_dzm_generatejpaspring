package de.kbs.so0373jb.dao.sqlserver;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.kbs.so0373jb.common.config.Configuration;

public class SqlServerTest  {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration.getConfiguration("resources/config/SqlServer.xml");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testExistsTable() {
		try {
			assertTrue ("Tabelle müsste existieren", SqlServerConnection.existsTable("TUFMST", "Aufnahmeantrag"));
			assertFalse("Tabelle dürfte nicht existieren", SqlServerConnection.existsTable("BLOED", "Quatsch"));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	@Test
	public void testReadColumns() {
		List<SqlServerColumn> columns	= SqlServerColumn.read("TUFMST", "Aufnahmeantrag");
		assertNotNull				(columns);
		assertNotEquals				(0, columns.size());
//		columns.stream().forEach	(s -> System.out.println(s.toString()));
	}

//	@Test
	public void testReadForeignKeys() {
		List<SqlServerForeignkey> forKeys	= SqlServerForeignkey.read("F536", "TBTST02", "FKTST011");
		assertNotNull					(forKeys);
		assertNotEquals					(0, forKeys.size());
//		forKeys.stream().forEach		(s -> System.out.println(s.toString()));
	}

	@Test
	public void testParentRels() {
		List<SqlServerRel> rels		= SqlServerRel.readParent("TUFMST", "weitere_einkuenfte_013a_linked");
		assertNotNull				("Es sollte ein Ergebnis mit Parent-Relationen geben", rels);
		assertNotEquals				(0, rels.size());
		rels.stream().forEach		(s -> System.out.println("Parent "+s.toString()));
	}

	@Test
	public void testChildRels() {
		List<SqlServerRel> rels		= SqlServerRel.readChild("TUFMST", "Aufnahmeantrag");
		assertNotNull				(rels);
		assertNotEquals				(0, rels.size());
//		rels.stream().forEach		(s -> System.out.println("Child "+s.toString()));
	}
}
