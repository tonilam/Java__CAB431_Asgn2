/**
 * 
 */
package main;

import models.factories.TopicOfInterestFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import lib.files.DocumentReader;
import lib.files.XmlReader;
import lib.files.XmlStructure;
import lib.preprocessing.BM25;
import lib.preprocessing.BowDocument;
import lib.preprocessing.Stemmer;
import lib.preprocessing.StoppingWords;
import models.Document;
import models.TermFrequencyMap;
import models.TopicOfInterest;
import models.factories.DocumentFactory;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, May 29, 2017
 */
public class TrainingSetDiscovery {

	private static boolean VERBOSE = false;
	private static boolean VERBOSE_DETAILS = false;
	private static boolean VERBOSE_INFO = true;
	private static int MIN_WORD_LENGHT = 2;
	private static Stemmer snowballStemmer;
	private static int totalDocLength;
	private static int currentTopic;
	private static boolean enableStemming = true;
	private static String stopwordResourceFile = "./src//resources//common-english-words.txt";
	private static String TrainingDataResourceFolder = ".//src//resources//dataset101-150/";
	private static StoppingWords sw = new StoppingWords(stopwordResourceFile);
	private static Set<String> dictionary;
	
	/**main 
	 * @since
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		int startTopic = 101;
		int maxTopic = 50;
		for (int i = startTopic; i < startTopic + maxTopic; ++i) {
			currentTopic = i;	// for debugging
			baselineModel(i);
		}
	}
	
	private static String getQueryString(int topicId) {
		debug("\n\n*** Get file ready ***");

		String queryString = "";
		
		TopicOfInterestFactory toiFty = new TopicOfInterestFactory();
		TopicOfInterest toi = toiFty.getTopic(topicId);
		
		/* choose one */
		//queryString = toi.title;
		queryString = toi.title + " " + toi.desc;
		
		queryString = queryString.replaceAll("[\\W]", " ");
		
		info("Topic: R" + topicId);
		debug("Title: " + toi.title);
		debug("Description: " + toi.desc);
		debug("Narrative: " + toi.narr);
		debug("query String: " + queryString);
		
		return queryString;
	}
	
	private static TermFrequencyMap getTerms(String content) {
		TermFrequencyMap terms = new TermFrequencyMap();
		
		String tokens[] = content.split(" ");
		for (String term : tokens) {
			debugDetails(term + "\t");
			terms.put(term.toLowerCase(), 1);
		}
		
		return terms;
	}
	
	private static TermFrequencyMap getQueryTerms(String queryString) {
		debug("*** Tokenize Topic ***");
		TermFrequencyMap terms = getTerms(queryString);
		debug("Total words found = " + terms.size());
		
		return terms;
	}
	
	private static TermFrequencyMap filterStopwords(TermFrequencyMap terms) {
		debug("*** Stopping ***");
		debug("No of terms for stopping: " + terms.size());
		
		TermFrequencyMap stoppedList = new TermFrequencyMap();
				
		for (Entry<String, Integer> item : terms.entrySet()) {
			debugDetails("Checking..." + item.getKey());
			String word = item.getKey().toLowerCase();
			if (
					(word.length() > MIN_WORD_LENGHT)
					&& word.matches("^[a-zA-Z]*$")
					&& !sw.isStopWord(word)
				) {
				debugDetails("+ " + word);
				stoppedList.accumulate(word, item.getValue());
        	} else {
        		debugDetails("\t\t\t- " + item);
        	}
		}
		debug("Total terms = " + stoppedList.size());
		
		return stoppedList;
	}
	
	private static Map<String, TermFrequencyMap> groupFilteringStopwords(Map<String, TermFrequencyMap> docTermMatrix) {
		debug("*** Group Stopping ***");

		Map<String, TermFrequencyMap> newMatrix = new HashMap<String, TermFrequencyMap>();
		
		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
			TermFrequencyMap newMap = filterStopwords(vector.getValue());
			newMatrix.put(vector.getKey(), newMap);
		}
		
		return newMatrix;
	}

	private static String stemming(String term) {
		String result = snowballStemmer.stemming(term);
		if (result.equals(term)) {
			debugDetails(term + " [no change]");
		} else {
			debugDetails(term + " => " + result);
		}
		return result;
	}

	private static TermFrequencyMap stemmingWords(TermFrequencyMap terms) {
		debug("Stemming, no. of terms = " + terms.size());
				
		TermFrequencyMap stemmedTerms = new TermFrequencyMap();
		for (Entry<String, Integer> termFreq : terms.entrySet()) {
			String stemmedWord = stemming(termFreq.getKey());
			debugDetails("\t " + termFreq.getKey() + " => " + stemmedWord);
			if (!sw.isStopWord(stemmedWord)) {
				dictionary.add(stemmedWord);
				stemmedTerms.accumulate(stemmedWord);
        	} else {
        		debugDetails("\t <removed>");
        	}
		}
		debug("After stemming:");
		stemmedTerms.forEach((k,v)->debugDetails(k));
		debug("Total terms  = " + stemmedTerms.size());
		
		return stemmedTerms;
	}
	
	private static Map<String, TermFrequencyMap> groupStemmingWords(Map<String, TermFrequencyMap> docTermMatrix) {
		debug("*** Group Stemming Words ***");

		Map<String, TermFrequencyMap> newMatrix = new HashMap<String, TermFrequencyMap>();
		
		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
			TermFrequencyMap newMap = stemmingWords(vector.getValue());
			newMatrix.put(vector.getKey(), newMap);
		}
		
		return newMatrix;
	}
	
	private static Map<String, Document> getTrainingFiles(int topicId) {
		debug("*** Get Training Files..." + topicId);
		
		HashMap<String, Document> docSet = new HashMap<String, Document>();

		String TrainingDatasetForTopic = TrainingDataResourceFolder + "Training" + topicId;
		debugDetails("Switch to resource folder: " + TrainingDatasetForTopic);
		File folder = new File(TrainingDatasetForTopic);
		File[] datalist = folder.listFiles();
		
		for (File file: datalist) {

			if(!file.getName().matches("[0-9]+(.)xml")) {
				continue;
			}
			debugDetails("Now reading ... " + file.getAbsolutePath());
			
			Document trainingData = new DocumentReader().ReadDocument(file.getAbsolutePath());
			trainingData.filename = file.getName();
			docSet.put(trainingData.filename, trainingData);
		}
		
		return docSet;
		
	}
	
	private static Map<String, TermFrequencyMap> getDocTermMatrix(Map<String, Document> trainingSet) {
		debug("*** Get Doc Term Matrix");
		Map<String, TermFrequencyMap> docTermMatrix = new HashMap<String, TermFrequencyMap>();
		
		for (Document trainingData: trainingSet.values()) {
			String content = trainingData.text;
			
			content = content.replaceAll("[\\W]", " "); // break words that contain non-word characters.
			debugDetails("Content: " + content);

			debug("Tokenize document..." + trainingData.filename);
			TermFrequencyMap termFreq = getTerms(content);
			debugDetails("Total words found = " + termFreq.size());
			
			docTermMatrix.put(trainingData.filename, termFreq);
		}
		
		return docTermMatrix;
	}

	private static Map<String, TermFrequencyMap> normalizeIndexing(Map<String, TermFrequencyMap> dfMap) {
		Map<String, TermFrequencyMap> normalizeIndexing = new HashMap<String, TermFrequencyMap>();
		
		for (Entry<String, TermFrequencyMap> vector : dfMap.entrySet()) {
			TermFrequencyMap newDf = new TermFrequencyMap();
			for (String term : dictionary) {
				if (vector.getValue().containsKey(term)) {
					newDf.put(term, vector.getValue().get(term));
				} else {
					newDf.put(term, 0);
				}
			}
			normalizeIndexing.put(vector.getKey(), newDf);
		}

		return normalizeIndexing;
	}
	
	private static Map<String, List<String>> initializeClassification() { 
		Map<String, List<String>> classifiedDocumentSet = new HashMap<String, List<String>>();
		classifiedDocumentSet.put("rel", new ArrayList<String>());
		classifiedDocumentSet.put("nonrel", new ArrayList<String>());
		return classifiedDocumentSet;
	}
	
	private static Map<String, List<String>> booleanModel(
			TermFrequencyMap queryTerms, Map<String, TermFrequencyMap> docTermMatrix) {
		debug("*** Boolean Model ***");
		debug("Query = " + queryTerms);
		
		Map<String, List<String>> booleanModel = new HashMap<String, List<String>>();
		List<String> relDoc = new ArrayList<String>();
		List<String> nonrelDoc = new ArrayList<String>();
		
		int counter = 0;
		for (Entry<String, TermFrequencyMap> doc : docTermMatrix.entrySet()) {
			boolean relevance = false;
			
			// search for at least one match
			for (Entry<String, Integer> tf : doc.getValue().entrySet()) {
				// Classify as relevant if the document term set contains the query term and the
				// frequency is > 0.
				if (queryTerms.containsKey(tf.getKey()) && (tf.getValue() > 0)) {
					debug(tf.getKey() + " match.");
					relevance = true;
				}
			}
			debug("Classifying..." + doc.getKey() + "\t" + relevance);

			if (relevance) {
				relDoc.add(doc.getKey());
			} else {
				nonrelDoc.add(doc.getKey());
			}
		}
		booleanModel.put("rel", relDoc);
		booleanModel.put("nonrel", nonrelDoc);

		info("Total relevant documents found = " + relDoc.size());
		info("Total irrelevant documents found = " + nonrelDoc.size());
		
		return booleanModel;
	}
	
	private static Map<String, List<String>> top10Model(Map<String, Double> rankingMap) {
		debug("*** Top 10 Model ***");
		
		Map<String, List<String>> top10Model = new HashMap<String, List<String>>();
		List<String> relDoc = new ArrayList<String>();
		List<String> nonrelDoc = new ArrayList<String>();
		
		int counter = 0;
		for (String doc : rankingMap.keySet()) {
			boolean relevance = false;
			
			if (++counter <= 10) {
				relDoc.add(doc);
			} else {
				nonrelDoc.add(doc);
			}
		}
		top10Model.put("rel", relDoc);
		top10Model.put("nonrel", nonrelDoc);

		info("Total relevant documents found = " + relDoc.size());
		info("Total irrelevant documents found = " + nonrelDoc.size());
		
		return top10Model;
	}
	
	private static Map<String, Double> calculateBM25(
			TermFrequencyMap queryTerms,
			Map<String, TermFrequencyMap> docTermMatrix,
			Map<String, List<String>> classificationResult) {
		debug("\nCalculating BM25 score:");

		List<String> rankingIndex = new ArrayList<String>();
		Map<String, Double> bm25Scores = new HashMap<String, Double>();
		double totalDocLength = 0,
			   avgDocLength = 0;
		
		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
			for (Entry<String, Integer> tf : vector.getValue().entrySet()) {
				totalDocLength += tf.getValue();
			}
		}
		info("Total Document Length of this document set = " + totalDocLength);
		avgDocLength = totalDocLength / docTermMatrix.size();
		info("Average Document Length of this document set = " + avgDocLength);
		
		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
			double bm25Score = BM25.calculateBM25(
				vector.getValue(),
				queryTerms,
				avgDocLength,
				docTermMatrix,
				docTermMatrix.size(),
				classificationResult.get("rel")
				);
			debug("BM25 = " + bm25Score);

			bm25Scores.put(vector.getKey(), bm25Score);
			
			// insert ranking
			if (rankingIndex.size() == 0) {
				rankingIndex.add(vector.getKey());
			} else {
				int maxSize = rankingIndex.size();
				for (int i = 0; i <= maxSize; ++i) {
					if (i == maxSize) {
						rankingIndex.add(vector.getKey());
					} else {
						if (bm25Scores.get(rankingIndex.get(i)) < bm25Score) {
							rankingIndex.add(i, vector.getKey());
							break;
						}
						
					}
				}
			}
		}
		

		Map<String, Double> rankedBm25Scores = new LinkedHashMap<String, Double>();
		rankingIndex.forEach((docId)->rankedBm25Scores.put(docId, bm25Scores.get(docId)));

		return rankedBm25Scores;
	}
	
	private static void saveDocumentRelevance(
			int topicId, Map<String, Double> rankingMap, Map<String, List<String>> classifiedDocumentSet) {
		List<String> relDoc = classifiedDocumentSet.get("rel");
		rankingMap.forEach(
			(k,v)->info(String.format("BM25(%9s) = %f %s",
					k,v, (relDoc.contains(k)?"+":"-")))
		);
		PrintStream out = null;
		try {
			int resultIndex = topicId - 100;
	    	File outfile = new File(".//src//resources//result//BaselineModel//Result"+resultIndex+".txt");
	    	out = new PrintStream(new FileOutputStream(outfile));

	    	for (String doc : rankingMap.keySet()) {
	    		String filename = doc.substring(0, doc.indexOf('.'));
	    		out.println("R" + topicId + "\t"
	    					+ filename + "\t"
	    				    + (relDoc.contains(doc)? 1:0));
			}
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
        	if ( out != null ) {
        		out.close();
        	}
        }
	}
	
	private static void savedocumentIndex(int topicId, Map<String, TermFrequencyMap> docTermMatrix) {
		PrintStream out = null;
		try {
	    	File outfile = new File(".//src//resources//result//BaselineModel//index"+(topicId - 100)+".txt");
	    	out = new PrintStream(new FileOutputStream(outfile));
    		out.println("<documents size=\"" + docTermMatrix.size() + "\">");
    		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
	    		out.println("\t<id>" + vector.getKey() + "</id>");
    		}
    		out.println("</documents>");

    		out.println("<term size=\"" + dictionary.size() + "\">");
	    	for(String term : dictionary) {
	    		out.println("\t<"+ term + ">");
	    		out.print("\t\t");
	    		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
		    		out.print(vector.getValue().get(term));
	    		}
	    		out.println("\n\t</" + term + ">");
			}
    		out.println("</term>");
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
        	if ( out != null ) {
        		out.close();
        	}
        }
	}
	
	private static void baselineModel(int topicId) {

		dictionary = new HashSet<String>();
		sw.enable();
		snowballStemmer = new Stemmer(enableStemming);

		///////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////
		// Section 1 : Generate Query Terms

		// Step 1: generate a query string from topic of interest
		String queryString = getQueryString(topicId);
		
		// Step 2: tokenize query string into terms
		TermFrequencyMap queryTerms = getQueryTerms(queryString);
		
		// Step 3: stopping
		queryTerms = filterStopwords(queryTerms);
		
		// Step 4: Stemming
		queryTerms = stemmingWords(queryTerms);

		///////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////
		// section 2 : process data set

		// Step 5: Retrieve Document set
		Map<String, Document> trainingSet = getTrainingFiles(topicId);
		
		// Step 6: Create Document-Term matrix
		Map<String, TermFrequencyMap> docTermMatrix = getDocTermMatrix(trainingSet);
		
		// Step 7: Stopping
		docTermMatrix = groupFilteringStopwords(docTermMatrix);
		
		// Step 8: Stemming
		docTermMatrix = groupStemmingWords(docTermMatrix);

		// Step 9: indexing
		docTermMatrix = normalizeIndexing(docTermMatrix);
		
		// Step 10: Calculate BM25 score
		// For baseline model, we assume there is no relevant document,
		Map<String, List<String>> classifiedDocumentSet = initializeClassification();
		Map<String, Double> rankingMap = calculateBM25(queryTerms, docTermMatrix, classifiedDocumentSet);

		// Step 11: Classification
		classifiedDocumentSet = booleanModel(queryTerms, docTermMatrix);
		//classifiedDocumentSet = top10Model(rankingMap);

		// Step 12: output result
		saveDocumentRelevance(topicId, rankingMap, classifiedDocumentSet);
		
		// Step 13: save document-term index for later use
		savedocumentIndex(topicId, docTermMatrix);
	}
	
	private static void debug(String message) {
		if (VERBOSE) {
			System.out.println(currentTopic + ">" + message);
		}
	}
	
	private static void info(String message) {
		if (VERBOSE_INFO) {
			System.out.println(currentTopic + ">" + message);
		}
	}
	
	private static void debugDetails(String message) {
		if (VERBOSE_DETAILS) {
			System.out.println(currentTopic + ">" + message);
		}
	}
}
