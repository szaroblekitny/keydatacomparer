package org.wojtekz.keydatacomparer;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wojtekz.keydatacomparer.utils.DaneTestowe;
import org.wojtekz.keydatacomparer.utils.SprawdzPlikXML;
import org.xml.sax.SAXException;

public class ObsluzPlikiTest {
	private final static Logger LOGG = Logger.getLogger(ObsluzPlikiTest.class.getName());
	
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
		LOGG.info("Test obsługi plików");
		try {
			ObsluzPliki obsPlk = new ObsluzPliki(testFile.getName());
			Assert.assertEquals(tabele, obsPlk.getNazwyTabel());
			Assert.assertEquals(1521, obsPlk.getCompportnumber());
			Assert.assertEquals("scott", obsPlk.getCompusername());
		} catch (IOException | SAXException | ParserConfigurationException | ClassNotFoundException ee) {
			LOGG.error("Sprawdzenie nieudane", ee);
			Assert.fail();
		}
		
	}
	
	@Test
	public void testFormalnyPliku() {
		LOGG.info("Sprawdzenie poprawności formalnej");
		File plikXSD = new File("src/main/resources/xsd/keydatacomparer.xsd");
		try {
			InputStream xsdInpStream = new FileInputStream(plikXSD);
			if (LOGG.isDebugEnabled()) {
				LOGG.debug("Stream ma: " + xsdInpStream.available() + " bajtów");
			}
			
			
			SprawdzPlikXML sprawdzarka = new SprawdzPlikXML();
			sprawdzarka.sprawdzFormalnie(xsdInpStream, testFile);
		} catch (IOException | ParserConfigurationException | SAXException  ee) {
			LOGG.error("Sprawdzenie nieudane", ee);
			Assert.fail();
		}
		
		Assert.assertTrue(true);
		
	}

}
