/**
 * 
 */
package factories;

import java.io.FileNotFoundException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import factories.handler.DocumentHandler;
import models.Document;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 8, 2017
 */
public class DocumentFactory {
	private Document doc;
	
	/**
	 * Constructor
	 */
	public DocumentFactory() {
		doc = new Document();
	}
	
	public void setDocument(String uri) {
		DocumentHandler handler = new DocumentHandler();
		doc = handler.getDocument();
		try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();         
            saxParser.parse(uri, handler);
        } catch (FileNotFoundException e) {
        	System.out.println("File not found.\n" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public int getDocId() {
		return doc.attributes.itemid;
	}
	
	public String getTitle() {
		return doc.title;
	}
	
	public String getText() {
		return doc.text;
	}
}
