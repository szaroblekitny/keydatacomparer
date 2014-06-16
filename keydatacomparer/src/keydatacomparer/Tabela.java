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

import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Represents the database table. It have primary key and fields of the table
 * given by name. Class Porownywacz fetch records of the table and do
 * comparation.
 *
 * @author Wojciech Zaręba
 */
public class Tabela {

    private static Logger logg = Logger.getLogger(Tabela.class.getName());

    /**
     *  Represents fields (columns) of the table. It have properties for each
     *  field: field name, data type, and for numeric fields precision and scale.
     */
    public class KolumnyTabeli {
        /*
         *   TODO
         * 
         *   Kolumny tabel powinny mieć znormalizowane nazwy typów, niezależne od
         *   implementacji bazy danych: char, number, date, boolean. W klasie implementacyjnej
         *   powinien być transformator typów, który przekształca typy używane
         *   w konkretnej bazie danych na typy znormalizowane.
         *   Ale to nie na tym etapie projektu. Na razie niech to zadziała dla Oracle'a.
         */

        private String nazwaKolumnny;
        private String typDanych;
        private int precyzja;
        private int skala;
        private int szerokosc;

        /**
         * Default constructor. Do nothing.
         */
        KolumnyTabeli() {
            // super();
            // logg.debug("Kolumna tabeli " + nazwaTabeli + ":");
        }

        /**
         * @return the field name
         */
        String getNazwaKolumnny() {
            return nazwaKolumnny;
        }

        /**
         * @return the datatype
         */
        String getTypDanych() {
            return typDanych;
        }

        /**
         * @return the precision
         */
        int getPrecyzja() {
            return precyzja;
        }

        /**
         * @return the scale
         */
        int getSkala() {
            return skala;
        }
        
        /**
         * @return the length of data
         */
        int getSzerokosc() {
            return szerokosc;
        }
        
    }
    // End of class KolumnyTabeli
    
    // Properties of class Tabela
    private String nazwaTabeli;
    private ArrayList<String> kluczGlowny = new ArrayList<>();
    private ArrayList<KolumnyTabeli> polaTabeli = new ArrayList<>();

    
    /**
     * Empty constructor, nazwaTabeli and kluczGlowny must be set separately.
     */
    public Tabela() {
        logg.debug("Nowa pusta tabela");
    }
    
    /**
     * Creates table with name only, the rest must be included later.
     * @param nazwa
     */
    public Tabela(String nazwa) {
        logg.debug("Nowa pusta tabela " + nazwa);
        this.nazwaTabeli = nazwa;
    }
    
    /**
     * Creates table with primary key.
     *
     * @param nazwa table name
     * @param kluGlu primary key
     */
    public Tabela(String nazwa, ArrayList<String> kluGlu) {
        this.nazwaTabeli = nazwa;
        this.kluczGlowny = kluGlu;
        // KolumnyTabeli kolumny = new KolumnyTabeli();
    }

    public void dodajKolumne(String kolumna, String typ) {
        
        KolumnyTabeli kolumnaTabeli = new KolumnyTabeli();
        kolumnaTabeli.nazwaKolumnny = kolumna;
        kolumnaTabeli.typDanych = typ;
        boolean add = polaTabeli.add(kolumnaTabeli);
        logg.debug("Dodałem kolumnę (1) " + kolumna + "? " + add);
    }
    
    public void dodajKolumne(String kolumna,
            String typ,
            int precyzja,
            int skala,
            int szerokosc) {
        
        KolumnyTabeli kolumnaTabeli = new KolumnyTabeli();
        kolumnaTabeli.nazwaKolumnny = kolumna;
        kolumnaTabeli.typDanych = typ;
        kolumnaTabeli.precyzja = precyzja;
        kolumnaTabeli.skala = skala;
        kolumnaTabeli.szerokosc = szerokosc;
        if (polaTabeli.add(kolumnaTabeli)) {
            if (logg.isTraceEnabled()) {
                logg.trace("Dodałem kolumnę " + kolumna);
            }
        } else {
            logg.debug("Dodanie kolumny " + kolumna + " nieudane");
        }
    }
    
    /**
     * @param nazwaTabeli the name of table to set
     */
    public void setNazwaTabeli(String nazwaTabeli) {
        this.nazwaTabeli = nazwaTabeli;
    }

    /**
     * @param kluczGlowny the list of fields for primary key to set
     */
    public void setKluczGlowny(ArrayList<String> kluczGlowny) {
        this.kluczGlowny = kluczGlowny;
    }
    
    /**
     * @return the nazwaTabeli
     */
    public String getNazwaTabeli() {
        return nazwaTabeli;
    }

    /**
     * @return the kluczGlowny
     */
    public ArrayList<String> getKluczGlowny() {
        return kluczGlowny;
    }

    /**
     * @return the polaTabeli
     */
    public ArrayList<KolumnyTabeli> getPolaTabeli() {
        return polaTabeli;
    }

}
