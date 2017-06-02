package lib.preprocessing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * DocumentFrequency is used for counting the frequency of each term in all documents
 * from the data set.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 24 2017
 */
public class DocumentFrequency {

	private HashMap<String, Integer> df;
	private HashMap<String, Integer> relevantDoc;
	private int noOfDoc;
	
	/**
	 * Simple constructor to initialize the variables.
	 * @since 1.0
	 */
	public DocumentFrequency() {
		df = new LinkedHashMap<String, Integer>();
		relevantDoc = new LinkedHashMap<String, Integer>();
		noOfDoc = 0;
	}
	
	/**
	 * It calculate the document frequency of every term in the collection.
	 * @since 1.0
	 *  
	 * @param docCollection is a collection of BowDocument, i.e. HashMap&lt;Integer,
	 *  BowDocument&gt; of docId:aBowDocument
	 *  
	 * @return HashMap&lt;String, Integer&gt; of term:df
	 */
	public HashMap<String, Integer> calculateDF(BowCollection docCollection) {
		BowDocument scanningDoc;
		HashMap<String, Integer> docTermFreq;
		int originalFreq, newFreq;
		
		// Record the total number of documents inspected.
		noOfDoc = docCollection.size();
		
		for (Entry<Integer, BowDocument> doc : docCollection.entrySet()) {
			scanningDoc = doc.getValue();
			docTermFreq = scanningDoc.getTermFreqMap();
			for (String docTerm : docTermFreq.keySet()) {
				// Get to term frequency of the specified term in the scanning document
				originalFreq = docTermFreq.get(docTerm);
				
				// Update the term frequency in DF for all documents
				if (df.containsKey(docTerm)) {
					newFreq = originalFreq + df.get(docTerm);
					df.replace(docTerm, newFreq);
					relevantDoc.replace(docTerm, relevantDoc.get(docTerm)+1);
				} else {
					df.put(docTerm, originalFreq);
					relevantDoc.put(docTerm, 1);
				}
			}
		}
		
		sortingDF();
		return df;
	}
	
	/**
	 * It sorts the entire document frequency map by terms in ascending 
	 * alphabetic order and by frequency in descending order.
	 * @since 1.0
	 */
	private void sortingDF() {
		Stream<Map.Entry<String,Integer>> mapWithSortedKey =
				df.entrySet().stream().sorted(
					Map.Entry.comparingByKey()
				 );
		Stream<Map.Entry<String,Integer>> mapWithSortedValue =
				mapWithSortedKey.sorted(
					Collections.reverseOrder(Map.Entry.comparingByValue())
				 );
		Iterator<Entry<String, Integer>> sortedListIterator = mapWithSortedValue.iterator();
		LinkedHashMap<String, Integer> mapSequence = new LinkedHashMap<String, Integer>();
		while(sortedListIterator.hasNext()) {
			Entry<String, Integer> newItem = sortedListIterator.next();
			mapSequence.put(newItem.getKey(), newItem.getValue());
		}
		df = mapSequence;
	}
	
	/**
	 * It simply sends the document frequency map to the caller.
	 * @since 1.0
	 * 
	 * @return the map information of the document frequency.
	 */
	public HashMap<String, Integer> getDFMap() {
		return df;
	}
	
	/**
	 * It gets the doucment frequency of a specified term. 
	 * @since 1.0
	 *
	 * @param term is the term name being querying. 
	 * @return the document frequency of this term.
	 */
	public int getDfByTerm(String term) {
		if (df.containsKey(term)) {
			return df.get(term);
		} else {
			return 0;
		}
	}
	
	/**
	 * It gets the no. of relevant document of the specified term. 
	 * @since 1.0
	 *
	 * @param term is the term name being querying
	 * @return the no. of relevant documents related to this term.
	 */
	public int getRelevantDocByTerm(String term) {
		if (relevantDoc.containsKey(term)) {
			return relevantDoc.get(term);
		} else {
			return 0;
		}
	}
	
	/**
	 * It displays all the document frequency on the console window.
	 * @since 1.0
	 * 
	 * @return the text description of the data stored in this object. 
	 */
	public String toString() {
		String strBuffer = "";
		strBuffer = strBuffer.concat(String.format(
						"There are %d documents in this data set and contains %d terms.\n",
						noOfDoc,
						df.size()));
		for (String term : df.keySet()) {
			strBuffer = strBuffer.concat(String.format(
							"%s: %d\n",
							String.format("%1$-12s", term),
							df.get(term)));
		}
		return strBuffer;
	}

}
