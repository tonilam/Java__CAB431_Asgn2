/**
 * 
 */
package factories;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import factories.handler.DocumentHandler;
import lib.preprocessing.StoppingWords;
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

	/**getBow 
	 * @return 
	 * @since
	 *
	 */
	public ArrayList<String> getBOW() {
		ArrayList<String> bow = new ArrayList<String>();
		StoppingWords sw = new StoppingWords(".//src//resources//common-english-words.txt", true);
		
		String content = (doc.title + " " + doc.headline + " " + doc.text)
							.toLowerCase()				// generalize all characters to lower case
							.replaceAll("[\\W]", " ");	// break words that contain non-word characters.
		
		StringTokenizer st = new StringTokenizer(content);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (
            		(token.length() > 1)
            		&&
            		(!token.matches("\\d+"))
            		&&
            		(!sw.isStopWord(token))
            	) {
            	bow.add(token);
            }
        }   
            
		return bow;
	}
}
