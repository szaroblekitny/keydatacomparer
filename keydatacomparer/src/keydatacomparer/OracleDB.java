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

import java.sql.*;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.log4j.Logger;

/**
 * Extends Oracle database.
 *
 * @author Wojciech Zaręba
 */
public class OracleDB extends BazaDanych {

    private static Logger logg = Logger.getLogger(OracleDB.class.getName());

    public OracleDB(String nazwaBazy,
            String nazwaHosta,
            String numerPortu,
            String schemat,
            String haslo) {
        super(nazwaBazy, nazwaHosta, numerPortu, schemat, haslo);

        logg.info("database " + database);
        logg.info("hostname " + hostname);
        logg.info("portnumber " + portnumber);
        logg.info("username " + username);
        logg.info("userpassword " + userpassword);
    }

    @Override
    public Connection databaseConnection() throws SQLException {
        // "jdbc:oracle:thin:hr/password@localhost:1521:wzdata"
        this.connectUrl = "jdbc:oracle:thin:" + this.username + "/" + this.userpassword
                + "@" + this.hostname + ":" + this.portnumber + ":" + this.database;

        logg.debug("URL: " + connectUrl);

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(this.connectUrl);
        dbconnection = ods.getConnection();

        logg.info("Mamy połączenie " + this.database);

        return getDbconnection();
    }

    @Override
    public void addPrimaryKey(Tabela tabelka) {
        logg.debug("addDatabaseTable: " + tabelka.getNazwaTabeli());
        
        ArrayList<String> kluGlu = new ArrayList<>();  // klucz główny
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
            logg.debug(sqlStatement);
            prepState = getDbconnection().prepareStatement(sqlStatement);
            result = prepState.executeQuery();

            while (result.next()) {
                poleKlucza = result.getString(1);
                if (poleKlucza != null) {
                    if (kluGlu.add(poleKlucza)) {
                        logg.debug("Do klucza dodaję pole " + poleKlucza);
                    } else {
                        logg.warn("Dodanie nieudane");
                    }
                }
            }

        } catch (SQLException ex) {
            logg.error("Błąd SQL: " + ex.getMessage());
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

            logg.debug(sqlStatement);
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
            logg.error("Błąd SQL: " + ex.getMessage());
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
        SortedSet<Klucz> daneKluczy;
        PreparedStatement prepState;
        ResultSet result;
        String sqlStatement;
        
        ArrayList<String> rekord = new ArrayList<>();
        String danePola;
        String daneRekordu;

        ArrayList<String> kluczyk = tabelka.getKluczGlowny();

        try {
            
            // najpierw liczymy ile rekordów ma tabelka
            sqlStatement = "select count(*) from " + tabelka.getNazwaTabeli();
            prepState = getDbconnection().prepareStatement(sqlStatement);
            result = prepState.executeQuery();
            if(!result.next()) {
                logg.error("Nie można policzyć");
            }
            int ileRekordow = result.getInt(1);
            logg.debug("Liczba rekordów: " + ileRekordow);
            
            result.close();
            prepState.close();
            
            // przechodzimy do wypełnienia struktury danych zawierającej wrtości kluczy głównych
            sqlStatement = "select ";
            for (int ii = 0; ii < kluczyk.size(); ii++) {
                sqlStatement += kluczyk.get(ii) + ", ";
            }
            // ucinamy końcowy przecinek
            sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 2);
            sqlStatement += " from " + tabelka.getNazwaTabeli() + " "
                    + "order by ";
            for (int ii = 0; ii < kluczyk.size(); ii++) {
                sqlStatement += kluczyk.get(ii) + ", ";
            }
            // ucinamy końcowy przecinek
            sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 2);

            logg.debug(sqlStatement);
            prepState = getDbconnection().prepareStatement(sqlStatement);
            result = prepState.executeQuery();

            daneKluczy = new TreeSet<>();
            
            while (result.next()) {
                daneRekordu = "";
                // numerujemy od jednego, JDBC ma inną pragmatykę...
                for (int ii = 1; ii <= kluczyk.size(); ii++) {
                    danePola = result.getString(ii);
                    rekord.add(danePola);
                    daneRekordu += danePola + " ";
                }
                if (logg.isTraceEnabled()) {
                    logg.trace("Rec: " + daneRekordu);
                }
                
                if (!daneKluczy.add(new Klucz(rekord))) {
                    logg.debug("Nieudaczne dodanie");
                }
                rekord.clear();
            }  // while (result.next())
            
            logg.debug("Ile w kluczach: " + daneKluczy.size());

        } catch (SQLException ex) {
            logg.error("Błąd SQL (daneKluczowe): " + ex.getMessage());
            daneKluczy = null;
        }

        return daneKluczy;
    }
}
