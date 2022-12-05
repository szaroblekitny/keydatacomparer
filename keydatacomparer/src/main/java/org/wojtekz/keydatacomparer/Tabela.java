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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a database table. It has a primary key and table field names.
 * The Comparer class fetches records and performs a comparison.
 *
 * @author Wojciech Zaręba
 */
public class Tabela {

    private final static Logger LOGG = LogManager.getLogger(Tabela.class.getName());

    // Properties of class Tabela
    private String nazwaTabeli;
    private List<String> kluczGlowny = new ArrayList<>();
    private List<KolumnaTabeli> polaTabeli = new ArrayList<>();

    
    /**
     * Creates table with name only, the rest must be included later.
     * @param nazwa
     */
    public Tabela(String nazwa) {
    	if (LOGG.isDebugEnabled()) {
    		LOGG.debug("New empty table " + nazwa);
    	}
        this.nazwaTabeli = nazwa;
    }
    
    /**
     * Creates table with primary key.
     *
     * @param nazwa table name
     * @param kluGlu primary key
     */
    public Tabela(String nazwa, List<String> kluGlu) {
        this.nazwaTabeli = nazwa;
        this.kluczGlowny = kluGlu;
    }

    /**
     * Adds column described only by name and type.
     * 
     * @param kolumna column name
     * @param typ column type
     */
    public void dodajKolumne(String kolumna, String typ) {
        
        KolumnaTabeli kolumnaTabeli = new KolumnaTabeli();
        kolumnaTabeli.setNazwaKolumnny(kolumna);
        kolumnaTabeli.setTypDanych(typ);
        boolean add = polaTabeli.add(kolumnaTabeli);
        if (LOGG.isTraceEnabled()) {
        	LOGG.trace("Column added: " + kolumna + " - " + add);
        }
    }
    
    /**
     * Adds column with more detailed description.
     * 
     * @param kolumna column name
     * @param typ column typ
     * @param precyzja precision
     * @param skala scale
     * @param szerokosc width
     */
    public void dodajKolumne(String kolumna,
            String typ,
            int precyzja,
            int skala,
            int szerokosc) {
        
        KolumnaTabeli kolumnaTabeli = new KolumnaTabeli();
        kolumnaTabeli.setNazwaKolumnny(kolumna);
        kolumnaTabeli.setTypDanych(typ);
        kolumnaTabeli.setPrecyzja(precyzja);
        kolumnaTabeli.setSkala(skala);
        kolumnaTabeli.setSzerokosc(szerokosc);
        if (polaTabeli.add(kolumnaTabeli)) {
            if (LOGG.isTraceEnabled()) {
                LOGG.trace("Column added: " + kolumna);
            }
        } else {
        	if (LOGG.isDebugEnabled()) {
        		LOGG.debug("Column addition failed for " + kolumna);
        	}
        }
    }
    

    /**
     * Set fields of primary key for this table.
     * 
     * @param kluGlu the list of fields for primary key to set
     */
    public void setKluczGlowny(List<String> kluGlu) {
        this.kluczGlowny = kluGlu;
    }
    
    /**
     * Returns table name.
     * 
     * @return nazwaTabeli name of the table
     */
    public String getNazwaTabeli() {
        return nazwaTabeli;
    }

    /**
     * Returns list of fields for primary key.
     * 
     * @return kluczGlowny list of fields
     */
    public List<String> getKluczGlowny() {
        return kluczGlowny;
    }

    /**
     * Returns list of columns for the table. Each column has name and type stored
     * in KolumnaTabeli object.
     * 
     * @return polaTabeli list of columns with their data
     */
    public List<KolumnaTabeli> getPolaTabeli() {
        return polaTabeli;
    }

}
