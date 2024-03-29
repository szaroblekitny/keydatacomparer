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

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Compares tables from two databases. It connects to both databases and for
 * each tables, for each records using primary key fetch records and compares
 * data. It reports it in the output file:
 * <ul>
 * <li>number of records in the source database
 * <li>number of records in the compared database
 * <li>missing records in the compared database which are in the source
 * <li>missing records in the source database which are in the other
 * <li>different records with the same primary key
 * </ul>
 *
 * @author Wojciech Zaręba
 */
public class Porownywacz {

    private final static Logger LOGG = LogManager.getLogger(Porownywacz.class.getName());
    private static final int ILE_ZNAKOW_W_POLU = 30;
    private SortedSet<Klucz> daneKluczyWzorcowych;
    private SortedSet<Klucz> daneKluczyPorownywanych;
    private FileWriter zapisywacz;
    private Tabela tabela;

    /**
     * Constructor - set writer to write final report.
     * 
     * @param writer something must write
     * @throws IOException
     */
    public Porownywacz(FileWriter writer) throws IOException {
        zapisywacz = writer;
    }

    /**
     * The main method for comparison. For each table it add fields (read from database)
     * for primary key columns and the rest colmns of table which content will be compared.
     * Then it read data from tables and do comparison. Results are stored as report
     * in a file.
     *
     * @param wzorzec source database
     * @param kopia compared database
     * @param nazwyTabel names of compared tables
     * @throws IOException file exceptions
     * @throws SQLException database exceptions
     *
     */
    public void porownuj(BazaDanych wzorzec, BazaDanych kopia, List<String> nazwyTabel)
            throws IOException, SQLException {
    	
        for (int nrTabeli = 0; nrTabeli < nazwyTabel.size(); nrTabeli++) {

            zapisywacz.write("---------------------------------------------\n");
            Tabela nowaTabela = new Tabela(nazwyTabel.get(nrTabeli));
            // tabela = wzorzec.addDatabaseTable(obspli.getNazwyTabel().get(nrTabeli));
            tabela = nowaTabela;
            wzorzec.addPrimaryKey(tabela);
            
            if (LOGG.isDebugEnabled()) {
            	logKluczGlowny();
            }

            zapisywacz.write("Comparison for table " + tabela.getNazwaTabeli() + "\n");

            // Tu jest podskórne założenie, że pola tabeli w obu bazach są identyczne,
            // oczywiście nie musi być to prawdą. W każdym razie bierzemy tabele z bazy
            // wzorcowej.
            wzorzec.getFields(tabela);

            List<KolumnaTabeli> polaTabeli = tabela.getPolaTabeli();
            
            logPolTabeli(polaTabeli);

            // dla ułatwienia analizy wypisujemy pola tabeli 
            zapisywacz.write("Table columns:\n");
            String wypPola = "";
            
            for (KolumnaTabeli kolumna : polaTabeli) {
            	wypPola = wypPola.concat(kolumna.getNazwaKolumnny()).concat("|");
            }
            
            zapiszPola(wypPola);

            /*
             * Dla każdej tabelki robimy selekty i łapiemy klucze główne do zbioru sortowanego (SortedSet).
             * Przez porównanie tych danych widać, których rekordów brakuje.
             * Potem dla każdego rekordu klucza robimy selecta z pozostałych danych i pokazujemy
             * różnice.
             */
            daneKluczyWzorcowych = wzorzec.daneKluczowe(tabela);
            LOGG.debug("Source key data:");
            logDaneKluczy(daneKluczyWzorcowych, wzorzec);
            
            daneKluczyPorownywanych = kopia.daneKluczowe(tabela);
            LOGG.debug("Compared key data:");
            logDaneKluczy(daneKluczyPorownywanych, kopia);

            LOGG.debug("Difference for source data");
            // metoda addAll zwraca true, jeśli dodawana kolekcja zmienia wielkość zbioru
            // i daje sumę zbiorów
            // metoda removeAll daje niesymetryczną różnicę zbiorów i zwraca true, jeśli operacja
            // zmienia wielkość zbioru
            SortedSet<Klucz> tmpSet = new TreeSet<Klucz>(daneKluczyWzorcowych);
            
            if (!tmpSet.addAll(daneKluczyPorownywanych)) {
                LOGG.debug("The source data does not change after adding the compared data");
            } else {
                LOGG.debug("Successful addition of compare records");
                SortedSet<Klucz> kopiaWzor = new TreeSet<>(daneKluczyWzorcowych);
                SortedSet<Klucz> kopiaKopii = new TreeSet<>(daneKluczyPorownywanych);
                pokazRoznice(kopiaKopii, kopiaWzor, "Records that are in the compared data but not in the source (key fields values)");
            }
            
            // czyścimy i sprawdzamy w drugą stronę
            tmpSet.clear();
            LOGG.debug("Difference for compare data");
            tmpSet.addAll(daneKluczyPorownywanych);
            if (!tmpSet.addAll(daneKluczyWzorcowych)) {
            	LOGG.debug("The compare data does not change after adding the source data");
            } else {
            	LOGG.debug("Successful addition of source records");
            	SortedSet<Klucz> kopiaWzor = new TreeSet<>(daneKluczyWzorcowych);
                SortedSet<Klucz> kopiaKopii = new TreeSet<>(daneKluczyPorownywanych);
                pokazRoznice(kopiaWzor, kopiaKopii, "Records that are in the source data but not in the compared data (key fields values)");
            }

			LOGG.debug("Intersection");
			/*
			 *  Najpierw tworzymy zbiór wszystkich rekordów. Potem stosujemy metodę retainAll,
			 *  która zachowuje w zbiorze wspolneRekordy tylko elementy występujące kolejno w obu
			 *  zbiorach.
			 */
			tmpSet.clear();
			LOGG.debug("Source data added");
			wyswietlDebugKluczy(daneKluczyWzorcowych);
			tmpSet.addAll(daneKluczyWzorcowych);
			LOGG.debug("Compare data added");
			wyswietlDebugKluczy(daneKluczyPorownywanych);
			tmpSet.retainAll(daneKluczyPorownywanych);
			LOGG.debug("Intersection");
			wyswietlDebugKluczy(tmpSet);
			
			if (tmpSet.isEmpty()) {
				LOGG.debug("Intersection is empty");
			} else {
				pokazRozneRekordy(tmpSet, wzorzec.getDbconnection(), kopia.getDbconnection());
			}

        }

    }

    /////////  privates  ////////////////////////////////////////////////////////////////////

    private void zapiszPola(String pola) throws IOException {
    	if (pola.length() > 0) {
    		pola = pola.substring(0, pola.length() - 1);
        	zapisywacz.write(pola + "\n");
        } else {
        	zapisywacz.write("No columns in the table\n");
        }
    }

    //--------------------------------------------------------------------------------------
    private void logDaneKluczy(Set<Klucz> setKluczy, BazaDanych baza) {
    	if (setKluczy != null) {
        	if (LOGG.isDebugEnabled()) {
        		wyswietlDebugKluczy(setKluczy);
        	}
        } else {
            LOGG.warn("No primary key data for {}/{}", baza.getSchemaAndDatabaseName(), tabela.getNazwaTabeli());
        }
    }

    //---------------------------------------------------------------------------------------
    private void logKluczGlowny() {
    	LOGG.debug("Primary key for table " + tabela.getNazwaTabeli() + ":");
        if (tabela.getKluczGlowny().isEmpty()) {
        	LOGG.debug("== No primary key ==");
        } else {
        	LOGG.debug(tabela.getKluczGlowny().toString());
        }
    }

    // --------------------------------------------------------------------------------------
    private void logPolTabeli(List<KolumnaTabeli> polaTabeli) {
    	if (LOGG.isDebugEnabled()) {
    		for(KolumnaTabeli kol : polaTabeli) {
    			LOGG.debug("Table {} has columns: {} {} {} {}",
    					tabela.getNazwaTabeli(),
    					kol.getNazwaKolumnny(),
    					kol.getTypDanych(),
    					kol.getPrecyzja(),
    					kol.getSkala()
    					);
    		}
    	}
	}

    //---------------------------------------------------------------------------------------

    private void wyswietlDebugKluczy(Set<Klucz> klucze) {
		if (klucze != null) {
			LOGG.debug("Size of the key data: {}", klucze.size());

			if (LOGG.isTraceEnabled()) {
				List<String> polaKlucza;
				String str = "";
				for (Klucz klucz : klucze) {
					polaKlucza = klucz.getLista();
					for (String pole: polaKlucza) {
						str = str.concat(pole).concat(" ");
					}

					LOGG.trace("Key fields: {}", str);
					str = "";
				}
			}
		} else {
			LOGG.warn("SortedSet klucze is null!");
		}
    }

    // --------------------------------------------------------------------------------------
    private void pokazRoznice(SortedSet<Klucz> co, SortedSet<Klucz> doCzego, String komunikat)
            throws IOException {
        String wypRozn = "";

        LOGG.debug("pokazRoznice begins");
        wyswietlDebugKluczy(co);
        wyswietlDebugKluczy(doCzego);
        
        co.removeAll(doCzego);
        if (!co.isEmpty()) {  // są rekordy w różnicy
        	if (LOGG.isDebugEnabled()) {
        		LOGG.debug("pokazRoznice message: " + komunikat);
        	}
            zapisywacz.write("\n" + komunikat + ":\n");
            for (Iterator<Klucz> it = co.iterator(); it.hasNext();) {
                Klucz kl = it.next();
                for (int ii = 0; ii < kl.getLista().size(); ii++) {
                    wypRozn += kl.getLista().get(ii) + "|";
                }
                wypRozn = wypRozn.substring(0, wypRozn.length() - 1);
                zapisywacz.write(wypRozn + "\n");
                wypRozn = "";
            }

        }
    }

    //---------------------------------------------------------------------------------------

    private boolean saRozne(int ileKolumn, ResultSet resultWzor, ResultSet resultKopia)
    		throws SQLException {
    	boolean rozne = false;
    	String wartWzor;
        String wartKopii;

        for (int ii = 1; ii <= ileKolumn; ii++) {
            LOGG.debug("Column {}", ii);
            wartWzor = resultWzor.getString(ii);
            wartKopii = resultKopia.getString(ii);

            LOGG.debug("In the source: {} in the compare data: {}", wartWzor, wartKopii);

            if (wartWzor == null ? wartKopii != null : !wartWzor.equals(wartKopii)) {
                rozne = true;
                break;
            }
        }

    	return rozne;
    }

    // --------------------------------------------------------------------------------------

    private void pokazRozneRekordy(SortedSet<Klucz> wspolne,
            Connection wzorzecConn,
            Connection kopiaConn) throws SQLException, IOException {

        String sqlStatement;

        zapisywacz.write("\nDifferent records\n");
        for (Klucz iter : wspolne) {
            sqlStatement = BazaDanych.tworzenieSelecta(tabela, iter);
            
            LOGG.trace("sqlStatement: >{}<", sqlStatement);
            
            try (
            		PreparedStatement prepStWzor = wzorzecConn.prepareStatement(sqlStatement);
            		ResultSet resultWzor = prepStWzor.executeQuery();
            		PreparedStatement prepStKopia = kopiaConn.prepareStatement(sqlStatement);
            		ResultSet resultKopia = prepStKopia.executeQuery()) {
            
	            ResultSetMetaData rsmd = resultWzor.getMetaData();
	            LOGG.debug("Number of columns in the table {}: {}", tabela.getNazwaTabeli(), rsmd.getColumnCount());

	            boolean rozne;
	            // teoretycznie powinien byc jeden rekord
	            while (resultWzor.next()) {
	            	if (!resultKopia.next()) {
	            		throw new SQLException("The number of records in the copy is different than in the source");
	            	}

	                rozne = saRozne(rsmd.getColumnCount(), resultWzor, resultKopia);

	                if (rozne) {
	                    zapisywacz.write(" Source data: ");
	                    wypiszDaneRekordow(resultWzor);

	                    zapisywacz.write("Compare data: ");
	                    wypiszDaneRekordow(resultKopia);
	                }

	            }   // while (resultWzor.next())
            }
            zapisywacz.write("\n");

        }   /// liczba wspólnych rekordów

    }

    // --------------------------------------------------------------------------------------
    private void wypiszDaneRekordow(ResultSet result)
    		throws IOException, SQLException {
    	
        String linia = "";
        String pole;
        int ileZnakow;
        String typPola;
        
        LOGG.debug("wypiszDaneRekordow fired");

        for (int ii = 1; ii <= tabela.getPolaTabeli().size(); ii++) {
        	if (LOGG.isDebugEnabled()) {
        		LOGG.debug("Column " + ii + " in table: " + tabela.getNazwaTabeli());
        	}
        	
            ileZnakow = tabela.getPolaTabeli().get(ii - 1).getSzerokosc();
            if (ileZnakow == 0) {
            	ileZnakow = ILE_ZNAKOW_W_POLU;
            }
            
            if (LOGG.isTraceEnabled()) {
            	LOGG.trace("Number of characters in the column: " + ileZnakow);
            }
            
            pole = result.getString(ii);
            if (pole == null) {
                pole = "";
            }

            // jeśli typ danych zawiera CHAR, to formatuj na lewo
            typPola = tabela.getPolaTabeli().get(ii - 1).getTypDanych();
            if (LOGG.isTraceEnabled()) {
            	LOGG.trace("column: " + pole + " type: " + typPola);
            }
            if (typPola.contains("CHAR")) {
                pole = padRight(pole, ileZnakow);
            } else {
                pole = padLeft(pole, ileZnakow);
            }
            linia += pole + "|";
        }

        linia = linia.substring(0, linia.length() - 1);
        linia = linia.trim();

        LOGG.debug("Data line: {}", linia);
        zapisywacz.write(linia + "\n");
    }

    

    // --------------------------------------------------------------------------------------
    private String padRight(String str, int n) {
    	if (LOGG.isTraceEnabled()) {
        	LOGG.trace("format: " + "%1$-" + n + "s **>" + str);
        }
        return String.format("%1$-" + n + "s", str);
    }

    // --------------------------------------------------------------------------------------
    private String padLeft(String str, int n) {
    	if (LOGG.isTraceEnabled()) {
        	LOGG.trace("format: " + "%1$" + n + "s **>" + str);
        }
        return String.format("%1$" + n + "s", str);
    }
}
