package org.wojtekz.keydatacomparer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.ArrayList;
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
	private static FileWriter writer;
	private static File confFile;
	private BazaDanych wzorzecMck = mock(BazaDanych.class);
	private BazaDanych kopiaMck = mock(BazaDanych.class);
	private Connection connMck = mock(Connection.class);
	//private KolumnaTabeli kol1 = new KolumnaTabeli();
	private ArrayList<String> tabelki;
	private Tabela tab1;

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
		// wzorzec = new OracleDB("aaa", "localhost", 1521, "scott", "password");
		// kopia = new OracleDB("aaa", "localhost", 1521, "hr", "password");
		tabelki = new ArrayList<String>();
		tabelki.add("Tab1");
		
		tab1 = new Tabela(tabelki.get(0));
		//when(wzorzecMck.).thenReturn("first");
		
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
		    	  tab1.dodajKolumne("Ident", "VARCHAR2");
		    	  tab1.dodajKolumne("Nazwisko", "VARCHAR2");
		          return tab1;
		      }})
		  .when(wzorzecMck).addPrimaryKey(tab1);
		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testPorownuj() {
		LOGG.info("Compare test starts");
		try {
			Porownywacz comparer = new Porownywacz(writer);
			comparer.porownuj(wzorzecMck, kopiaMck, tabelki);
		} catch (Exception ee) {
			LOGG.error("Porównanie zawiodło: ", ee);
			Assert.fail();
		}
		
	}

}
