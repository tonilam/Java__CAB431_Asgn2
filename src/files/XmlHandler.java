package lib.files;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XmlHandler extends DefaultHandler to decode the XML file into self-defined
 * XmlStructure. 
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017 
 */
public class XmlHandler extends DefaultHandler {
    /* this boolean variable indicate rather the searching text is inside
	   the <TEXT> tag or not */
    boolean btext; 
    
    String textContent;
	private final XmlStructure xml;


	/**
	 * Constructor.
	 * @param filename indicates which file to be handled.
	 */
	public XmlHandler(String filename) {
		xml = new XmlStructure();
		xml.setFileName(filename);
		btext = false;
		textContent = "";
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
    public void startElement(String uri, String localName,String qName,
    						 Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("newsitem")) {
            xml.setItemId(Integer.parseInt(attributes.getValue("itemid")));
        }

        if (qName.equalsIgnoreCase("text")) {
        	// indicate the text now processing is inside the <TEXT> tag.
            btext = true;
        }                    
    }

    @Override
    /**
     * It runs while the handler found a XML closing tag. If found, then the btext
     * status is set to false, and the content read so far will be stored is this
     * xml content.
     * @since 1.0
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("text")) {
        	// reset btext's state.
            btext = false;
            
            // store the finalized result of the text content into XmlStructure.
            xml.setContent(textContent);
        }    
    }

    @Override
    /**
     * It runs while the handler found the content between the XML opening & closing
     * tags. It will append the context found to the textContent variable.
     * @since 1.0
     */
    public void characters(char ch[], int start, int length) throws SAXException {
        /* sum up the word size while this content is wrapped by the <text> tag.
         */
        if (btext) {
        	textContent = textContent.concat(new String(ch, start, length));
        }

    }
    
    /**
     * It return the XmlStructure of the current handling XML file.
     * @return XmlStructure that stored the context of the corresponding file.
     * @since 1.0
     */
    public XmlStructure getXmlStructure() {
    	return xml;
    }
}
