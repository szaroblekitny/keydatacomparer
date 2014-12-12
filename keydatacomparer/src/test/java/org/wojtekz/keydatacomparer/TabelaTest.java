package org.wojtekz.keydatacomparer;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TabelaTest {
	private final static Logger logg = Logger.getLogger(TabelaTest.class.getName());
	
	private Tabela tabela;
	private ArrayList<String> kluczyk = new ArrayList<String>();

	@Before
	public void setUp() throws Exception {
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
		Assert.fail("Not yet implemented");
	}

}
