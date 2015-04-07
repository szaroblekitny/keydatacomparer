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

/**
 *  Represents fields (columns) of the table. It have properties for each
 *  field: field name, data type, and for numeric fields precision and scale.
 *  
 *  @author Wojciech Zaręba
 */
public class KolumnaTabeli {
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
    KolumnaTabeli() {
    	
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

	public void setNazwaKolumnny(String nazwaKolumnny) {
		this.nazwaKolumnny = nazwaKolumnny;
	}

	public void setTypDanych(String typDanych) {
		this.typDanych = typDanych;
	}

	public void setPrecyzja(int precyzja) {
		this.precyzja = precyzja;
	}

	public void setSkala(int skala) {
		this.skala = skala;
	}

	public void setSzerokosc(int szerokosc) {
		this.szerokosc = szerokosc;
	}
    
    
}
