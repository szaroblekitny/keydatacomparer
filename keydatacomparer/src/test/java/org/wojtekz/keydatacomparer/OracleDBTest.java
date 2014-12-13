package org.wojtekz.keydatacomparer;


import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OracleDBTest {
	private Tabela tabela;
	private Klucz klucz;
	private ArrayList<String> kluczyk = new ArrayList<String>();
	private ArrayList<String> daneKluczaDoPorow = new ArrayList<String>();

	@Before
	public void setUp() throws Exception {
		kluczyk.add("K1");
		kluczyk.add("K2");
		kluczyk.add("K3");
		tabela = new Tabela("Testowa", kluczyk);
		tabela.dodajKolumne("pierwsza", "VARCHAR2");
		tabela.dodajKolumne("druga", "VARCHAR2");
		tabela.dodajKolumne("trzecia", "VARCHAR2");
		daneKluczaDoPorow.add("Alfa");
		daneKluczaDoPorow.add("Beta");
		daneKluczaDoPorow.add("Gamma");
		klucz = new Klucz(daneKluczaDoPorow);
	}

	@Test
	public void testTworzenieSelecta() {
		String sql = OracleDB.tworzenieSelecta(tabela, klucz);
		Assert.assertEquals("select pierwsza, druga, trzecia from Testowa where K1 = 'Alfa' and K2 = 'Beta' and K3 = 'Gamma' order by K1, K2, K3", sql);
	}

}
