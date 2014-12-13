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

}
