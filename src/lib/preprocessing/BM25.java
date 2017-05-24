package lib.preprocessing;

import java.util.StringTokenizer;

public class BM25 {
	private BowCollection bowCollection;
	private Stemmer snowballStemmer;
	
	/**
	 * Constructor.
	 * @param collection is the bag of words collection used to calculate the BM25 value.
	 */
	public BM25(BowCollection collection) {		
		// this class use the stemmer so that the query's words will match the bag of words.
		snowballStemmer = new Stemmer(collection.isStemmingEnabled());

		bowCollection = collection;
	}
	
	/**
	 * It calculates the BM25 value of the given document and query.
	 * @since 1.0
	 * 
	 * @param aDoc - a BowDocument
	 * @param aQuery ¡V a String of query
	 * @param avgDocLen - an integer of the average document length
	 * @param docNo ¡V an integer of total number of document in a collection
	 * @return a double value of given document¡¦s BM25 score.
	 */
	public double calculateBM25(BowDocument aDoc, String aQuery, int avgDocLen, int docNo) {
		int totalNoOfDocInCollection = docNo; // the total number of documents in given documents collection
		int docFreqOfTerm = 0; // the document frequency of term i
		double noOfRelevantDoc = 0; // the number of relevant documents which contain term i, usually not given, so we just assume it is zero
		double totalNoOfRelevanDocInCollection = 0; // is total number of relevant documents in given document collection, usually not given so we just assume it is zero
		int termFreInDoc = 0; // the term i frequency in given document D
		double k1 = 1.2; // parameters in the equation
		double b = 0.75; // parameters in the equation
		double k2 = 100; // parameters in the equation
		double K = 0;
		double termFreqInQuery = 1; // the term i frequency in given query Q
		
		double result = 0;
		
		DocumentFrequency df = new DocumentFrequency();
		df.calculateDF(bowCollection);
		
		// tokenize the query and calculate the score of  each single term
		StringTokenizer st = new StringTokenizer(aQuery);
		String querykey;
		while (st.hasMoreTokens()) {
			querykey = snowballStemmer.stemming(st.nextToken().toLowerCase());
			docFreqOfTerm = df.getRelevantDocByTerm(querykey);
			termFreInDoc = aDoc.getTermFreq(querykey);
			double lengthRatio = (double)aDoc.getWordCount()/avgDocLen;
			K = k1*( (1-b)+b*lengthRatio );
			result += calculateScoreForSingleQueryTerm(
					noOfRelevantDoc,
					totalNoOfRelevanDocInCollection,
					docFreqOfTerm,
					totalNoOfDocInCollection,
					termFreInDoc,
					termFreqInQuery,
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
	private double calculateScoreForSingleQueryTerm(
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
