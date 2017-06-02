package lib.preprocessing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * TfIdf is used to calculate the TF*IDF value of each term in the data set.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22 2017
 */
public class TfIdf {
	
	private HashMap<Integer,BowDocument> bowCollection;

	/**
	 * This is the constructor and the bag of words collection have to be given.
	 * @since 1.0
	 * 
	 * @param collection is the BowCollection for this application.
	 */
	public TfIdf(HashMap<Integer,BowDocument> collection) {
		bowCollection = collection;
	}

	/**
	 * It calculate the TF*IDF value of each term and return the entire 
	 * TF*IDF map.
	 * @since 1.0
	 * 
	 * @param aDoc is the current inspecting document
	 * @param noDocs is the total number of documents in the data set
	 * 
	 * @return a hash map for all the TF*IDF value of each term
	 */
	public HashMap<String, Double> calculateTfIdf(
				BowDocument aDoc, int noDocs) {
		
		HashMap<String, Double> resultMap = new HashMap<String, Double>();
		HashMap<String, Integer> freqMap = aDoc.getTermFreqMap();

		/* Calculate the denominator of the TF*IDF formula */
		double denominator = 0;
		for (String term: freqMap.keySet()) {
			denominator += Math.pow(
					calculateSingleTfIdf(term, freqMap.get(term), noDocs),
					2);
		}
		denominator = Math.sqrt(denominator);
		
		/* Calculate the numerator of the TF*IDF formula */
		for (Entry<String, Integer> post: freqMap.entrySet()) {
			String term = post.getKey();
			Integer frequency = post.getValue();
			double tfidfResult = calculateSingleTfIdf(term, frequency, noDocs)
								 / denominator;
			resultMap.put(term, tfidfResult);
		}

		return sortingTfIdf(resultMap);
	}
	
	/**
	 * Given a single document and the term frequency, this method will calculate
	 * the relevant TF*IDF information for alter usage in the formula.
	 * @since 1.0
	 * 
	 * @param term is the specific term name in the formula
	 * @param termFreqInDoc is the term frequency within the current document
	 * @param noDocs is the total number of documents in the data set
	 * 
	 * @return the relevant TF*IDF based on the given data
	 */
	private double calculateSingleTfIdf(String term, int termFreqInDoc, int noDocs) {
		int docFreqForThisTerm;
		double termFrequency,
			   inverseDocumentFrequency;
					
		/* Go through each document and count how many document contains the term */
		docFreqForThisTerm = 0;
		for(int id : bowCollection.keySet()) {
			BowDocument scanningDoc = bowCollection.get(id);
			if (scanningDoc.getTermFreqMap().keySet().contains(term)) {
				++docFreqForThisTerm;
			}
		}

		termFrequency = 1.0 + Math.log10(termFreqInDoc);
		inverseDocumentFrequency = Math.log10(noDocs / docFreqForThisTerm);
		
		return termFrequency * inverseDocumentFrequency;
		
	}
	
	/**
	 * It sorts the entire TF*IDF map by term name in ascending order and by the
	 * TF*IDF value in descending order
	 * @since 1.0
	 * 
	 * @param originalMap is the original unsorted map
	 * 
	 * @return a sorted TF*IDF map
	 */
	private HashMap<String, Double> sortingTfIdf(HashMap<String, Double> originalMap) {
		// sort by the term names
		Stream<Map.Entry<String,Double>> mapWithSortedKey =
				originalMap.entrySet().stream().sorted(
					Map.Entry.comparingByKey()
				 );
		// sort by the TF*IDF values
		Stream<Map.Entry<String,Double>> mapWithSortedValue =
				mapWithSortedKey.sorted(
					Collections.reverseOrder(Map.Entry.comparingByValue())
				 );
		HashMap<String, Double> sortedTfIdf = new LinkedHashMap<String, Double>();
		Iterator<Entry<String, Double>> sortedListIterator = mapWithSortedValue.iterator();
		
		while (sortedListIterator.hasNext()) {
			Entry<String, Double> item = sortedListIterator.next();
			sortedTfIdf.put(item.getKey(), item.getValue());
		}
		return sortedTfIdf;
	}
}
