package factories;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import models.TopicOfInterest;

/**
 * XmlHandler extends DefaultHandler to decode the XML file into self-defined
 * XmlStructure. 
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017 
 */
public class TopicOfInterestHandler extends DefaultHandler {  
	private final TopicOfInterest xml;


	/**
	 * Constructor.
	 * @param filename indicates which file to be handled.
	 */
	public TopicOfInterestHandler() {
		xml = new TopicOfInterest();
	}
	
    @Override
    /**
     * It runs while the handler found a XML opening tag. it will check if the tag
     * is either of these cases:
     * 1) If the tag is &lt;newsitem&gt;, then set the item id of this xml
     *    structure to the value of "itemid" in this tag; or
     * 2) If the tag is &lt;text&gt;, then store the btext status to true.
     * @since 1.0
     */
    public void startElement(String uri, String localName, String qName,
    						 Attributes attributes) throws SAXException {
    }

    @Override
    /**
     * It runs while the handler found a XML closing tag. If found, then the btext
     * status is set to false, and the content read so far will be stored is this
     * xml content.
     * @since 1.0
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }

    @Override
    /**
     * It runs while the handler found the content between the XML opening & closing
     * tags. It will append the context found to the textContent variable.
     * @since 1.0
     */
    public void characters(char ch[], int start, int length) throws SAXException {
    }
    
    /**
     * It return the XmlStructure of the current handling XML file.
     * @return XmlStructure that stored the context of the corresponding file.
     * @since 1.0
     */
    public TopicOfInterest getDocument() {
    	return xml;
    }
}
