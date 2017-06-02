package lib.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * RankingMap stores a set of documents, their ranking number, and their relevance.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 23, 2017
 * 
 * @param <K> is the ranking number for each entry represented by an integer.
 * @param <V> is the document ID for each entry represented by an integer.
 */
public class RankingMap<K, V> extends LinkedHashMap<Integer, Integer> {
	
	private HashMap<Integer, Boolean> relevance;
	private int numOfRelevantDoc;

	/**
	 * Implementation of Serializable needs a serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor to initialize the class variable.
	 * @since 1.0
	 */
	public RankingMap() {
		super();
		relevance = new LinkedHashMap<Integer, Boolean>();
		numOfRelevantDoc = 0;
	}
	
	/**
	 * Load and retrieve the ranking records from the given file.
	 * @since 1.0
	 * 
	 * @param dataset is the data set file containing the ranking records.
	 * @throws IOException if the given data set is not a file.
	 */
	public void loadFromFile(File dataset) throws IOException {
		final String DELIMITER = " ";
		Scanner fileScanner;
		String recordScanner;
		String[] fields;
		int rankingNo,
			documentId;
		
		if (dataset.isFile()) {
			fileScanner = new Scanner(dataset);
			
			/* Retrieve each record line by line */
			while(fileScanner.hasNextLine()) {
				recordScanner = fileScanner.nextLine();
				fields = recordScanner.split(DELIMITER);
				try {
					rankingNo = Integer.parseInt(fields[0]);
					documentId = Integer.parseInt(fields[1]);
				} catch (NumberFormatException e) {
					System.out.println("Error in reading ranking record."
									   + " Skipped one line.");
					continue;
				}
				this.put(rankingNo, documentId);
			}

			fileScanner.close();
		} else {
			throw new IOException("Error in reading data source.");
		}
	}

	/**
	 * Identify the relevance of the data set from the relevant documents.
	 * @since 1.0
	 * 
	 * @param relevantDoc is the data set included in the ranking record set.
	 * @throws FileNotFoundException @see java.util.Scanner
	 */
	public void identifyRelevantDoc(File relevantDoc) throws FileNotFoundException {
		final String DELIMITER = " ";
		HashMap<Integer, Boolean> records = new HashMap<Integer, Boolean>();
		Scanner fileScanner;
		String[] fields;
		boolean isRelevant;
	
		fileScanner = new Scanner(relevantDoc);
	
		// Lookup every line and check if the record is marked as relevant.
		while(fileScanner.hasNextLine()) {
			fields = fileScanner.nextLine().split(DELIMITER);
			records.put(Integer.parseInt(fields[1]), (fields[2].equals("1")));
		}
		
		fileScanner.close();
		
		// Check every value in ranking map and determine its relevance.
		// The relevance record is append to the relevance map.
		// If it is relevant, then add the no. of relevant documents by 1.
		for (int docId : this.values()) {
			if (records.containsKey(docId)) {
				isRelevant = records.get(docId);
				relevance.put(docId, isRelevant);
				if (isRelevant) {
					++numOfRelevantDoc;
				}
			}
		}
	
	}
	
	/**
	 * Check if the give document ID is a relevant document.
	 * @since 1.0
	 * 
	 * @param docId is the document id of the checking file.
	 * @return true if the document is relevant;
	 * 		   false if the document is not relevant or the document not listed.
	 */
	public Boolean isRelevant(int docId) {
		if (relevance.containsKey(docId)) {
			return relevance.get(docId);
		} else {
			return false;
		}
	}

	/**
	 * Accessor of the number of relevant document stored in this class.
	 * @since 1.0
	 * 
	 * @return the number of relevant document.
	 */
	public int getNumOfRelevantDoc() {
		return numOfRelevantDoc;
	}

	/**
	 * Calculate the precision value at this fixed position.
 	 * @since 1.0
	 *
	 * @param fixedPosition is the current position, non-zero value.
	 * @return the precision value.
	 */
	public double calculateFixedRankPositionPrecision(int fixedPosition) {
		int numOfRelevantDocInFixedPos = findNumOfRelevantDocInFixedPos(fixedPosition);

		return (double)numOfRelevantDocInFixedPos / fixedPosition;
	}

	/**
	 * Calculate the recall value at this fixed position.
	 * @since 1.0
	 * 
	 * @param fixedPosition is the current position, non-zero value.
	 * @return the recall value.
	 */
	public double calculateFixedRankPositionRecall(int fixedPosition) {
		int numOfRelevantDocInFixedPos = findNumOfRelevantDocInFixedPos(fixedPosition);
		int totalRelevantDocInFixedPos = findNumOfRelevantDocInFixedPos(this.size());
		
		return (double)numOfRelevantDocInFixedPos / totalRelevantDocInFixedPos;
	}
	
	/**
	 * It lookup the relevance list from the beginning to the given position,
	 * and counts how many relevant documents there. 
	 * @since 1.0
	 *
	 * @param fixedPosition indicates the fixed position of this calculation.
	 * @return the number of relevant document at the fixed position.
	 */
	private int findNumOfRelevantDocInFixedPos(int fixedPosition) {
		int counter = 0;
		int step = 0;
		
		for (boolean isRevelant: relevance.values()) {
			// check if the checking position is in range.
			if (++step <= fixedPosition) {
				if (isRevelant) {
					++counter;
				}
			} else {
				// end the searching if the position is out of boundary.
				break;
			}
		}
		
		return counter;
	}

	/**
	 * It sums up all the precision in every position where the document is relevant
	 * and calculate the average precision.  
	 * @since 1.0
	 *
	 * @return the average precision of this map
	 */
	public double getAveragePrecision() {
		int position = 1;
		double sum = 0;
		
		for (boolean isRevelant: relevance.values()) {
			if (isRevelant) {
				sum += calculateFixedRankPositionPrecision(position);
			}
			position++;
		}
		return sum / numOfRelevantDoc;
	}
}
