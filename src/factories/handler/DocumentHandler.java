package factories.handler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import models.Document;
import models.structure.Newsitem;

/**
 * XmlHandler extends DefaultHandler to decode the XML file into self-defined
 * XmlStructure. 
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017 
 */
public class DocumentHandler extends DefaultHandler {
    
    private String textContent;
	private final Document xml;
	private ArrayList<String> elements;
	private int elementId;


	/**
	 * Constructor.
	 * @param filename indicates which file to be handled.
	 */
	public DocumentHandler() {
		/* N.B.: We are not interested in all other tags */
		String[] tags = new String[]{"newsitem", "headline", "text", "title"};

		xml = new Document();
		textContent = "";
		
		elements = new ArrayList<String>();
		for(String name : tags) {
			elements.add(name);
		}
		elementId = -1;
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
		if (elements.contains(qName)) {
			elementId = elements.indexOf(qName);
			
			if (elementId == elements.indexOf("newsitem")) {
	            xml.attributes = getAttributes(attributes);
	        }
		}

     
    }

    /**getAttributes 
	 * @since 1.0
	 *
	 * @param attributes is the attributes from the tags
	 * @return all the attributes as a Newsitem object
	 */
	private Newsitem getAttributes(Attributes attributes) {
		Newsitem newsitem = new Newsitem();
		
		try {
			newsitem.date = new SimpleDateFormat("yyyy-mm-dd").parse(attributes.getValue("date"));
		} catch (ParseException e) {
			System.out.println("Error while reading document. (Date in invalid format.)");
		}
		newsitem.id = attributes.getValue("id");
		newsitem.itemid = Integer.parseInt(attributes.getValue("itemid"));
		newsitem.id = attributes.getValue("xml:lang");
		return newsitem;
	}

	@Override
    /**
     * It runs while the handler found a XML closing tag. If found, then the btext
     * status is set to false, and the content read so far will be stored is this
     * xml content.
     * @since 1.0
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        /* store the finalized result of the text content into XmlStructure. */
        if (elementId == elements.indexOf("text")) {
            xml.text = textContent.replace("\n", " ").trim();
        }    
        
        /* reset the element ID only if the reader reach the closing tag of
         * the current element */
		if (elements.indexOf(qName) == elementId) {
	        elementId = -1;
		}
    }

    @Override
    /**
     * It runs while the handler found the content between the XML opening & closing
     * tags. It will append the context found to the textContent variable.
     * @since 1.0
     */
    public void characters(char ch[], int start, int length) throws SAXException {
    	String innerText = new String(ch, start, length);
        switch (elementId) {
	        case 0:
	        	/* We already handle the newsitem's attributes at startElement() */
	        	break;
	        case 1:
	        	xml.headline = innerText;
	        	break;
	        case 2:
	        	/* sum up the word size while this content is wrapped by the <text> tag.
	             */
	        	textContent = textContent.concat(new String(ch, start, length));
	        	break;
	        case 3:
	        	xml.title = innerText;
	        	break;
	        default:
	        	/* if no element id found, do nothing */
        }
    }
    
    /**
     * It return the XmlStructure of the current handling XML file.
     * @return XmlStructure that stored the context of the corresponding file.
     * @since 1.0
     */
    public Document getDocument() {
    	return xml;
    }
}
