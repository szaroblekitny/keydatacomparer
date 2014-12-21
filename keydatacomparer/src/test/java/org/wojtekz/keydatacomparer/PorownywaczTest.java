package org.wojtekz.keydatacomparer;

import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.wojtekz.utils.DaneTestowe;

@RunWith(MockitoJUnitRunner.class)
public class PorownywaczTest {
	private final static Logger LOGG = Logger.getLogger(PorownywaczTest.class.getName());
	
	private static String outputFile;
	private static FileWriter writer;
	private static File confFile;
	private static BufferedReader reader;
	private BazaDanych wzorzecMck = mock(BazaDanych.class, RETURNS_MOCKS);
	private BazaDanych kopiaMck = mock(BazaDanych.class, RETURNS_MOCKS);
	private Connection connMck = mock(Connection.class);
	//private KolumnaTabeli kol1 = new KolumnaTabeli();
	private ArrayList<String> tabelki;
	private Tabela tab1;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		outputFile = UUID.randomUUID().toString() + ".txt";
		writer = new FileWriter(outputFile);
		confFile = DaneTestowe.tworzPlikTestowyXML();
		reader = Files.newBufferedReader(FileSystems.getDefault().getPath(outputFile), Charset.defaultCharset());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		confFile.delete();
		writer.close();
		reader.close();
	}

	@Before
	public void setUp() throws Exception {
		// wzorzec = new OracleDB("aaa", "localhost", 1521, "scott", "password");
		// kopia = new OracleDB("aaa", "localhost", 1521, "hr", "password");
		tabelki = new ArrayList<String>();
		tabelki.add("Tab1");
		
		tab1 = new Tabela(tabelki.get(0));
		//when(wzorzecMck.).thenReturn("first");
		
		
		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testPorownuj() {
		LOGG.info("Compare test starts");
		
		when(wzorzecMck.getDbconnection()).thenReturn(connMck);
		
		doAnswer(new Answer() {
		      public Object answer(InvocationOnMock invocation) {
		          // Object[] args = invocation.getArguments();
		          // Object mock = invocation.getMock();
		    	  tab1.dodajKolumne("Ident", "VARCHAR2");
		    	  tab1.dodajKolumne("Nazwisko", "VARCHAR2");
		          return tab1;
		      }})
		  .when(wzorzecMck).getFields(tab1);
		
		doAnswer(new Answer() {
		      public Object answer(InvocationOnMock invocation) {
		          // Object[] args = invocation.getArguments();
		          // Object mock = invocation.getMock();
		    	  List<String> kluGlow = new ArrayList<>();
		    	  kluGlow.add("GlownyID");
		    	  tab1.setKluczGlowny(kluGlow);
		          return tab1;
		      }})
		  .when(wzorzecMck).addPrimaryKey(tab1);
		
		try {
			Porownywacz comparer = new Porownywacz(writer);
			comparer.porownuj(wzorzecMck, kopiaMck, tabelki);
			String wynik = reader.readLine();
			Assert.assertEquals("============================", wynik);
		} catch (Exception ee) {
			LOGG.error("Porównanie zawiodło: ", ee);
			Assert.fail();
		}
		
	}

}
