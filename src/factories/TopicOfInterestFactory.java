/**
 * 
 */
package factories;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.TopicOfInterest;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 8, 2017
 */
public class TopicOfInterestFactory {
	private TopicOfInterest toi;
	
	/**
	 * Constructor
	 */
	public TopicOfInterestFactory() {
		toi = new TopicOfInterest();
	}
	
	public void setTopic(String uri) {
		TopicOfInterestHandler handler = new TopicOfInterestHandler();
		toi = handler.getDocument();
		try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();         
            saxParser.parse(uri, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public String getTopicId() {
		return toi.topicId;
	}
	
	public String getTitle() {
		return toi.title;
	}
	
	public String getDesc() {
		return toi.desc;
	}
	
	public String getNarr() {
		return toi.narr;
	}
}
