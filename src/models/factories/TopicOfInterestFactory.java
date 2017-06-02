/**
 * 
 */
package models.factories;

import java.io.FileNotFoundException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lib.files.TopicOfInterestHandler;
import models.TopicOfInterest;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, May 29, 2017
 */
public class TopicOfInterestFactory {
	
	public TopicOfInterestFactory() {
		
	}
	
	public TopicOfInterest getTopic(int topicId){
		String uri = ".//src//resources//TopicStatements101-150.txt";
		TopicOfInterestHandler handler = new TopicOfInterestHandler();
		TopicOfInterest topic = handler.getTopicOfInterest(String.format("R%03d", topicId), uri);
		return topic;
	}

}
