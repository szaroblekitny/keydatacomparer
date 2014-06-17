/*
 Licensed to the Simple Public License (SimPL) 2.0. You may obtain
 a copy of the License at http://opensource.org/licenses/Simple-2.0

 You get the royalty free right to use the software for any purpose;
 make derivative works of it (this is called a "Derived Work");
 copy and distribute it and any Derived Work.
 You get NO WARRANTIES. None of any kind. If the software damages you
 in any way, you may only recover direct damages up to the amount you
 paid for it (that is zero if you did not pay anything).
 */

package keydatacomparer;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Wojciech Zaręba
 *
 */
public class OracleDBTest extends BazaDanych {

	public OracleDBTest(String nazwaBazy, String nazwaHosta, String numerPortu,
			String schemat, String haslo) {
		super(nazwaBazy, nazwaHosta, numerPortu, schemat, haslo);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link keydatacomparer.OracleDB#databaseConnection()}.
	 */
	@Test
	public void testDatabaseConnection() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link keydatacomparer.OracleDB#addPrimaryKey(keydatacomparer.Tabela)}.
	 */
	@Test
	public void testAddPrimaryKey() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link keydatacomparer.OracleDB#getFields(keydatacomparer.Tabela)}.
	 */
	@Test
	public void testGetFields() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link keydatacomparer.OracleDB#daneKluczowe(keydatacomparer.Tabela)}.
	 */
	@Test
	public void testDaneKluczowe() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link keydatacomparer.OracleDB#OracleDB(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testOracleDB() {
		fail("Not yet implemented");
	}

	@Override
	public Connection databaseConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPrimaryKey(Tabela tabelka) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFields(Tabela tabl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SortedSet<Klucz> daneKluczowe(Tabela tabelka) {
		// TODO Auto-generated method stub
		return null;
	}

}
