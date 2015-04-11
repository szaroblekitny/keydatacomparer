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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

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

		// jdbc:postgresql://host:port/database
		String url = "jdbc:postgresql://" + hostname + ":" + portnumber + "/" + database;
		LOGG.info("PostgreSQL url: " + url);
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", userpassword);
		props.setProperty("ssl", "false");
		Connection conn = DriverManager.getConnection(url, props);

		return conn;
	}

	@Override
	public void addPrimaryKey(Tabela tabelka) {
		if (LOGG.isDebugEnabled()) {
    		LOGG.debug("addDatabaseTable: " + tabelka.getNazwaTabeli());
    	}
        List<String> kluGlu = new ArrayList<>();  // klucz główny
        PreparedStatement prepState;
        ResultSet result;
        String poleKlucza;

        try {
        	
        	// SELECT aa.attname, format_type(aa.atttypid, aa.atttypmod) AS data_type
        	// FROM   pg_index ii, pg_attribute aa
        	// where aa.attrelid = ii.indrelid
        	//  and aa.attnum = ANY(ii.indkey)
        	//  and ii.indrelid = 'configuracja'::regclass
        	//  AND ii.indisprimary;
        	
            String sqlStatement = "select aa.attname "
                    + "from pg_index ii, pg_attribute aa "
                    + "where aa.attrelid = ii.indrelid "
                    + "and aa.attnum = ANY(ii.indkey) "
                    + "and ii.indrelid = '" + tabelka.getNazwaTabeli() + "'::regclass "
                    + "and ii.indisprimary";
            if (LOGG.isDebugEnabled()) {
            	LOGG.debug("SQL:" + sqlStatement);
            }
            prepState = getDbconnection().prepareStatement(sqlStatement);
            result = prepState.executeQuery();

            while (result.next()) {
                poleKlucza = result.getString(1);
                if (poleKlucza != null) {
                    if (kluGlu.add(poleKlucza)) {
                    	if (LOGG.isDebugEnabled()) {
                    		LOGG.debug("Do klucza dodaję pole " + poleKlucza);
                    	}
                    } else {
                        LOGG.warn("Dodanie klucza nieudane");
                    }
                }
            }

        } catch (SQLException ex) {
        	// the original exception's message and stack trace should be logged or passed forward
            LOGG.error("Błąd SQL: " + ex.getMessage());
            throw new RuntimeException(ex);
        }

        tabelka.setKluczGlowny(kluGlu);
		
	}

	@Override
	public void getFields(Tabela tabl) {
		PreparedStatement prepState;
        ResultSet result;
        String kolumna;
        String typDanych;
        int precyzja;
        int skala;
        int szer_pola;

        try {
        	
        	// select column_name, data_type, numeric_precision, numeric_scale, character_maximum_length
        	// from information_schema.columns 
        	// where table_catalog = 'wzdata'
        	//  and table_schema = 'public'
        	//   and table_name = 'configuracja'
        	// order by ordinal_position;
        	
        	
            String sqlStatement =
                    "select column_name, data_type, numeric_precision, numeric_scale, character_maximum_length "
                    + "from information_schema.columns "
                    + "where table_catalog = 'wzdata' "  /* TODO Nazwa bazy danych!!! */
                    + "and table_schema = 'public' "
                    + "and table_name = '" + tabl.getNazwaTabeli() + "' "
                    + "order by ordinal_position";

            LOGG.debug(sqlStatement);
            prepState = getDbconnection().prepareStatement(sqlStatement);
            result = prepState.executeQuery();

            while (result.next()) {
                kolumna = result.getString(1);
                typDanych = result.getString(2);
                precyzja = result.getInt(3);
                skala = result.getInt(4);
                szer_pola = result.getInt(5);
                tabl.dodajKolumne(kolumna, typDanych, precyzja, skala, szer_pola);
            }

        } catch (SQLException ex) {
            LOGG.error("Błąd SQL: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
		
	}

	@Override
	public SortedSet<Klucz> daneKluczowe(Tabela tabelka) {
		SortedSet<Klucz> daneKluczy = new TreeSet<>();
        PreparedStatement prepState;
        ResultSet result;
        String sqlStatement;
        
        List<String> rekord = new ArrayList<>();
        String danePola;
        String daneRekordu;
        int ileRekordow = 0;

        List<String> kluczyk = tabelka.getKluczGlowny();

        try {
            
            // najpierw liczymy ile rekordów ma tabelka
            sqlStatement = "select count(*) from " + tabelka.getNazwaTabeli();
            prepState = getDbconnection().prepareStatement(sqlStatement);
            
            try {
            	result = prepState.executeQuery();
            	if(!result.next()) {
            		LOGG.error("Nie można policzyć");
            	}
            	ileRekordow = result.getInt(1);
            	if (LOGG.isDebugEnabled()) {
            		LOGG.debug("Liczba rekordów (" + getSchemaAndDatabaseName() + "/"
            			+ tabelka.getNazwaTabeli() +"): " + ileRekordow);
            	}
            	result.close();
            	
            } catch (SQLException sex) {
            	LOGG.debug("Zeruję liczbę rekordów", sex);
            	ileRekordow = 0;
            } finally {
            	prepState.close();
            }
            
            if (ileRekordow > 0) {
				// przechodzimy do wypełnienia struktury danych zawierającej wrtości kluczy głównych
				sqlStatement = "select ";
				for (int ii = 0; ii < kluczyk.size(); ii++) {
					sqlStatement += kluczyk.get(ii) + ", ";
				}
				// ucinamy końcowy przecinek
				sqlStatement = sqlStatement.substring(0,
						sqlStatement.length() - 2);
				sqlStatement += " from " + tabelka.getNazwaTabeli() + " "
						+ "order by ";
				for (int ii = 0; ii < kluczyk.size(); ii++) {
					sqlStatement += kluczyk.get(ii) + ", ";
				}
				// ucinamy końcowy przecinek
				sqlStatement = sqlStatement.substring(0,
						sqlStatement.length() - 2);
				if (LOGG.isDebugEnabled()) {
					LOGG.debug(sqlStatement);
				}
				prepState = getDbconnection().prepareStatement(sqlStatement);
				result = prepState.executeQuery();
				
				while (result.next()) {
					daneRekordu = "";
					// numerujemy od jednego, JDBC ma inną pragmatykę...
					for (int ii = 1; ii <= kluczyk.size(); ii++) {
						danePola = result.getString(ii);
						rekord.add(danePola);
						daneRekordu += danePola + " ";
					}
					if (LOGG.isTraceEnabled()) {
						LOGG.trace("Rec: " + daneRekordu);
					}

					if (!daneKluczy.add(new Klucz(rekord)) && LOGG.isDebugEnabled()) {
						LOGG.debug("Nieudaczne dodanie");
					}
					rekord.clear();
				} // while (result.next())
				
				if (LOGG.isDebugEnabled()) {
					LOGG.debug("Ile w kluczach: " + daneKluczy.size());
				}
			}

        } catch (SQLException ex) {
            LOGG.error("Błąd SQL (daneKluczowe): " + ex.getMessage());
            throw new RuntimeException(ex);
        }

        return daneKluczy;
    }

}
