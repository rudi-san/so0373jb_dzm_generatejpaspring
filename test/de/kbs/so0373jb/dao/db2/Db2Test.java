package de.kbs.so0373jb.dao.db2;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.kbs.so0373jb.common.config.Configuration;

public class Db2Test  {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Configuration.getConfiguration("resources/config/TBTST.xml");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testExistsTable() {
		try {
			assertTrue("Existiert Tabelle?", Db2Connection.existsTable("F536", "TBTST01"));
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	@Test
	public void testReadColumns() {
		List<Db2Column> columns	= Db2Column.read("F536", "TBTST01");
		assertNotNull				(columns);
		assertNotEquals				(0, columns.size());
		columns.stream().forEach	(s -> System.out.println(s.toString()));
	}

	@Test
	public void testReadForeignKeys() {
		List<Db2Foreignkey> forKeys	= Db2Foreignkey.read("F536", "TBTST02", "FKTST011");
		assertNotNull					(forKeys);
		assertNotEquals					(0, forKeys.size());
//		forKeys.stream().forEach		(s -> System.out.println(s.toString()));
	}

	@Test
	public void testParentRels() {
		List<Db2Rel> rels			= Db2Rel.readParent("F536", "TBTST02");
		assertNotNull				(rels);
		assertNotEquals				(0, rels.size());
//		rels.stream().forEach		(s -> System.out.println("Parent "+s.toString()));
	}

	@Test
	public void testChildRels() {
		List<Db2Rel> rels			= Db2Rel.readChild("F536", "TBTST01");
		assertNotNull				(rels);
		assertNotEquals				(0, rels.size());
//		rels.stream().forEach		(s -> System.out.println("Child "+s.toString()));
	}
}
