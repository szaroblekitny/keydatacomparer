package org.wojtekz.utils;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringResultSetTest {
	private ResultSet rs = new StringResultSet();


	@Before
	public void setUp() throws Exception {
		rs.first();
	}

	@Test
	public void testNext() {
		try {
			if (rs.next()) {
				String str = rs.getString(2);
				Assert.assertEquals("Stanisław", str);
			} else {
				Assert.fail("next false");
			}
		} catch (SQLException ee) {
			Assert.fail();
		}
	}

	@Test
	public void testGetStringInt() {
		try {
			String wyn = rs.getString(3);
			Assert.assertEquals("Kowalski", wyn);
		} catch (SQLException ee) {
			Assert.fail();
		}
		
	}

	@Test
	public void testGetStringString() {
		try {
			String wyn = rs.getString("GlownyID");
			Assert.assertEquals("1", wyn);
		} catch (SQLException ee) {
			Assert.fail();
		}
	}

	@Test
	public void testIsFirst() {
		try {
			Assert.assertTrue(rs.isFirst());
		} catch (SQLException ee) {
			Assert.fail();
		}
	}

	@Test
	public void testIsLast() {
		try {
			Assert.assertFalse(rs.isLast());
		} catch (SQLException ee) {
			Assert.fail();
		}
	}

	@Test
	public void testFirst() {
		try {
			rs.absolute(2);
			String str = rs.getString(2);
			Assert.assertEquals("Stanisław", str);
			rs.first();
			Assert.assertEquals("Jan", rs.getString(2));
		} catch (SQLException ee) {
			Assert.fail();
		}
	}

	@Test
	public void testLast() {
		try {
			if (rs.last()) {
				Assert.assertEquals("3", rs.getString(1));
			} else {
				Assert.fail();
			}

		} catch (SQLException ee) {
			Assert.fail();
		}
	}
	
	@Test
	public void testMetaData() {
		// pierwsze puste, żeby było od jedynki
		String nazwy[] = {"", "GlownyID", "Imie", "Nazwisko"};
		String nazwaKolumny;

		try {
			ResultSetMetaData meta = rs.getMetaData();
			for (int ii = 1; ii < meta.getColumnCount(); ii++) {
				nazwaKolumny = meta.getColumnLabel(ii);
				Assert.assertEquals(nazwy[ii], nazwaKolumny);
			}
		} catch (SQLException ee) {
			Assert.fail();
		}
	}

}