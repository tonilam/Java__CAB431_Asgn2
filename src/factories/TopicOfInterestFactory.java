/**
 * 
 */
package factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import factories.handler.TopicOfInterestHandler;
import lib.preprocessing.Stemmer;
import lib.preprocessing.StoppingWords;
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
	
	private Stemmer snowballStemmer;
	
	/**
	 * Constructor
	 */
	public TopicOfInterestFactory() {
		toi = new TopicOfInterest();
		snowballStemmer = new Stemmer(true);
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

	public Set<String> getTerms() {
		Map<String, Integer> termFreq = getTermFrequency();
		return termFreq.keySet();
	}
	
	public Map<String, Integer> getTermFrequency() {
		HashMap<String, Integer> termFreq = new HashMap<String, Integer>();
		StoppingWords sw = new StoppingWords(".//src//resources//common-english-words.txt", true);
		
		String content = (toi.title + " " + toi.desc)
							.toLowerCase()				// generalize all characters to lower case
							.replaceAll("[\\W]", " ");	// break words that contain non-word characters.
		
		StringTokenizer st = new StringTokenizer(content);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!sw.isStopWord(token)) {
            	token = snowballStemmer.stemming(token);
            	if (termFreq.containsKey(token)) {
            		termFreq.replace(token, termFreq.get(token) + 1);
            	} else {
            		termFreq.put(token, 1);
            	}
            }
        }   
            
		return termFreq;
	}
	
	public String getQueryString() {
		Map<String, Integer> termFreq = getTermFrequency();
		String fullSetKeywords = String.join(" ", termFreq.keySet());
		String selectedKeywords = "";
		for (Entry<String, Integer> item : termFreq.entrySet()) {
			if (item.getValue() > 1) {
				selectedKeywords += item.getKey() + " ";
			}
		}
		System.out.println("F>>"+fullSetKeywords);
		System.out.println("S>>"+selectedKeywords);
		return selectedKeywords;
	}
}
