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
import java.sql.ResultSetMetaData;
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

@RunWith(MockitoJUnitRunner.class)
public class PorownywaczTest {
	private final static Logger LOGG = Logger.getLogger(PorownywaczTest.class.getName());
	
	private static String outputFile;
	private static FileWriter writer;
	private static File confFile;
	private static BufferedReader reader;
	private BazaDanych wzorzecMck = mock(BazaDanych.class, RETURNS_MOCKS);
	private BazaDanych kopiaMck = mock(BazaDanych.class, RETURNS_MOCKS);
	private Connection connWzorzecMck = mock(Connection.class);
	private Connection connKopiaMck = mock(Connection.class);
	private ArrayList<String> tabelki;
    private Klucz kl1;
    private Klucz kl2;
    private PreparedStatement prepStWzorMck = mock(PreparedStatement.class);
    private ResultSet resultWzorMck = mock(ResultSet.class);
    private PreparedStatement prepStKopiaMck = mock(PreparedStatement.class);
    private ResultSet resultKopiaMck = mock(ResultSet.class);
    private ResultSetMetaData wzorMetaDataMck = mock(ResultSetMetaData.class);
	
	
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
		
		List<String> daneKl1 = new ArrayList<>();
		daneKl1.add("1");
		daneKl1.add("2");
		List<String> daneKl2 = new ArrayList<>();
		daneKl1.add("1");
		daneKl1.add("3");
		
		kl1 = new Klucz(daneKl1);
		kl2 = new Klucz(daneKl2);
		
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testPorownuj() {
		LOGG.info("Compare test starts");
		
		when(wzorzecMck.getDbconnection()).thenReturn(connWzorzecMck);
		when(kopiaMck.getDbconnection()).thenReturn(connKopiaMck);
		
		doAnswer(new Answer<Tabela>() {
		      public Tabela answer(InvocationOnMock invocation) {
		          Object[] args = invocation.getArguments();
		          // Object mock = invocation.getMock();
		          Tabela tmpTab = (Tabela) args[0];
		    	  tmpTab.dodajKolumne("Imie", "VARCHAR2");
		    	  tmpTab.dodajKolumne("Nazwisko", "VARCHAR2");
		          return tmpTab;
		      }})
		  .when(wzorzecMck).getFields(any(Tabela.class));
		
		doAnswer(new Answer<Tabela>() {
		      public Tabela answer(InvocationOnMock invocation) {
		          Object[] args = invocation.getArguments();
		          // Object mock = invocation.getMock();
		          Tabela rettab = (Tabela) args[0];
		    	  List<String> kluGlow = new ArrayList<>();
		    	  kluGlow.add("GlownyID");
		    	  rettab.setKluczGlowny(kluGlow);
		          return rettab;
		      }})
		  .when(wzorzecMck).addPrimaryKey(any(Tabela.class));
		
		// daneKluczyWzorcowych = wzorzec.daneKluczowe(tabela);
		doAnswer(new Answer<Set<Klucz>>() {
		      public Set<Klucz> answer(InvocationOnMock invocation) {
		          SortedSet<Klucz> daneKluczy = new TreeSet<>();
		    	  daneKluczy.add(kl1);
		          return daneKluczy;
		      }})
		  .when(wzorzecMck).daneKluczowe(any(Tabela.class));
		
		doAnswer(new Answer<Set<Klucz>>() {
		      public Set<Klucz> answer(InvocationOnMock invocation) {
		          SortedSet<Klucz> daneKluczy = new TreeSet<>();
		    	  daneKluczy.add(kl2);
		          return daneKluczy;
		      }})
		  .when(kopiaMck).daneKluczowe(any(Tabela.class));
		
		
		
		// wzorzec.getSchemaAndDatabaseName()
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
		
		
		// prepStWzor = wzorzecConn.prepareStatement(sqlStatement);
		// when(mockedList.get(anyInt())).thenReturn("element");
		try {
			when(connWzorzecMck.prepareStatement(anyString())).thenReturn(prepStWzorMck);
			when(connKopiaMck.prepareStatement(anyString())).thenReturn(prepStKopiaMck);
			
			when(prepStWzorMck.executeQuery()).thenReturn(resultWzorMck);
			when(prepStKopiaMck.executeQuery()).thenReturn(resultKopiaMck);
			
			when(resultWzorMck.getMetaData()).thenReturn(wzorMetaDataMck);
		
		// ----------------------------------------------------------------------
		
		
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
