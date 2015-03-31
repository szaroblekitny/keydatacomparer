package org.wojtekz.keydatacomparer.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Util class to check XML files against xsd schema.
 * 
 * @author Wojtek Zaręba
 *
 */
public class SprawdzPlikXML {
	private final static Logger logg = Logger.getLogger(SprawdzPlikXML.class.getName());
	
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	private static DocumentBuilderFactory docFactory;
	private static SchemaFactory factory;
	private static DocumentBuilder dBuilder;
	
	/**
	 * Check formal XML file.
	 * 
	 * @param plikXSD xsd schema
	 * @param plikXML xml file to check
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static void sprawdzFormalnie(File plikXSD, File plikXML) 
			throws IOException, ParserConfigurationException, SAXException {
		
		if (!plikXML.exists() || !plikXSD.exists()) {
            logg.error("Szukam " + plikXML.getName() + " " + plikXML.exists());
            logg.error("Szukam " + plikXSD.getName() + " " + plikXSD.exists());
            throw new IOException("brak upragnionych plików");
        }

		Document docxml = zrobDocXMLZpliku(plikXML);
        
        if (logg.isDebugEnabled()) {
        	logg.debug(docxml.getBaseURI());
        }
        docFactory.setValidating(true);
        Document docxsd = dBuilder.parse(plikXSD);
        if (logg.isDebugEnabled()) {
        	logg.debug(docxsd.getBaseURI());
        }
        
        // tu wiemy, że oba pliki są plikami XML
        // teraz trzeba sprawdzić, że plik xsd jest poprawnym plikiem xsd
        Schema schema = factory.newSchema(plikXSD);
        Validator validator = schema.newValidator();
        Source source = new StreamSource(plikXML);
        // Sprawdzenie
        validator.validate(source);
        
	}
	
	/**
	 * Creates XML Document from a XML file.
	 * 
	 * @param plikXML input file
	 * @return XML Document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document zrobDocXMLZpliku(File plikXML) throws ParserConfigurationException, SAXException, IOException {
		factory = SchemaFactory.newInstance(W3C_XML_SCHEMA);

        docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        dBuilder = docFactory.newDocumentBuilder();
        Document docxml = dBuilder.parse(plikXML);
        
        return docxml;
	}

}
