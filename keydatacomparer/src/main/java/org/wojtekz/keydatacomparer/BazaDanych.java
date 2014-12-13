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
    private ArrayList<Tabela> tabele = new ArrayList<>();

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
     * Gives connection to the database.
     * 
     * @return the database connection
     * @throws java.sql.SQLException
     */
    public abstract Connection databaseConnection()
            throws SQLException;
    
    
    /**
     * It adds primary key array to table.
     * 
     * @param tabelka the database table representation object
     */
    public abstract void addPrimaryKey(Tabela tabelka);
    
    /**
     * Gets fields of the database table tabl. They are not in the primary
     * key and they data is compared.
     * 
     * @param tabl database table
     */
    public abstract void getFields(Tabela tabl);
    
    /**
     * Creates data from primary key sorted set for table tabelka. It will be used
     * to find missing records and compare rest of records data.
     * 
     * @param tabelka database table
     * @return data for primary keys
     */
    public abstract SortedSet<Klucz> daneKluczowe(Tabela tabelka);
    
    
    /**
     * Select data from database to compare it.
     * 
     * @param tabela
     * @param klucz
     * @return SQL select for getting data to compare
     */
    public static String tworzenieSelecta(Tabela tabela, Klucz klucz) {

        StringBuilder sqlStatement = new StringBuilder("select ");

        for (int ii = 0; ii < tabela.getPolaTabeli().size(); ii++) {
            sqlStatement.append(tabela.getPolaTabeli().get(ii).getNazwaKolumnny()).append(", ");
        }
        // ucinamy końcowy przecinek
        sqlStatement.substring(0, sqlStatement.toString().length() - 2);
        sqlStatement.append(" from ").append(tabela.getNazwaTabeli()).append(" where ");

        for (int jj = 0; jj < tabela.getKluczGlowny().size(); jj++) {
            sqlStatement.append(tabela.getKluczGlowny().get(jj)).append(" = '")
                    .append(klucz.getLista().get(jj)).append("' and ");
        }
        // ucinamy końcowy and 
        sqlStatement.substring(0, sqlStatement.toString().length() - 5);

        sqlStatement.append(" order by ");
        for (int kk = 0; kk < tabela.getKluczGlowny().size(); kk++) {
            sqlStatement.append(tabela.getKluczGlowny().get(kk)).append(", ");
        }
        // ucinamy końcowy przecinek
        sqlStatement.substring(0, sqlStatement.toString().length() - 2);

        return sqlStatement.toString();
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the userpassword
     */
    public String getUserpassword() {
        return userpassword;
    }

    /**
     * @return the portnumber
     */
    public int getPortnumber() {
        return portnumber;
    }

    /**
     * @return the connectUrl
     */
    public String getConnectUrl() {
        return connectUrl;
    }

    /**
     * @return the dbconnection
     */
    public Connection getDbconnection() {
        return dbconnection;
    }

    /**
     * @return the ArrayList of database tables
     */
    public ArrayList<Tabela> getTabele() {
        return tabele;
    }
    
    /**
     * 
     * @return String with schema and database for displaing
     */
    public String getSchemaAndDatabaseName() {
    	return this.database + ":" + this.username;
    }

    
}
