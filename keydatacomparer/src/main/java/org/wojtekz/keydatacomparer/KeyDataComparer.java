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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final static Logger LOGG = LogManager.getLogger(KeyDataComparer.class.getName());
    private static BazaDanych bazaWzorcowa;
    private static BazaDanych bazaPorownywana;
    private static File outputFile;
    
    // private constructor hides instatiation of this class
    private KeyDataComparer() {
    }

    /**
     * Main method of application. Reads configuration file and makes comparison.
     * 
     * @param args the command line arguments: config file name and optional output file name
     */
    public static void main(String[] args) {
        LOGG.debug("Key data comparer starts");

        if (args.length != 1 && args.length != 2) {
            LOGG.fatal("The parameter is the name of the file with the tables to be compared (xml format) and, optionally, the name of the report file.");
            throw new RuntimeException("Parameter is expected");
        }
        
        if (args.length == 2) {
        	outputFile = new File(args[1]);
        } else {
        	outputFile = new File("output.txt");
        }
        
        for (int ii = 0; ii < args.length; ii++) {
            LOGG.debug("arg[" + ii + "]: " + args[ii]);
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
            
            porownajIZapisz(obspli);
            
            LOGG.info("Results saved in file " + outputFile);

        } catch (SQLException ex) {
            LOGG.error("SQLException !! ", ex);
        } catch (IOException ex) {
            LOGG.error("IOException: " + ex.getMessage(), ex);
        } catch (SAXException ex) {
            LOGG.error("SAXException: " + ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            LOGG.error("ParserConfigurationException: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            LOGG.error("Wrong error: ", ex);
        } finally {
            LOGG.debug("Key data comparer ends");
        }

        // closing the log manager
        LogManager.shutdown();
    }
    
    /**
     * Makes comparison and writes output.
     * 
     * @param obslugaPlikow reader and parser for configuration XML file
     * @throws IOException
     * @throws SQLException
     */
    private static void porownajIZapisz(ObsluzPliki obslugaPlikow) throws IOException, SQLException {
    	try (FileWriter zapisywacz = new FileWriter(outputFile)) {
	        zapisywacz.write("Databases to compare:\n");
	        zapisywacz.write("Source: " + bazaWzorcowa.getDatabase() +
	                    "/" + bazaWzorcowa.getUsername() + "\n");
	        zapisywacz.write("Compared: " + bazaPorownywana.getDatabase() +
	                    "/" + bazaPorownywana.getUsername() + "\n");

	        Connection bwzorConn = bazaWzorcowa.databaseConnection();
	        Connection bporowConn = bazaPorownywana.databaseConnection();

	        if (LOGG.isTraceEnabled()) {
	            LOGG.trace("Source connection is valid: " + bwzorConn.isValid(10));
	            LOGG.trace("Compared connection is valid: " + bporowConn.isValid(11));
	        }
	        Porownywacz por = new Porownywacz(zapisywacz);
	        por.porownuj(bazaWzorcowa, bazaPorownywana, obslugaPlikow.getNazwyTabel());

	        zapisywacz.write("======================================================\n");
	        zapisywacz.write("**** THE END OF COMPARISON ****\n");
	        zapisywacz.close();


	        if (bwzorConn != null) {
	            bwzorConn.close();
	            if (bwzorConn.isClosed()) {
	            	LOGG.debug("Source connection is closed");
	            }
	        }

	        if (bporowConn != null) {
	            bporowConn.close();
	            if (bporowConn.isClosed()) {
	            	LOGG.debug("Compare connection is closed");
	            }
	        }

    	}
 
    }
}
