package org.wojtekz.keydatacomparer;

import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
import org.wojtekz.utils.StringResultSet;

@RunWith(MockitoJUnitRunner.class)
public class PorownywaczTest {
	private final static Logger LOGG = Logger.getLogger(PorownywaczTest.class.getName());
	
	private static String outputFile;
	private static FileWriter writer;
	private static File confFile;
	private static BufferedReader reader;
	private BazaDanych wzorzecMck = mock(BazaDanych.class);
	private BazaDanych kopiaMck = mock(BazaDanych.class);
	private Connection connWzorzecMck = mock(Connection.class);
	private Connection connKopiaMck = mock(Connection.class);
	private ArrayList<String> tabelki;
    private SortedSet<Klucz> kluczeWzorca = new TreeSet<Klucz>();
    private SortedSet<Klucz> kluczeKopii = new TreeSet<Klucz>();
    private PreparedStatement prepStWzorMck = mock(PreparedStatement.class);
    private ResultSet resultWzor;
    private PreparedStatement prepStKopiaMck = mock(PreparedStatement.class);
    private ResultSet resultKopia;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LOGG.info("setUpBeforeClass fired");
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
		LOGG.info("setUp Before fired");
		tabelki = new ArrayList<String>();
		tabelki.add("Tab1");
		
		// SortedSet dla wzorca
		List<String> daneKl1 = new ArrayList<>();
		daneKl1.add("1");
		Klucz kl1 = new Klucz(daneKl1);
		LOGG.debug("--- pierwszy klucz");
		kluczeWzorca.add(kl1);
		List<String> daneKl2 = new ArrayList<>();
		daneKl2.add("2");
		Klucz kl2 = new Klucz(daneKl2);
		LOGG.debug("--- drugi klucz");
		kluczeWzorca.add(kl2);
		List<String> daneKl3 = new ArrayList<>();
		daneKl3.add("3");
		Klucz kl3 = new Klucz(daneKl3);
		LOGG.debug("--- trzeci klucz");
		kluczeWzorca.add(kl3);
		kluczeKopii.add(kl1);
		kluczeKopii.add(kl3);
		
		
		String[][] daneWzorca = {
				{"RowId", "GlownyID", "Imie", "Nazwisko"},
				{"AAA", "1", "Jan", "Kowalski"},
				{"AAB", "2", "Stanisław", "Nowak"},
				{"AAC", "3", "Kazimiera", "Brzoza"}};
		
		String[][] daneKopii = {
				{"RowId", "GlownyID", "Imie", "Nazwisko"},
				{"AAA", "1", "Jan", "Kowalski"},
				{"AAC", "3", "Kazimierz", "Brzoza"}};
		
		resultWzor = new StringResultSet(daneWzorca);
		resultKopia = new StringResultSet(daneKopii);
		
		LOGG.info("setUp Before ended");
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testPorownuj() {
		LOGG.info("==== Compare test starts ====");
		
		when(wzorzecMck.getDbconnection()).thenReturn(connWzorzecMck);
		when(kopiaMck.getDbconnection()).thenReturn(connKopiaMck);
		
		doAnswer(new Answer<Tabela>() {
		      public Tabela answer(InvocationOnMock invocation) {
		          Object[] args = invocation.getArguments();
		          Tabela tmpTab = (Tabela) args[0];
		    	  tmpTab.dodajKolumne("Imie", "VARCHAR2");
		    	  tmpTab.dodajKolumne("Nazwisko", "VARCHAR2");
		          return tmpTab;
		      }})
		  .when(wzorzecMck).getFields(any(Tabela.class));
		
		// -----------------
		doAnswer(new Answer<Tabela>() {
		      public Tabela answer(InvocationOnMock invocation) {
		          Object[] args = invocation.getArguments();
		          Tabela rettab = (Tabela) args[0];
		    	  List<String> kluGlow = new ArrayList<>();
		    	  kluGlow.add("GlownyID");
		    	  rettab.setKluczGlowny(kluGlow);
		          return rettab;
		      }})
		  .when(wzorzecMck).addPrimaryKey(any(Tabela.class));
		
		// -----------------
		doAnswer(new Answer<Set<Klucz>>() {
		      public Set<Klucz> answer(InvocationOnMock invocation) {
		          return kluczeWzorca;
		      }})
		  .when(wzorzecMck).daneKluczowe(any(Tabela.class));
		
		doAnswer(new Answer<Set<Klucz>>() {
		      public Set<Klucz> answer(InvocationOnMock invocation) {
		          return kluczeKopii;
		      }})
		  .when(kopiaMck).daneKluczowe(any(Tabela.class));
		
		// -----------------
		doAnswer(new Answer<String>() {
		      public String answer(InvocationOnMock invocation) {
		          return "Testowy:test";
		      }})
		  .when(wzorzecMck).getSchemaAndDatabaseName();
		
		doAnswer(new Answer<String>() {
		      public String answer(InvocationOnMock invocation) {
		          return "Kopiowany:test";
		      }})
		  .when(kopiaMck).getSchemaAndDatabaseName();
		
		
		try {
			when(connWzorzecMck.prepareStatement(anyString())).thenReturn(prepStWzorMck);
			when(connKopiaMck.prepareStatement(anyString())).thenReturn(prepStKopiaMck);
			
			when(prepStWzorMck.executeQuery()).thenReturn(resultWzor);
			when(prepStKopiaMck.executeQuery()).thenReturn(resultKopia);
			
			// -----------------
		
			Porownywacz comparer = new Porownywacz(writer);
			LOGG.info("-------- zaczynamy porównanie ----------");
			comparer.porownuj(wzorzecMck, kopiaMck, tabelki);
			String wynik = reader.readLine();
			Assert.assertEquals("============================", wynik);
		} catch (Exception ee) {
			LOGG.error("Porównanie zawiodło: ", ee);
			Assert.fail();
		}
		
	}

}
