package org.wojtekz.keydatacomparer;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wojtekz.utils.DaneTestowe;

public class PorownywaczTest {
	private final static Logger logg = Logger.getLogger(PorownywaczTest.class.getName());
	private static FileWriter writer;
	private static File confFile;
	private BazaDanych wzorzec;
	private BazaDanych kopia;
	private ObsluzPliki obspli;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String outputFile = UUID.randomUUID().toString() + ".txt";
		writer = new FileWriter(outputFile);
		confFile = DaneTestowe.tworzPlikTestowyXML();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		confFile.delete();
	}

	@Before
	public void setUp() throws Exception {
		wzorzec = new OracleDB("aaa", "localhost", 1521, "scott", "password");
		kopia = new OracleDB("aaa", "localhost", 1521, "hr", "password");
		obspli = new ObsluzPliki(confFile.getName());
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testPorownuj() {
		logg.info("Compare test starts");
		try {
			Porownywacz comparer = new Porownywacz(writer);
			// TODO comparer.porownuj(wzorzec, kopia, obspli);
		} catch (Exception ee) {
			logg.error("Porównanie zawiodło: ", ee);
			Assert.fail();
		}
		
	}

}
