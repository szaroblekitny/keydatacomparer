package org.wojtekz.keydatacomparer;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TabelaTest {
	private final static Logger logg = Logger.getLogger(TabelaTest.class.getName());
	
	private Tabela tabela;
	private ArrayList<String> kluczyk = new ArrayList<String>();

	@Before
	public void setUp() throws Exception {
		PropertyConfigurator.configure(TabelaTest.class.getClassLoader()
                .getResource("log4j.properties"));
		kluczyk.add("K1");
		kluczyk.add("K2");
		kluczyk.add("K3");
		tabela = new Tabela("Testowa", kluczyk);
	}

	@Test
	public void testDodajKolumneStringString() {
		logg.debug("testDodajKolumneStringString fired");
		tabela.dodajKolumne("K1", "int");
		tabela.dodajKolumne("K2", "varchar2");
		tabela.dodajKolumne("K3", "number");
		Assert.assertEquals("K2", tabela.getPolaTabeli().get(1).getNazwaKolumnny());
		Assert.assertEquals("number", tabela.getPolaTabeli().get(2).getTypDanych());
	}

	@Test
	public void testDodajKolumneStringStringIntIntInt() {
		logg.debug("testDodajKolumneStringStringIntIntInt fired");
		tabela.dodajKolumne("K1", "int");
		tabela.dodajKolumne("K2", "number", 10, 2, 10);
		tabela.dodajKolumne("K3", "number", 20, 5, 22);
		Assert.assertEquals(10, tabela.getPolaTabeli().get(1).getPrecyzja());
		Assert.assertEquals(5, tabela.getPolaTabeli().get(2).getSkala());
		Assert.assertEquals(22, tabela.getPolaTabeli().get(2).getSzerokosc());
		Assert.assertEquals(20, tabela.getPolaTabeli().get(2).getPrecyzja());
	}

}
