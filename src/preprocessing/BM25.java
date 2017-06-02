package lib.preprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import models.Document;
import models.TermFrequencyMap;

public class BM25 {
	/**
	 * Constructor.
	 * @param collection is the bag of words collection used to calculate the BM25 value.
	 */
	public BM25() {		
	}
	
	/**
	 * It calculates the BM25 value of the given document and query.
	 * @since 1.0
	 * 
	 * @param tfMap - a BowDocument
	 * @param aQuery ¡V a String of query
	 * @param avgDocLen - an integer of the average document length
	 * @param docNo ¡V an integer of total number of document in a collection
	 * @return a double value of given document¡¦s BM25 score.
	 */
	public static double calculateBM25(
			TermFrequencyMap docTfMap,
			TermFrequencyMap queryTfMap,
			double avgDocLength,
			Map<String, TermFrequencyMap> docTermMatrix,
			int totalNoOfDocInCollection,	// the total number of documents in given documents collection
			List<String> relDoc
			) {
		double k1 = 1.2; // parameters in the equation
		double b = 0.75; // parameters in the equation
		double k2 = 100; // parameters in the equation
		double K = 0;
		int noOfRelDocContainsTerm = 0;
		
		double result = 0;
		
		int wordCount = 0;
		for (Entry<String, Integer> tf : docTfMap.entrySet()) {
			wordCount += tf.getValue();
		}
				
		// calculate the score of  each single term
		for (String term : queryTfMap.keySet()) {
			double lengthRatio = (double)wordCount/avgDocLength;
			K = k1*( (1-b)+b*lengthRatio );
			int docFreq = 0;
			int termFreqInDoc = docTfMap.containsKey(term)? docTfMap.get(term) : 0;
			int termFreqInQuery = queryTfMap.containsKey(term)? queryTfMap.get(term) : 0;
			for (Entry<String, TermFrequencyMap> freqVector : docTermMatrix.entrySet()) {
				if (freqVector.getValue().containsKey(term)
					&& (freqVector.getValue().get(term) > 0)) {
					++docFreq;
				}
				if (relDoc.contains(freqVector.getKey())) {
					++noOfRelDocContainsTerm;
				}
			}
			result += calculateScoreForSingleQueryTerm(
					noOfRelDocContainsTerm, //no. of relevant document contain term-i
					relDoc.size(), //total no. of relevant documents in the collection
					docFreq, //document frequency of this term
					totalNoOfDocInCollection, // total no. of document in the collection
					termFreqInDoc, //term frequency in this document
					termFreqInQuery, // term frequency in the query
					K,k1,k2
					);
		}
		return result;
	}
	
	/**
	 * It follows the equation of BM25 to calculate the score of one single query term. 
	 * @since 1.0
	 *
	 * @param r is the no. of relevant document
	 * @param R is the total no. of relevant documents in the collection
	 * @param n is the document frequency of this term
	 * @param N is the total no. of document in the collection
	 * @param f is the term frequency in this document
	 * @param qf is the term frequency in the query
	 * @param K is a parameter in the equation
	 * @param k1 is a parameter in the equation
	 * @param k2 is a parameter in the equation
	 * @return the score of this single query term
	 */
	private static double calculateScoreForSingleQueryTerm(
			double r,  //noOfRelevantDoc
			double R,  //totalNoOfRelevantDocInCollection
			double n,  //docFreqOfTerm
			double N,  //totalNoOfDocInCollection
			double f,  //termFreInDoc
			double qf, //termFreqInQuery
			double K,
			double k1,
			double k2) {
		double result;
		result =
			(
				Math.log(
					//This blog explain why we need to add 1 before log:
					//http://opensourceconnections.com/blog/2015/10/16/bm25-the-next-generation-of-lucene-relevation/
					1
					+
						(
							(r + 0.5) / (R - r + 0.5)
						)
						/
						(
							(n-r+0.5)
							/
							(N-n-R+r+0.5)
						)
					)
					*
					(
						((k1 + 1) * f)
						/
						(K+f)
					)
					*
					(
						((k2+1)*qf)
						/
						(k2+qf)
					)
				);

		return result;
		
	}

}
