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

import org.apache.log4j.Logger;

/**
 * Represents the database table. It have primary key and fields of the table
 * given by name. Class Porownywacz fetch records of the table and do
 * comparation.
 *
 * @author Wojciech Zaręba
 */
public class Tabela {

    private final static Logger LOGG = Logger.getLogger(Tabela.class.getName());

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
    		LOGG.debug("Nowa pusta tabela " + nazwa);
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
        	LOGG.trace("Dodałem kolumnę " + kolumna + " - " + add);
        }
    }
    
    /**
     * Adds column for more detailed description.
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
                LOGG.trace("Dodałem kolumnę " + kolumna);
            }
        } else {
        	if (LOGG.isDebugEnabled()) {
        		LOGG.debug("Dodanie kolumny " + kolumna + " nieudane");
        	}
        }
    }
    

    /**
     * @param kluGlu the list of fields for primary key to set
     */
    public void setKluczGlowny(List<String> kluGlu) {
        this.kluczGlowny = kluGlu;
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
    public List<String> getKluczGlowny() {
        return kluczGlowny;
    }

    /**
     * @return the polaTabeli
     */
    public List<KolumnaTabeli> getPolaTabeli() {
        return polaTabeli;
    }

}
