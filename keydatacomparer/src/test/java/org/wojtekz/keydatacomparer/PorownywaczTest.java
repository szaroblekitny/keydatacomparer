package org.wojtekz.keydatacomparer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.wojtekz.keydatacomparer.utils.DaneTestowe;
import org.wojtekz.keydatacomparer.utils.StringResultSet;

/**
 * The big test for class Porownywacz. This is the core of Keydatacomparer application.
 * 
 * @author Wojciech Zaręba
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PorownywaczTest {
	private final static Logger LOGG = LogManager.getLogger(PorownywaczTest.class.getName());
	
	private static File outputFile;
	private static FileWriter writer;
	private static File confFile;
	private BazaDanych wzorzecMck = mock(BazaDanych.class);
	private BazaDanych kopiaMck = mock(BazaDanych.class);
	private Connection connWzorzecMck = mock(Connection.class);
	private Connection connKopiaMck = mock(Connection.class);
	private ArrayList<String> tabelki;
    private SortedSet<Klucz> kluczeWzorca = new TreeSet<Klucz>();
    private SortedSet<Klucz> kluczeKopii = new TreeSet<Klucz>();
    private PreparedStatement prepSt1WzMck = mock(PreparedStatement.class);
    private PreparedStatement prepSt1KpMck = mock(PreparedStatement.class);
    private PreparedStatement prepSt3WzMck = mock(PreparedStatement.class);
    private PreparedStatement prepSt3KpMck = mock(PreparedStatement.class);
    private ResultSet result1Wz;
    private ResultSet result1Kp;
    private ResultSet result3Kopia;
    private ResultSet result3Wzor;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LOGG.info("setUpBeforeClass fired");
		outputFile = new File(UUID.randomUUID().toString() + ".txt");
		writer = new FileWriter(outputFile);
		confFile = DaneTestowe.tworzPlikTestowyXML();
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		confFile.delete();
		outputFile.delete();
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
		
		
		@SuppressWarnings("unused")
		String[][] daneWzorca = {
				{"RowId", "GlownyID", "Imie", "Nazwisko"},
				{"AAA", "1", "Jan", "Kowalski"},
				{"AAB", "2", "Stanisław", "Nowak"},
				{"AAC", "3", "Kazimiera", "Brzoza"}};
		
		@SuppressWarnings("unused")
		String[][] daneKopii = {
				{"RowId", "GlownyID", "Imie", "Nazwisko"},
				{"AAA", "1", "Jan", "Kowalski"},
				{"AAC", "3", "Kazimierz", "Brzoza"}};
		
		/// -----------------------------------------------
		
		String[][] daneRes1 = {
				{"RowId", "GlownyID", "Imie", "Nazwisko"},
				{"AAA", "1", "Jan", "Kowalski"}
				};
		
		String[][] daneRes3Wz = {
				{"RowId", "GlownyID", "Imie", "Nazwisko"},
				{"AAC", "3", "Kazimiera", "Brzoza"}};
		
		String[][] daneRes3Kop = {
				{"RowId", "GlownyID", "Imie", "Nazwisko"},
				{"AAC", "3", "Kazimierz", "Brzoza"}};
		
		result1Wz = new StringResultSet(daneRes1);
		result1Kp = new StringResultSet(daneRes1);
		result3Wzor = new StringResultSet(daneRes3Wz);
		result3Kopia = new StringResultSet(daneRes3Kop);
		
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
		          tmpTab.dodajKolumne("GlownyID", "VARCHAR2");
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
			when(connWzorzecMck.prepareStatement("select GlownyID, Imie, Nazwisko from Tab1 where GlownyID = '1' order by GlownyID")).thenReturn(prepSt1WzMck);
			when(connWzorzecMck.prepareStatement("select GlownyID, Imie, Nazwisko from Tab1 where GlownyID = '3' order by GlownyID")).thenReturn(prepSt3WzMck);
			when(connKopiaMck.prepareStatement("select GlownyID, Imie, Nazwisko from Tab1 where GlownyID = '1' order by GlownyID")).thenReturn(prepSt1KpMck);
			when(connKopiaMck.prepareStatement("select GlownyID, Imie, Nazwisko from Tab1 where GlownyID = '3' order by GlownyID")).thenReturn(prepSt3KpMck);
			
			when(prepSt1WzMck.executeQuery()).thenReturn(result1Wz);
			when(prepSt1KpMck.executeQuery()).thenReturn(result1Kp);
			when(prepSt3WzMck.executeQuery()).thenReturn(result3Wzor);
			when(prepSt3KpMck.executeQuery()).thenReturn(result3Kopia);
			
			// -----------------
		
			Porownywacz comparer = new Porownywacz(writer);
			LOGG.info("-------- zaczynamy porównanie ----------");
			comparer.porownuj(wzorzecMck, kopiaMck, tabelki);
			
			writer.close();
			
			BufferedReader reader = Files.newBufferedReader(outputFile.toPath(), Charset.defaultCharset());
			
			String wynik = reader.readLine();
			Assert.assertEquals("---------------------------------------------", wynik);
			wynik = reader.readLine();
			Assert.assertEquals("Comparison for table Tab1", wynik);
			wynik = reader.readLine();
			Assert.assertEquals("Table columns:", wynik);
			wynik = reader.readLine();
			Assert.assertEquals("GlownyID|Imie|Nazwisko", wynik);
			wynik = reader.readLine();
			wynik = reader.readLine();
			Assert.assertEquals("Records that are in the source data but not in the compared data:", wynik);
			wynik = reader.readLine();
			Assert.assertEquals("2", wynik);
			wynik = reader.readLine();
			wynik = reader.readLine();
			Assert.assertEquals("Different records", wynik);
			wynik = reader.readLine();
			wynik = reader.readLine();
			Assert.assertEquals("Source data: 3                             |Kazimiera                     |Brzoza", wynik);
			wynik = reader.readLine();
			Assert.assertEquals("Compare data: 3                             |Kazimierz                     |Brzoza", wynik);
			
			reader.close();
			
			LOGG.info("-------- porównanie udane ----------");
			
		} catch (Exception ee) {
			LOGG.error("Porównanie zawiodło: ", ee);
			Assert.fail();
		}
		
	}

}
