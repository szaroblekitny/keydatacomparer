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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extends BazaDanych class for implementation of Oracle database.
 *
 * @author Wojciech Zaręba
 */
public class OracleDB extends BazaDanych {

    private final static Logger LOGG = LogManager.getLogger(OracleDB.class.getName());

    public OracleDB(String nazwaBazy,
            String nazwaHosta,
            int numerPortu,
            String schemat,
            String haslo) {
        super(nazwaBazy, nazwaHosta, numerPortu, schemat, haslo);

        LOGG.info("database " + database);
        LOGG.info("hostname " + hostname);
        LOGG.info("portnumber " + portnumber);
        LOGG.info("username " + username);
        LOGG.info("userpassword " + userpassword);
    }

    @Override
    public Connection databaseConnection() throws SQLException {
        // "jdbc:oracle:thin:hr/password@localhost:1521:wzdata"
        this.connectUrl = "jdbc:oracle:thin:" + this.username + "/" + this.userpassword
                + "@" + this.hostname + ":" + this.portnumber + ":" + this.database;
        if (LOGG.isDebugEnabled()) {
        	LOGG.debug("URL: " + connectUrl);
        }

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(this.connectUrl);
        dbconnection = ods.getConnection();

        LOGG.info("Mamy połączenie " + this.database);

        return getDbconnection();
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
            String sqlStatement = "select acc.column_name "
                    + "from sys.all_constraints alc, SYS.all_cons_columns acc "
                    + "where alc.OWNER = '" + this.username.toUpperCase() + "' "
                    + "and alc.table_name = '" + tabelka.getNazwaTabeli().toUpperCase() + "' "
                    + "and alc.constraint_type = 'P' "
                    + "and acc.owner = alc.owner "
                    + "and acc.constraint_name = alc.constraint_name "
                    + "order by acc.position";
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
            LOGG.error("Błąd SQL: " + ex.getMessage());
        }

        tabelka.setKluczGlowny(kluGlu);
    }

    @Override
    public void getFields(Tabela tabelka) {
        PreparedStatement prepState;
        ResultSet result;
        String kolumna;
        String typDanych;
        int precyzja;
        int skala;
        int szer_pola;

        try {
            /*  wersja pomijająca pola klucza głównego, ale robimy prościej
            String sqlStatement =
                    "select col.column_name, col.data_type, col.data_precision, col.data_scale, col.data_length "
                    + "from sys.all_tab_columns col "
                    + "where col.OWNER = '" + this.username.toUpperCase() + "' "
                    + "and col.TABLE_NAME = '" + tabelka.getNazwaTabeli().toUpperCase() + "' "
                    + "and col.column_name not in (select acc.column_name "
                    + "from sys.all_constraints alc, SYS.all_cons_columns acc "
                    + "where alc.OWNER = col.owner "
                    + "and alc.table_name = col.table_name "
                    + "and alc.constraint_type = 'P' "
                    + "and acc.owner = alc.owner "
                    + "and acc.constraint_name = alc.constraint_name) "
                    + "order by col.column_id";
                    */
            
            String sqlStatement =
                    "select col.column_name, col.data_type, col.data_precision, col.data_scale, col.data_length "
                    + "from sys.all_tab_columns col "
                    + "where col.OWNER = '" + this.username.toUpperCase() + "' "
                    + "and col.TABLE_NAME = '" + tabelka.getNazwaTabeli().toUpperCase() + "' "
                    + "order by col.column_id";

            LOGG.debug(sqlStatement);
            prepState = getDbconnection().prepareStatement(sqlStatement);
            result = prepState.executeQuery();

            while (result.next()) {
                kolumna = result.getString(1);
                typDanych = result.getString(2);
                precyzja = result.getInt(3);
                skala = result.getInt(4);
                szer_pola = result.getInt(5);
                tabelka.dodajKolumne(kolumna, typDanych, precyzja, skala, szer_pola);
            }

        } catch (SQLException ex) {
            LOGG.error("Błąd SQL: " + ex.getMessage());
        }
    }

    /**
     * Creates data from primary key sorted set for table tabelka. It will be
     * used to find missing records and compare rest of records data.
     *
     * @param tabelka database table
     * @return data for primary keys
     */
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
        }

        return daneKluczy;
    }
}
