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
package org.wojtekz.keydatacomparer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class DaneTestowe {
	
	public static File tworzPlikTestowyXML() throws IOException {
		String nazwaPliku = UUID.randomUUID().toString() + ".xml";
		File testFile = new File(nazwaPliku);
		String zawartosc = "<keydatacomparer>                      \n" +
				"    <databases>                                   \n" +
				"        <sourcedatabase>                          \n" +
				"          <host>localhost</host>                  \n" +
				"            <port>1521</port>                     \n" +
				"            <name>ora11</name>                    \n" +
				"            <username>hr</username>               \n" +
				"            <userpassword>password</userpassword> \n" +
				"        </sourcedatabase>                         \n" +
				"                                                  \n" +
				"        <compareddatabase>                        \n" +
				"            <host>localhost</host>                \n" +
				"            <port>1521</port>                     \n" +
				"            <name>ora11</name>                    \n" +
				"            <username>scott</username>            \n" +
				"            <userpassword>password</userpassword> \n" +
				"        </compareddatabase>                       \n" +
				"                                                  \n" +
				"    </databases>                                  \n" +
				"    <tables>                                      \n" +
				"        <table>CONFIGURACJA</table>               \n" +
				"        <table>RECONFIGURACJA</table>             \n" +
				"    </tables>                                     \n" +
				"    <logging file=\"log4j.xml\" />                \n" +
				"</keydatacomparer>                                \n";
		FileWriter fileWritter = new FileWriter(testFile.getName());
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(zawartosc);
        bufferWritter.close();
        fileWritter.close();
        
        return testFile;
	}

}
