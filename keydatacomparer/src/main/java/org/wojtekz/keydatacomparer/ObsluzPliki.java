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
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wojtekz.keydatacomparer.utils.SprawdzPlikXML;
import org.xml.sax.SAXException;

/**
 * Reads and examines XML files.
 * <ul>
 * <li>checks if xml and xsd file exist</li>
 * <li>checks xml file against xsd schema</li>
 * <li>reads database connection parameters</li>
 * <li>reads tables for comparison</li>
 * <li>reads log parameters</li>
 * </ul>
 * 
 * All is stored in the class properties. 
 *
 * @author Wojciech Zaręba
 */
public class ObsluzPliki {

    private final static Logger LOGG = Logger.getLogger(ObsluzPliki.class.getName());
    
    private String sourcedatabase;
    private String sourcehostname;
    private int sourceportnumber;
    private String sourceusername;
    private String sourceuserpassword;
    private String compdatabase;
    private String comphostname;
    private int compportnumber;
    private String compusername;
    private String compuserpassword;
    private List<String> nazwyTabel = new ArrayList<>();

    /**
     * Reads XML file and checks it against xsd/keydatacomparer.xsd file.
     * 
     * @param plikXML the XML configuration file.
     * 
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public ObsluzPliki(String plikXML)
            throws IOException, SAXException, ParserConfigurationException {
        obsluga(plikXML, "xsd/keydatacomparer.xsd");
    }
    
    // public sprawdzenieFormalne

    /**
     * Service for configuration file.
     * 
     * @param plik1
     * @param plik2
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private void obsluga(String plik1, String plik2)
            throws IOException, SAXException, ParserConfigurationException {
        File plikXML = new File(plik1);
        File plikXSD = new File(plik2);

        SprawdzPlikXML.sprawdzFormalnie(plikXSD, plikXML);
        
        if (LOGG.isDebugEnabled()) {
        	LOGG.info(plikXML + " jest poprawny");
        }
        
        Document docxml = SprawdzPlikXML.zrobDocXMLZpliku(plikXML);
        
        Element xmlElem = docxml.getDocumentElement();
        if (LOGG.isDebugEnabled()) {
        	LOGG.debug("Root: " + xmlElem.getNodeName());
        }
        ///--- sourcedatabase
        NodeList sourcedbNL = docxml.getElementsByTagName("sourcedatabase");
        if (sourcedbNL != null && sourcedbNL.getLength() > 0) {
            for (int ii = 0; ii < sourcedbNL.getLength(); ii++) {

                Node node = sourcedbNL.item(ii);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elm = (Element) node;
                    NodeList nodeList = elm.getElementsByTagName("host");
                    this.sourcehostname = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("sourcehostname: " + this.sourcehostname);
                    }
                    nodeList = elm.getElementsByTagName("port");
                    this.sourceportnumber = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("sourceportnumber: " + this.sourceportnumber);
                    }
                    nodeList = elm.getElementsByTagName("name");
                    this.sourcedatabase = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("sourcedatabase: " + this.sourcedatabase);
                    }
                    nodeList = elm.getElementsByTagName("username");
                    this.sourceusername = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("sourceusername: " + this.sourceusername);
                    }
                    nodeList = elm.getElementsByTagName("userpassword");
                    this.sourceuserpassword = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("sourceuserpassword: " + this.sourceuserpassword);
                    }
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
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("comphostname: " + this.comphostname);
                    }
                    nodeList = elm.getElementsByTagName("port");
                    this.compportnumber = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("compportnumber: " + this.compportnumber);
                    }
                    nodeList = elm.getElementsByTagName("name");
                    this.compdatabase = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("compdatabase: " + this.compdatabase);
                    }
                    nodeList = elm.getElementsByTagName("username");
                    this.compusername = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("compusername: " + this.compusername);
                    }
                    nodeList = elm.getElementsByTagName("userpassword");
                    this.compuserpassword = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("compuserpassword: " + this.compuserpassword);
                    }
                }
            }
        }

        ///--- tables
        String tableName;

        LOGG.debug("Tables:");
        sourcedbNL = docxml.getElementsByTagName("table");
        if (LOGG.isDebugEnabled()) {
            LOGG.debug("długość NL: " + sourcedbNL.getLength());
        }
        if (sourcedbNL != null && sourcedbNL.getLength() > 0) {
            for (int ii = 0; ii < sourcedbNL.getLength(); ii++) {
                Node node = sourcedbNL.item(ii);
                if (LOGG.isDebugEnabled()) {
                    LOGG.debug("node: " + node.getNodeName() + " type: " + node.getNodeType());
                }
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    tableName = node.getTextContent();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug(ii + ": " + tableName);
                    }
                    this.getNazwyTabel().add(tableName);
                }
            }
        }
        ///---

        /// TODO - dorobić konfigurację loggera

    }  // end of obsluga


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
    public int getSourceportnumber() {
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
    public int getCompportnumber() {
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
    public List<String> getNazwyTabel() {
        return nazwyTabel;
    }
}
