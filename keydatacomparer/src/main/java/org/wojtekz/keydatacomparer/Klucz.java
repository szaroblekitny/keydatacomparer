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
import org.apache.log4j.Logger;

/**
 * This class implements Comparable for the primary keys data read from the database.
 * 
 * @author Wojciech Zaręba
 */
public class Klucz implements Comparable<Klucz> {
    private final static Logger logg = Logger.getLogger(Klucz.class.getName());
    private ArrayList<String> lista = new ArrayList<>();
    private int dlugosc;
    
    /**
     * Cobstructor assigns parameter to private field.
     * 
     * @param listka
     */
    public Klucz(ArrayList<String> listka) {
        this.lista.addAll(listka);
        this.dlugosc = this.lista.size();
        if (logg.isTraceEnabled()) {
            for (int ii = 0; ii < this.dlugosc; ii++) {
                logg.trace("ii: " + ii + "->" + this.lista.get(ii));
            }
        }
    }
    
    /**
     * Concanets data to String with '$%^' between fields data. Then do comparation
     * on this String.
     * 
     * @param innaLista the object Klucz type to compare
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object
     */
    @Override
    public int compareTo(Klucz innaLista) {
        int wynikPorownania = 0;
        // int wielkoscListy = lista.size();
        ArrayList<String> innyArray = innaLista.getLista();
        StringBuilder lancuchListy = new StringBuilder();
        StringBuilder lancuchInnejListy = new StringBuilder();
        String lancuszek;
        
        if (logg.isTraceEnabled()) {
            logg.trace("wielkoscListy: " + this.getDlugosc());
        }
        
        for (int ii = 0; ii < this.getDlugosc(); ii++) {
            lancuszek = this.lista.get(ii) + "$%^";
            if (logg.isTraceEnabled()) {
            	logg.trace("ii " + ii + ": " + lancuszek);
            }
            lancuchListy.append(lancuszek);
        }
        
        if (logg.isTraceEnabled()) {
        	logg.trace("lancuchListy: " + lancuchListy.toString());
        }
        
        for (int jj = 0; jj < innyArray.size(); jj++) {
            lancuszek = innyArray.get(jj) + "$%^";
            if (logg.isTraceEnabled()) {
                logg.trace("jj " + jj + ": " + lancuszek);
            }
            lancuchInnejListy.append(lancuszek);
        }
        
        if (logg.isTraceEnabled()) {
        	logg.trace("lancuchInnejListy: " + lancuchInnejListy.toString());
        }
        
        if ("".equals(lancuchListy.toString()) || "".equals(lancuchInnejListy.toString())) {
            // nigdy nie zachodzi w teorii
            wynikPorownania = 0;
        } else {
            wynikPorownania = lancuchListy.toString().compareTo(lancuchInnejListy.toString());
        }
        
        if (logg.isTraceEnabled()) {
        	logg.trace("wynikPorownania: " + wynikPorownania);
        }
        
        return wynikPorownania;
    }

    /**
     * @return the ArrayList list
     */
    public ArrayList<String> getLista() {
        return lista;
    }

    /**
     * @return the dlugosc
     */
    public int getDlugosc() {
        return dlugosc;
    }

}
