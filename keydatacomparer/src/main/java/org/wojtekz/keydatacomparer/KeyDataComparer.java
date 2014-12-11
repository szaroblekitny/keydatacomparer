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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.BasicConfigurator;
// import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Main class of the <b>Key data comparer</b> project.<br>
 * <p>I have to fast compare the key data between two databases. As input I have
 * a XML file with list of tables to compare. Program connects to both
 * databases, reads the list of tables from configuration file, compares data in
 * records with the same primary key and finally produces report: records
 * missing in the databases and records with differences.</p>
 * <p>Configuration file contains informations about databases, tables to
 * compare and logging process</p>
 *
 * @author Wojciech Zaręba
 */
public class KeyDataComparer {

    private static Logger logg = Logger.getLogger(KeyDataComparer.class.getName());
    private static BazaDanych bazaWzorcowa;
    private static BazaDanych bazaPorownywana;
    private static File outputFile = new File("output.txt");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        // logg.setLevel(Level.INFO);
        logg.info("Key data comparer starts");

        if (args.length != 1) {
            logg.fatal("Argumentem jest nazwa pliku z tabelami do porównania");
            System.exit(1);
        }
        for (int ii = 0; ii < args.length; ii++) {
            logg.debug("arg[" + ii + "]: " + args[ii]);
        }

        try {
            ObsluzPliki obspli = new ObsluzPliki(args[0]);
            // nazwaBazy, nazwaHosta, numerPortu, schemat, haslo
            bazaWzorcowa = new OracleDB(obspli.getSourcedatabase(),
                    obspli.getSourcehostname(),
                    obspli.getSourceportnumber(),
                    obspli.getSourceusername(),
                    obspli.getSourceuserpassword());
            bazaPorownywana = new OracleDB(obspli.getCompdatabase(),
                    obspli.getComphostname(),
                    obspli.getCompportnumber(),
                    obspli.getCompusername(),
                    obspli.getCompuserpassword());
            
            try (FileWriter zapisywacz = new FileWriter(outputFile)) {
                zapisywacz.write("Porównanie baz \n");
                zapisywacz.write("Baza wzorcowa: " + bazaWzorcowa.getDatabase() +
                        "/" + bazaWzorcowa.getUsername() + "\n");
                zapisywacz.write("Baza porównywana: " + bazaPorownywana.getDatabase() + 
                        "/" + bazaPorownywana.getUsername() + "\n");
                // zapisywacz.close();
                
                Connection bwzorConn = bazaWzorcowa.databaseConnection();
                Connection bporowConn = bazaPorownywana.databaseConnection();

                if (logg.isTraceEnabled()) {
                    logg.trace("Mamy popr. połączenie wzor" + bwzorConn.isValid(10));
                    logg.trace("Mamy popr. połączenie por" + bporowConn.isValid(11));
                }
                Porownywacz por = new Porownywacz(zapisywacz);
                por.porownuj(bazaWzorcowa, bazaPorownywana, obspli);
                
                zapisywacz.write("======================================================\n");
                zapisywacz.write("**** KONIEC PORÓWNANIA ****\n");
                zapisywacz.close();

                if (bwzorConn != null) {
                    bwzorConn.close();
                    logg.debug("Połączenie bazy wzorcowej zamknięte");
                }
                
                if (bporowConn != null) {
                    bporowConn.close();
                    logg.debug("Połączenie bazy porównywanej zamknięte");
                }
            }
            
        } catch (SQLException ex) {
            logg.error("SQLException !! ", ex);
        } catch (IOException ex) {
            logg.error("IOException: " + ex.getMessage());
        } catch (SAXException ex) {
            logg.error("SAXException: " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            logg.error("ParserConfigurationException: " + ex.getMessage());
        } catch (Exception ex) {
            logg.error("Błędny błąd", ex);
        } finally {
            logg.info("Key data comparer ends");
        }
    }
}
