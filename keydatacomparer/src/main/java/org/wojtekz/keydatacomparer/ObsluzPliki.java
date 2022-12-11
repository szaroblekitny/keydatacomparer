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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final static Logger LOGG = LogManager.getLogger(ObsluzPliki.class.getName());
    
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
     * It reads XML file and checks it against xsd/keydatacomparer.xsd file.
     * 
     * @param plikXML the XML configuration file.
     * 
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws ClassNotFoundException 
     */
    public ObsluzPliki(String plikXML)
            throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException {
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
     * @throws ClassNotFoundException 
     */
    private void obsluga(String plik1, String plik2)
            throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException {

   		LOGG.info("obsluga --> plik1: {} plik2: {}", plik1, plik2);

        File plikXML = new File(plik1);

        Class<?> cls = Class.forName("org.wojtekz.keydatacomparer.ObsluzPliki");

        // returns the ClassLoader object associated with this Class
        ClassLoader cLoader = cls.getClassLoader();
        // input stream
        InputStream xsdStream = cLoader.getResourceAsStream(plik2);
        SprawdzPlikXML sprawdzarka = new SprawdzPlikXML();
        sprawdzarka.sprawdzFormalnie(xsdStream, plikXML);

        LOGG.info("File {} is valid", plikXML.getName());

        Document docxml = sprawdzarka.zrobDocXMLZpliku(plikXML);
        Element xmlElem = docxml.getDocumentElement();
        LOGG.debug("Root: " + xmlElem.getNodeName());
        
        ///--- sourcedatabase
        NodeList fileNodeList = docxml.getElementsByTagName("sourcedatabase");
        rozszyjDaneBazy(fileNodeList, true);
        
        LOGG.debug("sourcehostname: {}", this.sourcehostname);
        LOGG.debug("sourceportnumber: {}", this.sourceportnumber);
        LOGG.info("Source database: {}", this.sourcedatabase);
        LOGG.debug("sourceusername: {}", this.sourceusername);
        LOGG.debug("sourceuserpassword: {}", this.sourceuserpassword);

        ///--- NodeList for compareddatabase
        fileNodeList = docxml.getElementsByTagName("compareddatabase");
        rozszyjDaneBazy(fileNodeList, false);
        
        LOGG.debug("comphostname: {}", this.comphostname);
        LOGG.debug("compportnumber: {}", this.compportnumber);
        LOGG.info("Compared database: {}", this.compdatabase);
        LOGG.debug("compusername: {}", this.compusername);
        LOGG.debug("compuserpassword: {}", this.compuserpassword);

        ///--- tables
        String tableName;

        LOGG.debug("Tables:");
        fileNodeList = docxml.getElementsByTagName("table");
        LOGG.debug("NodeList length: {}", fileNodeList.getLength());

        if (fileNodeList != null && fileNodeList.getLength() > 0) {
            for (int ii = 0; ii < fileNodeList.getLength(); ii++) {
                Node node = fileNodeList.item(ii);
                if (LOGG.isDebugEnabled()) {
                    LOGG.debug("node: " + node.getNodeName() + " type: " + node.getNodeType());
                }
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    tableName = node.getTextContent();
                    if (LOGG.isDebugEnabled()) {
                    	LOGG.debug("Element " + ii + ": " + tableName);
                    }
                    this.getNazwyTabel().add(tableName);
                }
            }
        }

        LOGG.debug("The end of obsluga");
    }  // end of obsluga

    //-------------------------------------------------------------------------------------------------

    private void rozszyjDaneBazy(NodeList fileNodeList, boolean zrodlowa) {
    	String hostname = null;
    	int portnumber = 100000;
    	String database = null;
    	String username = null;
    	String password = null;

    	if (fileNodeList != null && fileNodeList.getLength() > 0) {
            for (int ii = 0; ii < fileNodeList.getLength(); ii++) {

                Node node = fileNodeList.item(ii);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elm = (Element) node;
                    NodeList nodeList = elm.getElementsByTagName("host");
                    hostname = nodeList.item(0).getChildNodes().item(0).getNodeValue();

                    nodeList = elm.getElementsByTagName("port");
                    portnumber = Integer.parseInt(nodeList.item(0).getChildNodes().item(0).getNodeValue());
	                
                    nodeList = elm.getElementsByTagName("name");
                    database = nodeList.item(0).getChildNodes().item(0).getNodeValue();

                    nodeList = elm.getElementsByTagName("username");
                    username = nodeList.item(0).getChildNodes().item(0).getNodeValue();

                    nodeList = elm.getElementsByTagName("userpassword");
                    password = nodeList.item(0).getChildNodes().item(0).getNodeValue();
                }
            }
        }

    	if (zrodlowa) {
			this.sourcehostname = hostname;
			this.sourceportnumber = portnumber;
			this.sourcedatabase = database;
			this.sourceusername = username;
			this.sourceuserpassword = password;
    	} else {
    		this.comphostname = hostname;
			this.compportnumber = portnumber;
			this.compdatabase = database;
			this.compusername = username;
			this.compuserpassword = password;
    	}
    }

    //-------------------------------------------------------------------------------------------------

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
     * @return the nazwyTabel list
     */
    public List<String> getNazwyTabel() {
        return nazwyTabel;
    }
}
