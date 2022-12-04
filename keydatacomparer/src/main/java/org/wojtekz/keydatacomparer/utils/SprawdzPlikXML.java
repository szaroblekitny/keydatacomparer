package org.wojtekz.keydatacomparer.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Util class to check XML files against xsd schema.
 * 
 * @author Wojtek Zaręba
 *
 */
public class SprawdzPlikXML {
	private final static Logger logg = LogManager.getLogger(SprawdzPlikXML.class.getName());
	
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	private DocumentBuilderFactory docFactory;
	private SchemaFactory factory;
	private ErrorHandler errHandler;
	private DocumentBuilder dBuilder;
	
	public SprawdzPlikXML() throws ParserConfigurationException {
		docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        dBuilder = docFactory.newDocumentBuilder();
        
        factory = SchemaFactory.newInstance(W3C_XML_SCHEMA);
		// próbuję złapać błędy w ErrorHandler
        errHandler = new DefaultHandler();
        factory.setErrorHandler(errHandler);
	}
	
	/**
	 * Check formal XML file against XSD schema.
	 * 
	 * @param xsdSchemaStream xsd schema as InputStream
	 * @param plikXML xml file to check
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void sprawdzFormalnie(InputStream xsdSchemaStream, File plikXML) 
			throws IOException, ParserConfigurationException, SAXException {
		
		if (!plikXML.exists() || xsdSchemaStream == null) {
            logg.error("Szukam " + plikXML.getName() + " " + plikXML.exists());
            throw new IOException("brak upragnionych plików");
        }

		Document docxml = dBuilder.parse(plikXML);
        
        if (logg.isDebugEnabled()) {
        	logg.debug("Base URI: " + docxml.getBaseURI());
        }
        
        // teraz trzeba sprawdzić, że plik XML jest poprawny względem pliku xsd
        Source xsdSource = new StreamSource(xsdSchemaStream);
        Schema schema = factory.newSchema(xsdSource);
        Validator validator = schema.newValidator();
        Source source = new StreamSource(plikXML);
        // Sprawdzenie
        validator.validate(source);
        
	}

	/**
	 * Creates XML Document from XML file.
	 * 
	 * @param plikXML
	 * @return XML Document
	 * @throws SAXException when parsing went wrong
	 * @throws IOException when is problem with file
	 */
	public Document zrobDocXMLZpliku(File plikXML) throws SAXException, IOException {
		Document docxml = dBuilder.parse(plikXML);
		return docxml;
	}
	
}
