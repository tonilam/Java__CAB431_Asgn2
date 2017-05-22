/**
 * 
 */
package factories;

import factories.handler.TopicOfInterestHandler;
import models.TopicOfInterest;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 8, 2017
 */
public class TopicOfInterestFactory {
	private TopicOfInterest toi;
	private String topic;
	
	/**
	 * Constructor
	 */
	public TopicOfInterestFactory() {
		toi = new TopicOfInterest();
	}
	
	public void setTopics(String uri) {
		TopicOfInterestHandler handler = new TopicOfInterestHandler();
		toi = handler.getTopicOfInterest(topic, uri);
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopicId() {
		return toi.topicNum;
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
