/**
 * 
 */
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;
import java.util.Map.Entry;

import lib.files.DocumentReader;
import lib.preprocessing.BM25;
import lib.preprocessing.BowCollection;
import lib.preprocessing.BowDocument;
import lib.preprocessing.Stemmer;
import lib.preprocessing.StoppingWords;
import lib.preprocessing.TfIdf;
import models.Document;
import models.TermFrequencyMap;
import models.TopicOfInterest;
import models.factories.TopicOfInterestFactory;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, May 29, 2017
 */
public class IFModel {
	private static boolean VERBOSE = false;
	private static boolean VERBOSE_DETAILS = false;
	private static boolean VERBOSE_INFO = true;
	private static int currentTopic;

	private static String TrainingResultResourceFolder = ".//src//resources//result//BaselineModel//";
	
	/**main 
	 * @since
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		int startTopic = 101;
		int maxTopic = 1;
		for (int i = startTopic; i < startTopic + maxTopic; ++i) {
			currentTopic = i;	// for debugging
			Map<String, Double> rankingMap = lassoMachineLearning(i);
			saveDocumentRelevance(i, rankingMap);
		}
	}
	
	/**lassoMachineLearning 
	 * @since
	 *
	 * @param i
	 * @return 
	 */
	private static Map<String, Double> lassoMachineLearning(int topicId) {
		// Step 1: Retrieve Training Data Set
		Map<String, List<String>> classifiedDocumentSet = getTrainingDataset(topicId);
		
		// Step 2: Retrieve Index table
		Map<String, TermFrequencyMap> docTermMatrix = getDocumentIndex(topicId);
		
		// Step 3: Lasso machine learning to find the term weight
		Double delta = 0.0025;
		Map<String, Double> beta = ApproximationLassoTraining(
				classifiedDocumentSet,
				docTermMatrix,
				delta
			);
		return beta;
	}
	
	/**getTrainingDataset 
	 * @since
	 *
	 * @return
	 */
	private static Map<String, List<String>> getTrainingDataset(int topicId) {
		debug("*** getTrainingDataset ***");
		
		Map<String, List<String>> classifiedDocumentSet = new LinkedHashMap<String, List<String>>();
		List<String> relDoc = new LinkedList<String>();
		List<String> nonrelDoc = new LinkedList<String>();
		
		int resultIndex = topicId - 100;
		File file = new File(TrainingResultResourceFolder+"Result" + resultIndex + ".txt");
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] tokens = line.split("\t");
				if (tokens.length == 3) {
					if (tokens[2].equals("1")) {
						relDoc.add(tokens[1]);
					} else if (tokens[2].equals("0")) {
						nonrelDoc.add(tokens[1]);
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		classifiedDocumentSet.put("rel", relDoc);
		classifiedDocumentSet.put("nonrel", nonrelDoc);

		info("Total relevant documents found = " + relDoc.size());
		info("Total irrelevant documents found = " + nonrelDoc.size());
		
		return classifiedDocumentSet;
	}

	/**getTrainingDataset 
	 * @since
	 *
	 * @return
	 */
	private static Map<String, TermFrequencyMap> getDocumentIndex(int topicId) {
		debug("*** getDocumentIndex ***");

		Map<String, TermFrequencyMap> documentMatrix = new HashMap<String, TermFrequencyMap>();
		List<String> documentIndex = new LinkedList<String>();
				
		int resultIndex = topicId - 100;
		File file = new File(TrainingResultResourceFolder+"index" + resultIndex + ".txt");
		Scanner scanner;
		boolean start = false;
		try {
			scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.indexOf("<documents ") >= 0) {
					start = true;
					while (start) {
						String documentRow = scanner.nextLine();
						if (documentRow.indexOf("</documents>") >= 0) {
							start = false;
						} else {
							String tokens[] = documentRow.split("[(<[a-z]*>)(.xml)]");
							String docId = tokens[4];
							documentIndex.add(docId);
							documentMatrix.put(docId, new TermFrequencyMap());
						}
					}
				} else if (line.indexOf("<term ") >= 0) {
					debug("No. of File = " + documentIndex.size());
					start = true;
					while (start) {
						String termRow = scanner.nextLine();
						if (termRow.indexOf("</term>") >= 0) {
							start = false;
						} else {
							String termname = termRow;
							termname = termname.substring(2, termname.length() - 1);
							String booleanRecord = scanner.nextLine().replaceAll("[\t\n\r]","");
							for (int i = 0; i < booleanRecord.length(); ++i) {
								TermFrequencyMap tfMap = documentMatrix.get(documentIndex.get(i));
								tfMap.accumulate(termname, Integer.parseInt(booleanRecord.substring(i,i+1)));
								documentMatrix.replace(documentIndex.get(i), tfMap);
							}
							scanner.nextLine();  // skip ending tag
						}
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return documentMatrix;
	}
	

	/**
	 * ApproximationLassoTraining is used to generate a list of term weight by using the Lasso Regression
	 * machine learning algorithm. Thus, we can sort the list and find out which term is most important.
	 * @since 2.0
	 *
	 * @param classifiedDocumentSet 
	 * @param docTermMatrix 
	 * @param delta
	 * @return a hash map storing all the terms and their weight.
	 */
	public static Map<String, Double> ApproximationLassoTraining(
			Map<String, List<String>> classifiedDocumentSet,
			Map<String, TermFrequencyMap> docTermMatrix,
			Double delta
		) {

		// find out relevance of each term
		Map<String, Double> y = new HashMap<String, Double>();
		for (String docId : classifiedDocumentSet.get("rel")) {
			y.put(docId, 1.0);
		}
		for (String docId : classifiedDocumentSet.get("nonrel")) {
			y.put(docId, -1.0 * ((double)classifiedDocumentSet.get("rel").size()
					/ classifiedDocumentSet.get("nonrel").size()));
		}
				
		// prepare the beta array
		Map<String, Double> beta = new HashMap<String, Double>();
		TermFrequencyMap tfMapSample = docTermMatrix.get(docTermMatrix.keySet().iterator().next());
		for (String term : tfMapSample.keySet()) {
			beta.put(term, 0.0);
		}
		for (String term : beta.keySet()) {
			// find document frequency
			int df = 0; 
			for (Entry<String, TermFrequencyMap> item : docTermMatrix.entrySet()) {
				if (item.getValue().containsKey(term) && item.getValue().get(term) > 0) {
					++df;
				}
			}
			
			// find tf*idf of a term
			double weightSum = 0.0;
			for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
				
				int freqOfTermInDoc = vector.getValue().get(term);
				double weight =
						(Math.log10(freqOfTermInDoc) + 1.0)
						*
						(Math.log10(docTermMatrix.size())/ df);
				if (weight < 0) {
					weight = 0;
				}
				weightSum += weight;
			}
			
			// find mean weight
			double meanWeight = (double) weightSum / docTermMatrix.size();
			debugDetails("meanWeight(" + term+ ") = " + meanWeight);
			beta.put(term, meanWeight);			
		}
		debug("Initial beta:\n"+beta);
		
		double estimation = 0.0,
			   newEstimation = 0.0;
		
		// calculate initial estimation
		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
			double dotproduct = 0.0;
			int termf = 0;
			for (String term : beta.keySet()) {
				if (vector.getValue().containsKey(term) && vector.getValue().get(term) > 0 ){
					dotproduct += beta.get(term);
					termf++;
				}
			}
			estimation += Math.pow(y.get(vector.getKey()) - dotproduct, 2);
		}
		debug("estimation(init) = " + estimation);
		
		newEstimation = estimation;
		Map<String, Double> newBeta = null;
		do {
			debug(newEstimation + " < " + estimation);
			if (newEstimation < estimation) {
				debug("Estimation changed..." + estimation);
				beta = newBeta;
				estimation = newEstimation;
			}	
			
			newBeta = beta;
			for (String term : beta.keySet()) {
				double newWeight = beta.get(term) - delta;
				newBeta.put(term, newWeight);
			}
			
			// calculate next estimation
			newEstimation = 0.0;
			for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
				double dotproduct = 0.0;
				for (String term : newBeta.keySet()) {
					if (vector.getValue().containsKey(term) && vector.getValue().get(term) > 0 )
					dotproduct += newBeta.get(term);
				}
				debug(vector.getKey()+": "+(y.get(vector.getKey()) - dotproduct));
				newEstimation += Math.pow(y.get(vector.getKey()) - dotproduct, 2);
			}
			debug("estimation(next) = " + newEstimation);	
			
		} while (newEstimation < estimation);
		
		// build up a common terms for relevant document
		Set<String> commonTerms = new HashSet<String>();
		info("No. of terms orginally = "+beta.size());
		commonTerms.addAll(beta.keySet());
		for (String docId : classifiedDocumentSet.get("rel")) {
			for (String term : beta.keySet()) {
				if (!docTermMatrix.get(docId).containsKey(term)
						|| docTermMatrix.get(docId).get(term) == 0) {
					if (commonTerms.contains(term)) {
						commonTerms.remove(term);
					}
				}
			}
		}
		info("No. of terms after purning = "+commonTerms.size());
		
		// find support of all terms in positive document
		TermFrequencyMap termset = new TermFrequencyMap();
		for (String term : beta.keySet()) {
			for (String docId : classifiedDocumentSet.get("rel")) {
				if (docTermMatrix.get(docId).containsKey(term)
						&& docTermMatrix.get(docId).get(term) > 0) {
					termset.accumulate(term);
				}
			}
		}
		// find support of all terms in negative document
		TermFrequencyMap antiTermset = new TermFrequencyMap();
		for (String term : beta.keySet()) {
			for (String docId : classifiedDocumentSet.get("rel")) {
				if (docTermMatrix.get(docId).containsKey(term)
						&& docTermMatrix.get(docId).get(term) > 0) {
					antiTermset.accumulate(term);
				}
			}
		}
		// remove term that below min support
		// find the optimal minimal support that contains at least 10 terms
		int topNTerms = 10;
		Set<String> supportedTerms = new HashSet<String>();
		supportedTerms.addAll(termset.keySet());
		double minConfident = 0.75;
		for (int i = 100; i > 1 ; --i) {
			Set<String> testTerms = new HashSet<String>();
			testTerms.addAll(supportedTerms);
			double minSupport = i/100.0;
			for (String term : termset.keySet()) {
				if (termset.get(term) > 0) {
					double support = (double) termset.get(term) / classifiedDocumentSet.get("rel").size();
					double confident = (double) termset.get(term) / termset.get(term)+antiTermset.get(term);
					if ((support < minSupport) && (confident < minConfident)) {
						testTerms.remove(term);
					}
				}
			}
			if (testTerms.size() >= topNTerms) {
				supportedTerms = testTerms;
				i = 0;
			}
		}
		info("No. of terms reach min support = "+supportedTerms.size());
		System.out.println(supportedTerms);
		
		// Show Top Ten Terms
		info("Top " + topNTerms + " Terms in decending order:");
		int topTenCounter = 0;
		Set<String> topTenTerms = beta.keySet();
		for (String term : topTenTerms) {
			if (!supportedTerms.contains(term)){
				beta.replace(term, Double.NEGATIVE_INFINITY);
			}
		}
		beta = sortByValue(beta);
		topTenCounter = 0;
		topTenTerms = new HashSet<String>();
		for (String term : beta.keySet()) {
			info(term);
			topTenTerms.add(term);
			if (++topTenCounter >= topNTerms) {
				break;
			}
		}

		// Save term weight into file
		PrintStream out = null;
		try {
			int resultIndex = currentTopic - 100;
	    	File outfile = new File(".//src//resources//result//IFModel//TermWeight"+resultIndex+".txt");
	    	out = new PrintStream(new FileOutputStream(outfile));

	    	for (String term : beta.keySet()) {
	    		out.format("%20s\t%f\n", term, beta.get(term));
			}
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
        	if ( out != null ) {
        		out.close();
        	}
        }

		// calculate relevance ranking
		Map<String, Double> rankingMap = new HashMap<String, Double>();
		for (Entry<String, TermFrequencyMap> vector : docTermMatrix.entrySet()) {
			debug("calculating relevance of..." + vector.getKey());
			double relevance = 0.0;
			for (String term : topTenTerms) {
				boolean termExist = (vector.getValue().containsKey(term) && vector.getValue().get(term) > 0 );
				if (termExist) {
					relevance += beta.get(term);
				}
			}
			debug("rel(" + vector.getKey() + ") = " + relevance);
			rankingMap.put(vector.getKey(), relevance);
		}
		rankingMap = sortByValue(rankingMap);
		
		return rankingMap;
	}
	

	private static void saveDocumentRelevance(
			int topicId, Map<String, Double> rankingMap) {
		rankingMap.forEach(
			(k,v)->info(String.format("MinSupport(%9s) = %f",
					k,v))
		);
		PrintStream out = null;
		try {
			int resultIndex = topicId - 100;
	    	File outfile = new File(".//src//resources//result//IFModel//Result"+resultIndex+".txt");
	    	out = new PrintStream(new FileOutputStream(outfile));

	    	for (String doc : rankingMap.keySet()) {
	    		out.println("R" + topicId + "\t"
	    					+ doc + "\t"
	    				    + ((rankingMap.get(doc) >= 0)? 1 : 0));
			}
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
        	if ( out != null ) {
        		out.close();
        	}
        }
	}
	
	/* This function is referenced to a tutorial from Mkyong.com.
	 * How to sort a Map in Java
	 * https://www.mkyong.com/java/how-to-sort-a-map-in-java/
	 * */
	private static <E> Map<E, Double> sortByValue(Map<E, Double> unsortMap) {
        // 1. Convert Map to List of Map
        List<Map.Entry<E, Double>> list =
                new LinkedList<Map.Entry<E, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<E, Double>>() {
            public int compare(Map.Entry<E, Double> o1,
                               Map.Entry<E, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<E, Double> sortedMap = new LinkedHashMap<E, Double>();
        for (Map.Entry<E, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
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
