package lib.files;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Document;

/**
 * XmlReader is used to read a target xml file and return the content in XmlStructure.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017
 */
public class DocumentReader {
	private Document xml;
	
	/**
	 * Constructor to initialize all attributes.
	 * 
	 * @param dir is the directory path of the data source.
	 * @since 1.0
	 */
	public DocumentReader() {
		xml = new Document();
	}
	
	/**
	 * It reads the XML file and store the relevant information into a XmlStructure
	 * object.
	 * 
	 * @return XmlStruture object containing information of the XML file.
	 * @since 1.0
	 */
	public Document ReadDocument(String resourceFile) {
		DocumentHandler handler = new DocumentHandler();
		xml = handler.getDocument();
		
		try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();         
            saxParser.parse(resourceFile, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return xml;
	}

}
