package org.wojtekz.keydatacomparer;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wojtekz.utils.DaneTestowe;
import org.wojtekz.utils.SprawdzPlikXML;
import org.xml.sax.SAXException;

public class ObsluzPlikiTest {
	private final static Logger logg = Logger.getLogger(ObsluzPlikiTest.class.getName());
	
	private static File testFile;
	
	private ArrayList<String> tabele;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testFile = DaneTestowe.tworzPlikTestowyXML();
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		if(!testFile.delete()) {
			throw new IOException("File not deleted");
		}
	}

	@Before
	public void setUp() throws Exception {
		tabele = new ArrayList<String>();
		tabele.add("CONFIGURACJA");
		tabele.add("RECONFIGURACJA");
	}

	

	@Test
	public void testObsluzPliki() {
		logg.info("Test obsługi plików");
		try {
			ObsluzPliki obsPlk = new ObsluzPliki(testFile.getName());
			Assert.assertEquals(tabele, obsPlk.getNazwyTabel());
			Assert.assertEquals(1521, obsPlk.getCompportnumber());
			Assert.assertEquals("scott", obsPlk.getCompusername());
		} catch (IOException | SAXException | ParserConfigurationException ee) {
			logg.error("Sprawdzenie nieudane", ee);
			Assert.fail();
		}
		
	}
	
	@Test
	public void testFormalnyPliku() {
		logg.info("Sprawdzenie poprawności formalnej");
		File plikXSD = new File("xsd/keydatacomparer.xsd");
		try {
			SprawdzPlikXML.sprawdzFormalnie(plikXSD, testFile);
		} catch (IOException | ParserConfigurationException | SAXException ee) {
			logg.error("Sprawdzenie nieudane", ee);
			Assert.fail();
		}
		
		Assert.assertTrue(true);
		
	}

}
