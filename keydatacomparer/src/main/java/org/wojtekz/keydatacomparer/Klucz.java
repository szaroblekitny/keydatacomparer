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
 * This class implements Comparable for compared data read from a database.
 * This is only for search records with the same primary key values. Comparison
 * the rest of data is done by reading data from database.
 * 
 * @author Wojciech Zaręba
 */
public class Klucz implements Comparable<Klucz> {
    private final static Logger LOGG = LogManager.getLogger(Klucz.class.getName());
    private List<String> lista = new ArrayList<>();
    private int dlugosc;
    
    /**
     * The constructor sets primary key fields values. On this base objects Klucz
     * are stored in a SortedSet object.
     * 
     * @param listka list of primary key fields
     */
    public Klucz(List<String> listka) {
        this.lista.addAll(listka);
        this.dlugosc = this.lista.size();
        if (LOGG.isTraceEnabled()) {
            for (int ii = 0; ii < this.dlugosc; ii++) {
                LOGG.trace("ii: " + ii + "->" + this.lista.get(ii));
            }
        }
    }
    
    /**
     * Concanets data to String with '$%^' between fields data. Then do comparison
     * on this String.
     * 
     * @param innaLista the object Klucz type to compare
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object
     */
    @Override
    public int compareTo(Klucz innaLista) {
    	LOGG.trace("compareTo begins");

        int wynikPorownania = 0;
        // int wielkoscListy = lista.size();
        List<String> innyArray = innaLista.getLista();
        StringBuilder lancuchListy = new StringBuilder();
        StringBuilder lancuchInnejListy = new StringBuilder();
        String lancuszek;

        if (LOGG.isTraceEnabled()) {
            LOGG.trace("wielkoscListy: " + this.getDlugosc());
        }

        for (int ii = 0; ii < this.getDlugosc(); ii++) {
            lancuszek = this.lista.get(ii) + "$%^";
            if (LOGG.isTraceEnabled()) {
            	LOGG.trace("ii " + ii + ": " + lancuszek);
            }
            lancuchListy.append(lancuszek);
        }

        if (LOGG.isTraceEnabled()) {
        	LOGG.trace("lancuchListy: " + lancuchListy.toString());
        }

        for (int jj = 0; jj < innyArray.size(); jj++) {
            lancuszek = innyArray.get(jj) + "$%^";
            if (LOGG.isTraceEnabled()) {
                LOGG.trace("jj " + jj + ": " + lancuszek);
            }
            lancuchInnejListy.append(lancuszek);
        }

        if (LOGG.isTraceEnabled()) {
        	LOGG.trace("lancuchInnejListy: " + lancuchInnejListy.toString());
        }

        if ("".equals(lancuchListy.toString()) || "".equals(lancuchInnejListy.toString())) {
            // nigdy nie zachodzi w teorii
            wynikPorownania = 0;
        } else {
            wynikPorownania = lancuchListy.toString().compareTo(lancuchInnejListy.toString());
        }

        if (LOGG.isTraceEnabled()) {
        	LOGG.trace("wynikPorownania: " + wynikPorownania);
        }

        return wynikPorownania;
    }


    @Override
    public boolean equals(Object obj) {
    	LOGG.trace("Klucz equals starts");
    	if (obj instanceof Klucz) {
    		if (this.getLista().size() != ((Klucz)obj).getLista().size()) {
    			return false;
    		}
    		
    		for (int ii = 0; ii < this.getLista().size(); ii++) {
    			if (!((Klucz)obj).getLista().get(ii).equals(this.getLista().get(ii))) {
    				return false;
    			}
    		}
    	} else {
    		return false;
    	}

    	LOGG.trace("Klucz equals fine equal");
    	return true;
    }


    @Override
    public int hashCode() {
    	int hashCode = 0;

    	for (String str : this.getLista()) {
    		hashCode += str.hashCode();
    	}

    	return hashCode;
    }


    /**
     * Primary key fields data.
     * 
     * @return list of fields content (String) which is unique for this key
     */
    public List<String> getLista() {
        return lista;
    }

    /**
     * Number of primary key fields.
     * 
     * @return integer value
     */
    public int getDlugosc() {
        return dlugosc;
    }

}
