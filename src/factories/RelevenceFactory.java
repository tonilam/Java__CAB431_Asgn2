/**
 * 
 */
package factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import config.AppConfig;
import lib.preprocessing.BM25;
import lib.preprocessing.BowCollection;
import lib.preprocessing.BowDocument;
import lib.preprocessing.TfIdf;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, May 22, 2017
 */
public class RelevenceFactory {

	private TopicOfInterestFactory toifty;
	private Double threshold;
	
	public RelevenceFactory() {
		toifty = new TopicOfInterestFactory();
	}
	
	public void setTopic(String topic) {
		toifty.setTopic(topic);
		toifty.setTopics(".//src//resources//TopicStatements101-150.txt");
	}
	
	public String getQueryString() {
		return toifty.getQueryString();
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	public HashMap<String, HashMap<Integer, Double>> getReleventDocument(String topic) {
		HashMap<String, HashMap<Integer, Double>> completeTrainingSet = new HashMap<String, HashMap<Integer, Double>>();
		HashMap<Integer, Double> releventDocuments = new HashMap<Integer, Double>();
		HashMap<Integer, Double> nonreleventDocuments = new HashMap<Integer, Double>();

		// prepare the Topic of Interest file
		setTopic(topic);
		
		// prepare the set of input documents
		String trainingFolder = topic.replace("R", "Training");
		BowCollection bowCollection = new BowCollection(AppConfig.DEFAULT_DATASET_DIR + trainingFolder + "//");
		
		// Algorithm for the discovery of a complete training set
		TfIdf tfidfAlgorithm = new TfIdf(bowCollection);
		HashMap<Integer, Double> rank = new HashMap<Integer, Double>();
		for(Entry<Integer, BowDocument> doc : bowCollection.entrySet()) {
			HashMap<String, Double> weight = tfidfAlgorithm.calculateTfIdf(doc.getValue(), bowCollection.size());
			rank.put(doc.getKey(), tfidfAlgorithm.TFIDFtesting(toifty.getTerms(), weight));

			double minSupport = bowCollection.size() * 0.5;
			threshold = (1/minSupport) * Math.log(bowCollection.size()/minSupport);
			if (rank.get(doc.getKey()) >= threshold) {
				releventDocuments.put(doc.getKey(), rank.get(doc.getKey()));
			} else {
				nonreleventDocuments.put(doc.getKey(), rank.get(doc.getKey()));
			}
			
		}
		
		// combine two sets of document to output
		completeTrainingSet.put("rel", releventDocuments);
		completeTrainingSet.put("nonrel", nonreleventDocuments);
				
		return completeTrainingSet;
	}
}
