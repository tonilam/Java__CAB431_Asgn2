/**
 * 
 */
package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import config.AppConfig;

import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import factories.TopicOfInterestFactory;
import lib.preprocessing.BowCollection;
import lib.preprocessing.BowDocument;
import lib.preprocessing.TfIdf;
import models.RelevanceJudgment;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, May 25, 2017
 */
public class InformationFilteringModel {
	
	static String trainingFolder;
	static BowCollection bowCollection;

	static HashMap<Integer, Double> y = new HashMap<Integer, Double>();
	
	/**main 
	 * @since
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		TopicOfInterestFactory toifty = new TopicOfInterestFactory();
		StringTokenizer st;
		RelevanceJudgment relevance = new RelevanceJudgment();
		List<Integer> relDoc = new ArrayList<Integer>();
		List<Integer> nonrelDoc = new ArrayList<Integer>();
		
		for (int i = 1; i <= 50; ++ i) {			
			File file = new File(".//src//resources//result//BaselineResult"+i+".txt");
			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					if (scanner.nextLine().matches("(\\*\\W)+(\\*)+(\\W)*")) {
						while (scanner.hasNext()) {
							st = new StringTokenizer(scanner.nextLine());
							relevance = new RelevanceJudgment();
							relevance.topicId = "R"+(100+i);
							relevance.docId = Integer.parseInt(st.nextToken());
							relevance.relevance = st.nextToken().equals("+");
							relevance.degree = Double.parseDouble(st.nextToken());
							
							if (relevance.relevance) {
								relDoc.add(relevance.docId);
							} else {
								nonrelDoc.add(relevance.docId);
							}
						}
					}
				}
				scanner.close();
			} catch (IOException e) {
				System.out.println("Error on reading file.\n" + e);
			}
			
			toifty.setTopic(relevance.topicId);
			toifty.setTopics(".//src//resources//TopicStatements101-150.txt");
			
			Double delta = 0.005;
			HashMap<String, Double> beta = ApproximationLassoTraining( relevance.topicId,
										relDoc.size() + nonrelDoc.size(),
										relDoc,
										nonrelDoc,
										toifty.getTerms(),
										delta
										);
//			for (String term : toifty.getTerms()) {
//				double result = 0.0;
//				Iterator<Integer> relDocSet = relDoc.iterator();
//				int docId = 0;
//				while (relDocSet.hasNext()) {
//					docId = relDocSet.next();
//					BowDocument doc = bowCollection.get(docId);
//					if (doc.getTermFreq(term) > 0) {
//						System.out.println(term + " found in this doc ~ " + beta.get(term));
//						result += beta.get(term);
//					} else {
//						result += 0;
//					}
//				}
//				result = y.get(docId)- result;
//				System.out.println(term + " = " + result);
//			}
			
			// find out the smallest weight in the term set
			double floorBeta = 1.0;
			for (String term : beta.keySet()) {
				if (beta.get(term) < floorBeta) {
					floorBeta = beta.get(term);
				}
			}
			
			// Find out the sum of all term weight for normalization.,
			// so that the total term weight of a document s would be 0 <= s <= 1.
			// Hence, if a document contains all relevant terms, the total term weight should be 1.
			// This is useful when we do the document rating.
			double normalizeBase = 0.0;
			for (String term : beta.keySet()) {
				normalizeBase += beta.get(term) - floorBeta;
			}
			
			System.out.println("Selected terms for " + relevance.topicId + ":");
			for (String term : beta.keySet()) {
				beta.put(term, (beta.get(term) - floorBeta) / normalizeBase);
				if (beta.get(term) > 0) {
					System.out.format("+ %.10f\t%s\n", beta.get(term), term);
				} else {
					System.out.format("- %s\n", term);
				}
			}
			
			Map<Integer, Double> rankedDoc = new HashMap<Integer, Double>();
			for (Entry<Integer, BowDocument> doc : bowCollection.entrySet()) {
				HashMap<String, Integer> termFreq = doc.getValue().getTermFreqMap();
				double weight = 0.0;
				for (String term : termFreq.keySet()) {
					if (beta.containsKey(term)) {
						weight += beta.get(term);
					}
				}
				rankedDoc.put(doc.getKey(), weight);
			}
			
			double minSupport = bowCollection.size() * 0.15;
			double threshold = (1/minSupport) * Math.log(bowCollection.size()/minSupport);
			HashMap<Integer, Double> releventDocuments = new HashMap<Integer, Double>();
			HashMap<Integer, Double> nonreleventDocuments = new HashMap<Integer, Double>();
			for (Entry<Integer, Double> doc : sortByValue(rankedDoc).entrySet()) {
				if (doc.getValue() > threshold) {
					releventDocuments.put(doc.getKey(), doc.getValue());
				} else {
					nonreleventDocuments.put(doc.getKey(), doc.getValue());
				}
			}
			
			PrintStream out = null;
			try {
		    	File outfile = new File(".//src//resources//result//Result"+i+".txt");
		    	out = new PrintStream(new FileOutputStream(outfile));

	            out.println("Method = Lasso Mechine Learning Function");
	            out.println("Threshold = " + threshold);
	            out.println("Total number of documents = " + bowCollection.size());
	            out.println("No. of relevent documents = " + releventDocuments.size());
	            out.println("No. of non-relevent documents = " + nonreleventDocuments.size());
	            out.println("* * * * * * * * * * * * * * * * * * * *");

	            for (Entry<Integer, Double> doc : sortByValue(releventDocuments).entrySet()) {
	            	out.println(doc.getKey() + "\t+\t" + doc.getValue() );
				}

	            for (Entry<Integer, Double> doc : sortByValue(nonreleventDocuments).entrySet()) {
	            	out.println(doc.getKey() + "\t-\t" + doc.getValue() );
				}
	        } catch ( IOException e ) {
	            e.printStackTrace();
	        } finally {
	        	if ( out != null ) {
	        		out.close();
	        	}
	        }
//			double sumOfRow = 0.0;
//			for (int doc : relDoc) {
//				double dotProduct = 0.0;
//				for (String betaKey : beta.keySet()) {
//					for (Entry<Integer, BowDocument> docEntry : bowCollection.entrySet()) {
//						if (docEntry.getValue().getTermFreq("betaKey") > 0) {
//							dotProduct += beta.get(betaKey);
//						}
//					}
//				}
//				sumOfRow += y.get(doc) - dotProduct;
//			}
//			System.out.println(" Lasso result = " + sumOfRow);
			
		}
	}
	
	/**
	 * ApproximationLassoTraining is used to generate a list of term weight by using the Lasso Regression
	 * machine learning algorithm. Thus, we can sort the list and find out which term is most important.
	 * @since 2.0
	 *
	 * @param topic
	 * @param trainingSize
	 * @param relDoc
	 * @param nonrelDoc
	 * @param terms
	 * @param delta
	 * @return a hash map storing all the terms and their weight.
	 */
	public static HashMap<String, Double> ApproximationLassoTraining( String topic,
											int trainingSize,
											List<Integer> relDoc,
											List<Integer> nonrelDoc,
											Set<String> terms,
											double delta
											) {

		// prepare the set of input documents
		trainingFolder = topic.replace("R", "Training");
		bowCollection = new BowCollection(AppConfig.DEFAULT_DATASET_DIR + trainingFolder + "//");
		
		Iterator<Integer> relDocSet = relDoc.iterator();
		while (relDocSet.hasNext()) {
			int docId = relDocSet.next();
			y.put(docId, 1.0);
			//System.out.println("y[" + docId + "] = " + y.get(docId));
		}
		Iterator<Integer> nonrelDocSet = nonrelDoc.iterator();
		while (nonrelDocSet.hasNext()) {
			int docId = nonrelDocSet.next();
			y.put(docId, -1.0 * ((double)relDoc.size() / nonrelDoc.size()));
			//System.out.println("y[" + docId + "] = " + y.get(docId));
		}

		
		// prepare the beta array
		HashMap<String, Double> beta = new HashMap<String, Double>();
		Iterator<String> termset = terms.iterator();
		TfIdf tfidfAlgorithm = new TfIdf(bowCollection);
		while (termset.hasNext()) {
			String term = termset.next();
			Map<Integer, Double> termArray = new HashMap<Integer, Double>();
			//System.out.println("term[" + i++ + "] = " + term);
			for (Entry<Integer, BowDocument> doc : bowCollection.entrySet()) {
				HashMap<String, Double> weight = tfidfAlgorithm.calculateTfIdf(doc.getValue(), trainingSize);
				if (weight.keySet().contains(term)) {
					termArray.put(doc.getKey(), weight.get(term));
					//System.out.println(doc.getKey() +"[" + term + "] = " + weight.get(term) );
				}
			}
			double avgWeight = 0.0;
			for (double termWeight : termArray.values()) {
				avgWeight += termWeight;
			}
			avgWeight /= termArray.size() + Math.pow(1, -100);	// add a very small value to avoid divide by zero
			//System.out.println("Avg weight = " + avgWeight );
			beta.put(term, avgWeight);
		}
		
		// Estimation
		double estimation = 0.0;
		for (Entry<Integer, BowDocument> doc : bowCollection.entrySet()) {
			double sumOfMatrix = 0.0;
			for (String term : beta.keySet()) {
				sumOfMatrix += beta.get(term) * ((doc.getValue().getTermFreq(term) > 0)? 1 : 0);
			}
			estimation += Math.pow(y.get(doc.getKey()) - sumOfMatrix, 2);
		}
		//System.out.println("estimation = " + estimation );
		

		double newEstimation = 0.0;
		boolean optimal = false;
		do {
			
			termset = terms.iterator(); // restart terms iterator
			while (termset.hasNext()) {
				String term = termset.next();
				beta.put(term, beta.get(term) + delta);
				//System.out.println("Update beta[" + term + "] = " + beta.get(term) );
			}
			
			// Estimation 1
			newEstimation = 0.0;
			for (Entry<Integer, BowDocument> doc : bowCollection.entrySet()) {
				double sumOfMatrix = 0.0;
				for (String term : beta.keySet()) {
					sumOfMatrix += beta.get(term) * ((doc.getValue().getTermFreq(term) > 0)? 1 : 0);
				}
				newEstimation += Math.pow(y.get(doc.getKey()) - sumOfMatrix, 2);
			}
			//System.out.println("new estimation = " + newEstimation );
			if (newEstimation < estimation) {
				estimation = newEstimation;
			} else {
				optimal = true;
			}
		} while (!optimal);
		
		return beta;

	}
	

	/* This function is referenced to a tutorial from Mkyong.com.
	 * How to sort a Map in Java
	 * https://www.mkyong.com/java/how-to-sort-a-map-in-java/
	 * */
	private static Map<Integer, Double> sortByValue(Map<Integer, Double> unsortMap) {
        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<Map.Entry<Integer, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : list) {
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
}
