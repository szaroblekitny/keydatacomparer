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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads and examines XML files
 *
 * @author Wojciech Zaręba
 */
public class ObsluzPliki {

    private static Logger logg = Logger.getLogger(ObsluzPliki.class.getName());
    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private SchemaFactory factory;
    private File plikXML;
    private File plikXSD;
    private String sourcedatabase;
    private String sourcehostname;
    private String sourceportnumber;
    private String sourceusername;
    private String sourceuserpassword;
    private String compdatabase;
    private String comphostname;
    private String compportnumber;
    private String compusername;
    private String compuserpassword;
    private ArrayList<String> nazwyTabel = new ArrayList<>();

    /**
     * Reads two files and checks if they are XML files
     *
     * @param plik1 the XML file
     * @param plik2 the schema file
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public ObsluzPliki(String plik1, String plik2)
            throws IOException, SAXException, ParserConfigurationException {
        obsluga(plik1, plik2);
    }

    public ObsluzPliki(String plikXML)
            throws IOException, SAXException, ParserConfigurationException {
        obsluga(plikXML, "xsd/keydatacomparer.xsd");
    }

    private void obsluga(String plik1, String plik2)
            throws IOException, SAXException, ParserConfigurationException {
        plikXML = new File(plik1);
        plikXSD = new File(plik2);

        if (!plikXML.exists() || !plikXSD.exists()) {
            logg.error("Szukam " + plikXML.getName() + " " + plikXML.exists());
            logg.error("Szukam " + plikXSD.getName() + " " + plikXSD.exists());
            throw new IOException("brak upragnionych plików");
        }

        this.factory = SchemaFactory.newInstance(W3C_XML_SCHEMA);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        // docFactory.setValidating(true);
        docFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = docFactory.newDocumentBuilder();
        Document docxml = dBuilder.parse(plikXML);
        logg.debug(docxml.getBaseURI());
        docFactory.setValidating(true);
        Document docxsd = dBuilder.parse(plikXSD);
        logg.debug(docxsd.getBaseURI());
        // tu wiemy, że oba pliki są plikami XML
        // teraz trzeba sprawdzić, że plik xsd jest poprawnym plikiem xsd
        Schema schema = factory.newSchema(plikXSD);
        Validator validator = schema.newValidator();
        Source source = new StreamSource(plikXML);
        // Sprawdzenie
        validator.validate(source);
        logg.info(plikXML + " jest poprawny");
        Element xmlElem = docxml.getDocumentElement();
        logg.debug("Root: " + xmlElem.getNodeName());

        ///--- sourcedatabase
        NodeList sourcedbNL = docxml.getElementsByTagName("sourcedatabase");
        if (sourcedbNL != null && sourcedbNL.getLength() > 0) {
            for (int ii = 0; ii < sourcedbNL.getLength(); ii++) {

                Node node = sourcedbNL.item(ii);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elm = (Element) node;
                    NodeList nodeList = elm.getElementsByTagName("host");
                    this.sourcehostname = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("sourcehostname: " + this.sourcehostname);
                    nodeList = elm.getElementsByTagName("port");
                    this.sourceportnumber = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("sourceportnumber: " + this.sourceportnumber);
                    nodeList = elm.getElementsByTagName("name");
                    this.sourcedatabase = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("sourcedatabase: " + this.sourcedatabase);
                    nodeList = elm.getElementsByTagName("username");
                    this.sourceusername = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("sourceusername: " + this.sourceusername);
                    nodeList = elm.getElementsByTagName("userpassword");
                    this.sourceuserpassword = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("sourceuserpassword: " + this.sourceuserpassword);
                }
            }
        }

        ///--- compareddatabase
        sourcedbNL = docxml.getElementsByTagName("compareddatabase");
        if (sourcedbNL != null && sourcedbNL.getLength() > 0) {
            for (int ii = 0; ii < sourcedbNL.getLength(); ii++) {

                Node node = sourcedbNL.item(ii);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elm = (Element) node;
                    NodeList nodeList = elm.getElementsByTagName("host");
                    this.comphostname = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("comphostname: " + this.comphostname);
                    nodeList = elm.getElementsByTagName("port");
                    this.compportnumber = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("compportnumber: " + this.compportnumber);
                    nodeList = elm.getElementsByTagName("name");
                    this.compdatabase = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("compdatabase: " + this.compdatabase);
                    nodeList = elm.getElementsByTagName("username");
                    this.compusername = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("compusername: " + this.compusername);
                    nodeList = elm.getElementsByTagName("userpassword");
                    this.compuserpassword = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    logg.debug("compuserpassword: " + this.compuserpassword);
                }
            }
        }

        ///--- tables
        String tableName;

        logg.debug("Tables:");
        sourcedbNL = docxml.getElementsByTagName("table");
        if (logg.isDebugEnabled()) {
            logg.debug("długość NL: " + sourcedbNL.getLength());
        }
        if (sourcedbNL != null && sourcedbNL.getLength() > 0) {
            for (int ii = 0; ii < sourcedbNL.getLength(); ii++) {
                Node node = sourcedbNL.item(ii);
                if (logg.isDebugEnabled()) {
                    logg.debug("node: " + node.getNodeName() + " type: " + node.getNodeType());
                }
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    tableName = node.getTextContent();
                    logg.debug(ii + ": " + tableName);
                    this.getNazwyTabel().add(tableName);
                }
            }
        }
        ///---

        /// TODO - dorobić konfigurację loggera

    }  // end of obsluga

    /**
     * @return the plikXML
     */
    public File getPlikXML() {
        return plikXML;
    }

    /**
     * @return the plikXSD
     */
    public File getPlikXSD() {
        return plikXSD;
    }

    /**
     * @return the sourcedatabase
     */
    public String getSourcedatabase() {
        return sourcedatabase;
    }

    /**
     * @return the sourcehostname
     */
    public String getSourcehostname() {
        return sourcehostname;
    }

    /**
     * @return the sourceportnumber
     */
    public String getSourceportnumber() {
        return sourceportnumber;
    }

    /**
     * @return the sourceusername
     */
    public String getSourceusername() {
        return sourceusername;
    }

    /**
     * @return the sourceuserpassword
     */
    public String getSourceuserpassword() {
        return sourceuserpassword;
    }

    /**
     * @return the compdatabase
     */
    public String getCompdatabase() {
        return compdatabase;
    }

    /**
     * @return the comphostname
     */
    public String getComphostname() {
        return comphostname;
    }

    /**
     * @return the compportnumber
     */
    public String getCompportnumber() {
        return compportnumber;
    }

    /**
     * @return the compusername
     */
    public String getCompusername() {
        return compusername;
    }

    /**
     * @return the compuserpassword
     */
    public String getCompuserpassword() {
        return compuserpassword;
    }

    /**
     * @return the nazwyTabel
     */
    public ArrayList<String> getNazwyTabel() {
        return nazwyTabel;
    }
}
