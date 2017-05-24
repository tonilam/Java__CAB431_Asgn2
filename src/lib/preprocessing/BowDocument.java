package lib.preprocessing;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * BowDocument is used for storing bag of words of a document. It contains a map of each term in a document
 * and the frequency of each term.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017
 *
 */
public class BowDocument {
	private final int INITIAL_WORDCOUNT = 0;
	private final int INITIAL_TERM_FREQ = 1;
	private final int TERM_FREQ_INCREMENT = 1;
	
	private String fileNo;
	private int docID;
	private int itemID;
	private HashMap<String, Integer> termFreqMap;
	private int docLengtht;
	
	/**
	 * Constructor to initialize attributes.
	 * @since 1.0
	 * 
	 * @param fileId is the file ID of the current document.
	 */
	public BowDocument(String fileId) {
		fileNo = fileId;
		termFreqMap = new HashMap<String, Integer>();
		docLengtht = INITIAL_WORDCOUNT;
	}

	/**
	 * Accessor of the attribute fileNo.
	 * @since 2.0
	 * 
	 * @return the file no. of the current document.
	 */
	public String getFileNo() {
		return fileNo;
	}

	/**
	 * Accessor of the attribute docID
	 * @since 1.0
	 * 
	 * @return the current document ID.
	 */
	public int getDocId() {
		return docID;
	}
	
	/**
	 * It sets the itemID and docID at the same time.
	 * @since 1.0
	 * 
	 * @param id indicate which file is being used. It should not be null.
	 */
	public void setitemId(int id) {
		itemID = id;
		// docID contains the same value of itemID. Its purpose is to show that
		// the program will use the item id as the document ID as per the requirement
		// stated in week 3 exercise.
		docID = itemID;
	}

	/**
	 * Accessor of the attribute itemID
	 * @since 1.0
	 * 
	 * @return the document's item ID.
	 */
	public int getItemId() {
		return itemID;
	}
	
	/**
	 * It performs in 2 ways:
	 * 1. If the term is already in the bag, then add its frequency by 1.
	 * 2. If the term is not exist in the bag, then add the new term into the bag. 
	 * @since 1.0
	 * 
	 * @param name is the term name
	 */
	public void addTerm(String name) {
		if (termFreqMap.keySet().contains(name)) {
			int frequency = termFreqMap.get(name) + TERM_FREQ_INCREMENT;
			termFreqMap.replace(name, frequency);
		} else {
			termFreqMap.put(name, INITIAL_TERM_FREQ);
		}
	}
	
	/**
	 * It gets the target term's frequency.
	 * @since 1.0
	 * 
	 * @param name is the term name. It should not be null.
	 * @return the frequency of the target term.
	 */
	public int getTermFreq(String name) {
		if (termFreqMap.keySet().contains(name)) {
			return termFreqMap.get(name);
		} else {
			return 0;
		}
	}

	/**
	 * It gets the entire term frequency map.
	 * @since 1.0
	 * 
	 * @return the term frequency map stored in this instance.
	 */
	public HashMap<String, Integer> getTermFreqMap() {
		return termFreqMap;
	}
	
	/**
	 * It gets the sorted term frequency map.
	 * @since 1.0
	 * 
	 * @return the sorted term frequency map as an iterator object.
	 */
	public HashMap<String, Integer> getSortedTermFreqMap() {
		// sort by names
		Stream<Map.Entry<String,Integer>> mapWithSortedKey =
				termFreqMap.entrySet().stream().sorted(
					Map.Entry.comparingByKey()
				 );
		// sort by values.
		Stream<Map.Entry<String,Integer>> mapWithSortedValue =
				mapWithSortedKey.sorted(
					Collections.reverseOrder(Map.Entry.comparingByValue())
				 );
		
		// The following lines store the sorted list into a LinkedHashMap  
		Iterator<Entry<String, Integer>> sortedListIterator = mapWithSortedValue.iterator();
		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		while (sortedListIterator.hasNext()) {
			Entry<String, Integer> item = sortedListIterator.next();
			sortedMap.put(item.getKey(), item.getValue());
		}
		
		return sortedMap;
	}
	
	/**
	 * It gets the number of terms inside the bag of words.
	 * @since 1.0
	 * 
	 * @return the size of the bag of words.
	 */
	public int getTermCount() {
		return termFreqMap.size();
	}

	/**
	 * It do the word count increment by 1 whenever it is called.
	 * @since 1.0
	 */
	public void addWordCount() {
		docLengtht += 1;
	}
	
	/**
	 * Accessor of the attribute word count, which is the number of words in this document.
	 * @since 1.0
	 * 
	 * @return the value of wordCount.
	 */
	public int getWordCount() {
		return docLengtht;
	}
	
}
