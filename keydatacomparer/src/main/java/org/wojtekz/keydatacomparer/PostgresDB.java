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
package org.wojtekz.keydatacomparer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.SortedSet;

import org.apache.log4j.Logger;

public class PostgresDB extends BazaDanych {
	private final static Logger LOGG = Logger.getLogger(PostgresDB.class.getName());

	public PostgresDB(String nazwaBazy, String nazwaHosta, int numerPortu,
			String schemat, String haslo) {
		super(nazwaBazy, nazwaHosta, numerPortu, schemat, haslo);
		
		LOGG.info("PostgreSQL database " + database);
        LOGG.info("hostname " + hostname);
        LOGG.info("portnumber " + portnumber);
        LOGG.info("username " + username);
        LOGG.info("userpassword " + userpassword);
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
