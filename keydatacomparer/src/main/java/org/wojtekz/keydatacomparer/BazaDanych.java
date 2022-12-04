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

/**
 * Represents a database. Must be implement for the database vendor (Oracle,
 * MySQL, PostgreSQL and so forth).
 *
 * @author Wojciech Zaręba
 */
public abstract class BazaDanych {
    protected String database;
    protected String hostname;
    protected int portnumber;
    protected String username;
    protected String userpassword;
    
    protected String connectUrl;
    /**
     * Database connection
     */
    protected Connection dbconnection;
    
    /**
     * List of database tables to compare
     */
    private List<Tabela> tabele = new ArrayList<>();

    /**
     * Default constructor. Sets database connection parameters only.
     * 
     * @param nazwaBazy database name
     * @param nazwaHosta host
     * @param numerPortu port number
     * @param schemat user name
     * @param haslo user password
     */
    public BazaDanych(String nazwaBazy,
                      String nazwaHosta,
                      int numerPortu,
                      String schemat,
                      String haslo) {
        this.database = nazwaBazy;
        this.hostname = nazwaHosta;
        this.portnumber = numerPortu;
        this.username = schemat;
        this.userpassword = haslo;
    }
    
    /**
     * Gives connection to the database. <p>Must be implemented for each database vendor.</p>
     * 
     * @return the database connection
     * @throws java.sql.SQLException
     */
    public abstract Connection databaseConnection()
            throws SQLException;
    
    
    /**
     * It reads primary key for given table from the database and adds primary key
     * fields as array to the tabelka object.
     * <p>Must be implemented for each database vendor.</p>
     * 
     * @param tabelka the database table representation object
     */
    public abstract void addPrimaryKey(Tabela tabelka);
    
    /**
     * Reads fields of the database table tabl and adds to the tabl object.
     * They are <b>not</b> in the primary key and data from they is compared.
     * <p>Must be implemented for each database vendor.</p>
     * 
     * @param tabl database table
     */
    public abstract void getFields(Tabela tabl);
    
    /**
     * Reads data from primary key fields and stores sorted as sorted set.
     * It will be used to compare of records data. Based on these key whole
     * records will be read and compaed their data.
     * 
     * <p>Must be implemented for each database vendor.</p>
     * 
     * @param tabelka database table
     * @return data for primary keys
     */
    public abstract SortedSet<Klucz> daneKluczowe(Tabela tabelka);
    
    
    /**
     * Select data from database to compare it.
     * 
     * @param tabela Tabela (table) object
     * @param klucz primary key object
     * @return SQL select for getting data to compare
     */
    public static String tworzenieSelecta(Tabela tabela, Klucz klucz) {

        StringBuilder sqlStatement = new StringBuilder("select ");

        for (int ii = 0; ii < tabela.getPolaTabeli().size(); ii++) {
            sqlStatement.append(tabela.getPolaTabeli().get(ii).getNazwaKolumnny()).append(", ");
        }
        // ucinamy końcowy przecinek
        String poUcPrzec = sqlStatement.substring(0, sqlStatement.toString().length() - 2);
        sqlStatement = new StringBuilder(poUcPrzec);
        sqlStatement.append(" from ").append(tabela.getNazwaTabeli()).append(" where ");

        for (int jj = 0; jj < tabela.getKluczGlowny().size(); jj++) {
            sqlStatement.append(tabela.getKluczGlowny().get(jj)).append(" = '")
                    .append(klucz.getLista().get(jj)).append("' and ");
        }
        // ucinamy końcowy and 
        String poUcAnd = sqlStatement.substring(0, sqlStatement.toString().length() - 5);
        sqlStatement = new StringBuilder(poUcAnd);
        sqlStatement.append(" order by ");
        for (int kk = 0; kk < tabela.getKluczGlowny().size(); kk++) {
            sqlStatement.append(tabela.getKluczGlowny().get(kk)).append(", ");
        }
        // ucinamy końcowy przecinek
        String finalSQL = sqlStatement.substring(0, sqlStatement.toString().length() - 2);

        return finalSQL;
    }

    /**
     * Gives database name from database field.
     * 
     * @return the database name String
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Gives host name (or IP) from hostname field.
     * 
     * @return the host name
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Gives user name from username field.
     * 
     * @return the user name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gives password to connect to user given by username.
     * @return the password
     */
    public String getUserpassword() {
        return userpassword;
    }

    /**
     * Gives port number used to establish database connection.
     * 
     * @return the port number
     */
    public int getPortnumber() {
        return portnumber;
    }

    /**
     * Gives connection URL used to establish database connection.
     * 
     * @return the connection URL
     */
    public String getConnectUrl() {
        return connectUrl;
    }

    /**
     * Gives Connection object to connected database.
     * 
     * @return the database Connection
     */
    public Connection getDbconnection() {
        return dbconnection;
    }

    /**
     * List of tables which will be compared.
     * 
     * @return the list of compared tables
     */
    public List<Tabela> getTabele() {
        return tabele;
    }
    
    /**
     * Concatenation of database and username String in form &lt;database&gt;:&lt;username&gt;.
     * 
     * @return String with schema and database for displaing
     */
    public String getSchemaAndDatabaseName() {
    	return this.database + ":" + this.username;
    }

    
}
