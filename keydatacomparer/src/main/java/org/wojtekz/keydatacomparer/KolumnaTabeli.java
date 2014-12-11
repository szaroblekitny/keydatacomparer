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
