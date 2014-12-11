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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


// import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

    private static Logger logg = Logger.getLogger(Porownywacz.class.getName());
    private Set<Klucz> daneKluczyWzorcowych;
    private Set<Klucz> daneKluczyPorownywanych;
    private Set<Klucz> roznicaWzorca = new HashSet<>();
    private Set<Klucz> wspolneRekordy;
    private FileWriter zapisywacz;
    private Tabela tabela;

    public Porownywacz(FileWriter writer) throws IOException {
        zapisywacz = writer;
        // logg.setLevel(Level.TRACE);
    }

    /**
     * The main method for comparison.
     *
     * @param wzorzec source database
     * @param kopia compared database
     * @param obspli the program input parameters
     * @throws IOException
     */
    public void porownuj(BazaDanych wzorzec,
            BazaDanych kopia,
            ObsluzPliki obspli)
            throws IOException, SQLException {

        // logg.setLevel(Level.TRACE);
        for (int nrTabeli = 0; nrTabeli < obspli.getNazwyTabel().size(); nrTabeli++) {

            zapisywacz.write("---------------------------------------------\n");
            Tabela nowaTabela = new Tabela(obspli.getNazwyTabel().get(nrTabeli));
            // tabela = wzorzec.addDatabaseTable(obspli.getNazwyTabel().get(nrTabeli));
            tabela = nowaTabela;
            wzorzec.addPrimaryKey(tabela);
            
            if (logg.isDebugEnabled()) {
                logg.debug("Klucz główny dla " + tabela.getNazwaTabeli());
                logg.debug(tabela.getKluczGlowny().toString());
            }

            zapisywacz.write("Porównanie dla tabeli " + tabela.getNazwaTabeli() + "\n");

            // TODO Tu jest podskórne założenie, że pola tabeli w obu bazach są identyczne,
            // oczywiście nie musi być to prawdą. W każdym razie bierzemy tabele z bazy
            // wzorcowej.
            wzorzec.getFields(tabela);

            ArrayList<Tabela.KolumnyTabeli> polaTabeli = tabela.getPolaTabeli();

            if (logg.isDebugEnabled()) {
                for (int ii = 0; ii < polaTabeli.size(); ii++) {
                    logg.debug("Pola tabeli " + tabela.getNazwaTabeli() + ": "
                            + polaTabeli.get(ii).getNazwaKolumnny()
                            + " " + polaTabeli.get(ii).getTypDanych()
                            + " " + polaTabeli.get(ii).getPrecyzja()
                            + " " + polaTabeli.get(ii).getSkala());
                }
            }

            // dla ułatwienia analizy wypisujemy pola tabeli 
            zapisywacz.write("Pola tabeli:\n");
            String wypPola = "";
            for (int ii = 0; ii < polaTabeli.size(); ii++) {
                wypPola += polaTabeli.get(ii).getNazwaKolumnny() + "|";
            }
            
            if (wypPola.length() > 0) {
            	wypPola = wypPola.substring(0, wypPola.length() - 1);
            	zapisywacz.write(wypPola + "\n");
            } else {
            	zapisywacz.write("Brak pól w tabeli\n");
            }

            /*
             * Dla każdej tabelki robimy selekty i łapiemy klucze główne do zbioru sortowanego (SortedSet).
             * Przez porównanie tych danych widać, których rekordów brakuje.
             * Potem dla każdego rekordu klucza robimy selecta z pozostałych danych i pokazujemy
             * różnice.
             */
            daneKluczyWzorcowych = wzorzec.daneKluczowe(tabela);
            if (logg.isDebugEnabled()) {
            	if (daneKluczyWzorcowych != null)
            		wyswietlDebugKluczy((SortedSet<Klucz>) daneKluczyWzorcowych);
                else
                	logg.warn("Brak danych z kluczy głównych dla "
                           + wzorzec.getSchemaAndDatabaseName() + "/" + tabela.getNazwaTabeli());
            }

            daneKluczyPorownywanych = kopia.daneKluczowe(tabela);
            if (logg.isDebugEnabled()) {
            	if (daneKluczyPorownywanych != null)
            		wyswietlDebugKluczy((SortedSet<Klucz>) daneKluczyPorownywanych);
            	else
            		logg.warn("Brak danych z kluczy głównych dla "
                            + kopia.getSchemaAndDatabaseName() + "/" + tabela.getNazwaTabeli());
            }

            logg.debug("Różnica wzorca");
            // metoda addAll zwraca true, jeśli dodawana kolekcja zmienia wielkość zbioru
            // i daje sumę zbiorów
            // metoda removeAll daje niesymetryczną różnicę zbiorów i zwraca true, jeśli operacja
            // zmienia wielkość zbioru
            roznicaWzorca.addAll(daneKluczyWzorcowych);
            if (!roznicaWzorca.addAll(daneKluczyPorownywanych)) {
                logg.debug("brak różnic w danych kluczy");
            } else {
                logg.debug("Dodanie udane");
                pokazRoznice((SortedSet<Klucz>) daneKluczyWzorcowych, (SortedSet<Klucz>) daneKluczyPorownywanych,
                        "Rekordy, które są we wzorcu, a nie ma w porównaniu");
                pokazRoznice((SortedSet<Klucz>) daneKluczyPorownywanych, (SortedSet<Klucz>) daneKluczyWzorcowych,
                        "Rekordy, które są w porównaniu, a nie ma we wzorcu");
            }

			if (daneKluczyWzorcowych != null && daneKluczyPorownywanych != null) {
				logg.debug("Część wspólna");
				wspolneRekordy = new TreeSet<>();
				/*
				 *  Najpierw tworzymy zbiór wszystkich rekordów. Potem stosujemy metodę retainAll,
				 *  która zachowuje w zbiorze wspolneRekordy tylko elementy występujące kolejno w obu
				 *  zbiorach.
				 */
				if (!wspolneRekordy.addAll(daneKluczyWzorcowych) && !wspolneRekordy.addAll(daneKluczyPorownywanych)) {
					logg.debug("pusto w danych kluczy");
				} else {
					if (wspolneRekordy.retainAll(daneKluczyPorownywanych)
							|| wspolneRekordy.retainAll(daneKluczyWzorcowych)) {
						if (logg.isDebugEnabled()) {
							logg.debug("empty: " + wspolneRekordy.isEmpty());  
						}
						pokazRozneRekordy((SortedSet<Klucz>)wspolneRekordy,
								wzorzec.getDbconnection(),
								kopia.getDbconnection());
					} else {
						logg.debug("retain nic nie zmienia");
					}
				}
			} else {
				logg.error("BRAK KLUCZOWYCH DANYCH");
			}

        }

    }

    /////////  privates  ////////////////////////////////////////////////////////////////////
    private void wyswietlDebugKluczy(SortedSet<Klucz> klucze) {
		if (klucze != null) {
			String str = "";
			logg.debug("Dane kluczy porow: " + klucze.size());
			for (Klucz iterator : klucze) {
				for (int ii = 0; ii < iterator.getDlugosc(); ii++) {
					str += iterator.getLista().get(ii) + " ";
				}
				if (logg.isTraceEnabled()) {
					logg.trace(str);
				}
				str = "";
			}
		} else {
			logg.warn("Klucze puste!");
		}
    }

    // --------------------------------------------------------------------------------------
    private void pokazRoznice(SortedSet<Klucz> co, SortedSet<Klucz> doCzego, String komunikat)
            throws IOException {
        String wypRozn = "";

        logg.debug("pokazRoznice");
        wyswietlDebugKluczy(co);
        wyswietlDebugKluczy(doCzego);
        
        co.removeAll(doCzego);
        if (!co.isEmpty()) {  // są rekordy w różnicy
            logg.debug(komunikat);
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

    // --------------------------------------------------------------------------------------
    private void pokazRozneRekordy(SortedSet<Klucz> wspolne,
            Connection wzorzecConn,
            Connection kopiaConn) throws SQLException, IOException {

        PreparedStatement prepStWzor;
        ResultSet resultWzor;
        PreparedStatement prepStKopia;
        ResultSet resultKopia;
        String sqlStatement;

        zapisywacz.write("\nRóżne rekordy w obu bazach\n");
        for (Klucz iter : wspolne) {
            sqlStatement = tworzenieSelecta(iter);
            
            if (logg.isTraceEnabled()) {
                logg.trace("sqlStatement: " + sqlStatement);
            }
            
            prepStWzor = wzorzecConn.prepareStatement(sqlStatement);
            resultWzor = prepStWzor.executeQuery();
            prepStKopia = kopiaConn.prepareStatement(sqlStatement);
            resultKopia = prepStKopia.executeQuery();
            ResultSetMetaData rsmd;
            // logg.debug("Wz: " + resultWzor.);

            boolean saRozne;
            String wartWzor;
            String wartKopii;

            while (resultWzor.next()) {
                if (!resultKopia.next()) {
                    throw new SQLException("Liczba rekordów w kopii inna niż we wzorcu");
                }

                saRozne = false;
                rsmd = resultWzor.getMetaData();
                for (int ii = 1; ii <= rsmd.getColumnCount(); ii++) {
                    wartWzor = resultWzor.getString(ii);
                    wartKopii = resultKopia.getString(ii);
                    if (wartWzor == null ? wartKopii != null : !wartWzor.equals(wartKopii)) {
                        saRozne = true;
                        break;
                    }
                }

                if (saRozne) {
                    zapisywacz.write("Wzor : ");
                    wypiszDaneRekordow(resultWzor);

                    zapisywacz.write("Kopia: ");
                    wypiszDaneRekordow(resultKopia);
                }
            }
            zapisywacz.write("\n");

        }   /// liczba wspólnych rekordów

    }

    // --------------------------------------------------------------------------------------
    private void wypiszDaneRekordow(ResultSet result) throws IOException, SQLException {
        String linia = "";
        String pole;
        int ileZnakow;
        String typPola;

        for (int ii = 1; ii <= tabela.getPolaTabeli().size(); ii++) {
            ileZnakow = tabela.getPolaTabeli().get(ii - 1).getSzerokosc();
            logg.trace("znaki: " + ileZnakow);
            pole = result.getString(ii);
            if (pole == null) {
                pole = "";
            }

            // jeśli typ danych zawiera CHAR, to formatuj na lewo
            typPola = tabela.getPolaTabeli().get(ii - 1).getTypDanych();
            logg.trace("Typ pola: " + typPola);
            if (typPola.indexOf("CHAR") > 0) {
                pole = padRight(pole, ileZnakow);
            } else {
                pole = padLeft(pole, ileZnakow);
            }
            linia += pole + "|";
        }

        linia = linia.substring(0, linia.length() - 1);
        linia = linia.trim();
        logg.trace("linia: " + linia);
        zapisywacz.write(linia + "\n");
    }

    // --------------------------------------------------------------------------------------
    // TODO przenieść do podklasy BazaDanych
    private String tworzenieSelecta(Klucz klucz) {

        String sqlStatement = "select ";

        for (int ii = 0; ii < tabela.getPolaTabeli().size(); ii++) {
            sqlStatement += tabela.getPolaTabeli().get(ii).getNazwaKolumnny() + ", ";
        }
        // ucinamy końcowy przecinek
        sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 2);
        sqlStatement += " from " + tabela.getNazwaTabeli() + " where ";

        for (int jj = 0; jj < tabela.getKluczGlowny().size(); jj++) {
            sqlStatement += tabela.getKluczGlowny().get(jj) + " = '"
                    + klucz.getLista().get(jj) + "' and ";
        }
        // ucinamy końcowy and 
        sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 5);

        sqlStatement += " order by ";
        for (int kk = 0; kk < tabela.getKluczGlowny().size(); kk++) {
            sqlStatement += tabela.getKluczGlowny().get(kk) + ", ";
        }
        // ucinamy końcowy przecinek
        sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 2);

        return sqlStatement;
    }

    // --------------------------------------------------------------------------------------
    private String padRight(String str, int n) {
        return String.format("%1$-" + n + "s", str);
    }

    private String padLeft(String str, int n) {
        return String.format("%1$" + n + "s", str);
    }
}
