package org.wojtekz.keydatacomparer.utils;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wojtekz.keydatacomparer.utils.StringResultSet;

public class StringResultSetTest {
	private final static Logger LOGG = Logger.getLogger(StringResultSetTest.class.getName());
	
	private static String[][] dane = {
			{"RowId", "GlownyID", "Imie", "Nazwisko"},
			{"AAA", "1", "Jan", "Kowalski"},
			{"AAB", "2", "Stanisław", "Nowak"},
			{"AAC", "3", "Kazimiera", "Brzoza"}
	};
	private static ResultSet rs;


	@BeforeClass
	public static void setUpBeforeClass() throws SQLException {
		LOGG.trace("StringResultSetTest before set up");
		rs = new StringResultSet(dane);
	}
	
	@Before
	public void setUp() throws SQLException {
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
			LOGG.error("SQL Error", ee);
			Assert.fail();
		}
	}

	@Test
	public void testGetStringInt() {
		try {
			String wyn = rs.getString(3);
			Assert.assertEquals("Kowalski", wyn);
		} catch (SQLException ee) {
			LOGG.error("SQL Error", ee);
			Assert.fail();
		}
		
	}

	@Test
	public void testGetStringString() {
		LOGG.trace("StringResultSetTest testGetStringString fired");
		try {
			String wyn = rs.getString("GlownyID");
			LOGG.debug("wynik: " + wyn + " a ma byc 1");
			Assert.assertEquals("1", wyn);
		} catch (SQLException ee) {
			LOGG.error("SQL Error", ee);
			Assert.fail();
		}
	}

	@Test
	public void testIsFirst() {
		try {
			Assert.assertTrue(rs.isFirst());
		} catch (SQLException ee) {
			LOGG.error("SQL Error", ee);
			Assert.fail();
		}
	}

	@Test
	public void testIsLast() {
		try {
			Assert.assertFalse(rs.isLast());
		} catch (SQLException ee) {
			LOGG.error("SQL Error", ee);
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
			LOGG.error("SQL Error", ee);
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
			LOGG.error("SQL Error", ee);
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
			LOGG.error("SQL Error", ee);
			Assert.fail();
		}
	}

}
