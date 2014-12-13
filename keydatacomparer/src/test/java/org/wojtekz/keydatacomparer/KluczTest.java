package org.wojtekz.keydatacomparer;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KluczTest {
	private ArrayList<String> liKolInd = new ArrayList<>();
	private ArrayList<String> innaListka = new ArrayList<>();
	private Klucz kl1;
	private Klucz kl2;
	private Klucz klWzor;

	@Before
	public void setUp() throws Exception {
		liKolInd.add("Alfa");
		liKolInd.add("Beta");
		liKolInd.add("Gamma");
		
		innaListka.add("Alfa");
		innaListka.add("Beta");
		innaListka.add("Delta");
		
		klWzor = new Klucz(liKolInd);
		kl1 = new Klucz(liKolInd);
		kl2 = new Klucz(innaListka);
	}

	@Test
	public void testCompareTo() {
		int w1 = klWzor.compareTo(kl1);
		Assert.assertEquals(0, w1);
		int w2 = klWzor.compareTo(kl2);
		Assert.assertEquals(3, w2);
		
	}
	
	@Test
	public void testCompareString() {
		int w1 = "Alfb".compareTo("Alfa");
		Assert.assertEquals(1, w1);
		int w2 = "Alfb".compareTo("Alfaa");
		Assert.assertEquals(1, w2);
		int w3 = "Alfb".compareTo("Alfd");
		Assert.assertEquals(-2, w3);
		int w4 = "Alfb".compareTo("Alfg");
		Assert.assertEquals(-5, w4);
		int w5 = "Alfb".compareTo("Alfń");
		Assert.assertEquals(-226, w5);
	}

}
