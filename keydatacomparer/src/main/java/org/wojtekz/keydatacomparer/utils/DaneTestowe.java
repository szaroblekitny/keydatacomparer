package org.wojtekz.keydatacomparer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class DaneTestowe {
	
	private DaneTestowe() {
	}

	public static File tworzPlikTestowyXML() throws IOException {
		String nazwaPliku = UUID.randomUUID().toString() + ".xml";
		File testFile = new File(nazwaPliku);
		String zawartosc =
				"<keydatacomparer>                                 \n" +
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
				"</keydatacomparer>                                \n";
		FileWriter fileWritter = new FileWriter(testFile.getName());

		try (BufferedWriter bufferWritter = new BufferedWriter(fileWritter)) {
			bufferWritter.write(zawartosc);
		}

        fileWritter.close();

        return testFile;
	}

}
