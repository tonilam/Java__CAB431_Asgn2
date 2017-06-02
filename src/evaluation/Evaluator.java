package lib.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.TTest;

import lib.files.Log;

/**
 * Evaluator calculates various evaluation measures of relevance from a query.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 23, 2017
 */
public class Evaluator {
	Map<Integer, RelevanceJudgment> benchmarkRec;
	private Map<Integer, RelevanceJudgment>	irRec;
	private int numOfRelevantDocuments,
				numOfRetrievedDocuments,
				numOfRelevantAndRetrievedDoc;

	private int rankingCutoff = 10;

	/**
	 * Constructor - uses to read the given benchmark file and the given output of an IR
	 * model.
	 * @since 1.0
	 * 
	 * @param topic is the topic that related to the query.
	 * @param benchmark is the benchmark file.
	 * @param irOutput is the output of an IR model.
	 * @throws IOException from {@link #scanForRecords}, or if any given file is not
	 * 			a file in the system.
	 * @throws NumberFormatException from {@link #scanForRecords}
	 * @throws RelevanceJudgmentException from {@link #scanForRecords}
	 */
	public Evaluator(String topic, File benchmark, File irOutput)
			throws IOException, NumberFormatException, RelevanceJudgmentException {
		/* Process only if both files are accessible */
		if (benchmark.isFile() && irOutput.isFile()) {
			benchmarkRec = scanForRecords(topic, benchmark);
			irRec = scanForRecords(topic, irOutput);
			
			// find the relevance of IR model result
			for (Entry<Integer, RelevanceJudgment> record : benchmarkRec.entrySet()) {
				RelevanceJudgment newRJ = irRec.get(record.getKey());
				newRJ.relevanceJudgment = record.getValue().relevanceJudgment;
				irRec.replace(record.getKey(), newRJ);
			}
		} else {
			throw new IOException("Cannot open the evaluating file(s).");
		}
		
		// prepare the relevance information for future use.
		countRelevance();
	}
	
	/**
	 * Read the given file and transform every single line into a HashMap
	 * of records containing the document ID and the relevance of that document.
	 * @since 1.0
	 * 
	 * @param topic is the topic related to the current relevance situation.
	 * @param recordFile is the given file for retrieving records.
	 * @return a HashMap of records
	 * @throws FileNotFoundException if Scanner() method call fails.
	 * @throws NumberFormatException from {@link RelevanceJudgment#setJudgmentByString}.
	 * @throws RelevanceJudgmentException from {@link RelevanceJudgment#setJudgmentByString}.
	 */
	private Map<Integer, RelevanceJudgment> scanForRecords(String topic,
					File recordFile)
				throws FileNotFoundException, NumberFormatException,
						   RelevanceJudgmentException {

		Map<Integer, RelevanceJudgment> records
			= new LinkedHashMap<Integer, RelevanceJudgment>();
		Scanner fileScanner;
		String recordScanner;
		
		fileScanner = new Scanner(recordFile);
		
		// Read every line in the file and retrieve the related information.
		while(fileScanner.hasNextLine()) {
			recordScanner = fileScanner.nextLine();
			RelevanceJudgment judgmentRecord = new RelevanceJudgment(topic);
			
			// Try to convert the line of record into a RelevanceJudgment object.
			// If it fails, then warning the user that this record will be skipped.
			try {
				//System.out.println(recordScanner);
				judgmentRecord.setJudgmentByString(recordScanner);
			} catch (RelevanceJudgmentException e) {
				//System.out.println(e.getMessage()+" Skipped one record.");
			}
			records.put(judgmentRecord.getDocId(), judgmentRecord);
		}

		fileScanner.close();
		
		return records;
	}
	

	/**
	 * Count the number of relevant documents and the number of retrieved documents.
	 * @since 2.0
	 */
	private void countRelevance() {
		numOfRelevantDocuments = 0;
		numOfRetrievedDocuments = 0;
		numOfRelevantAndRetrievedDoc = 0;
		RelevanceJudgment inspectingItem,
						  inspectingIrOutput;
		int counter = 0;
		for (int recId : benchmarkRec.keySet()) {
			inspectingItem = benchmarkRec.get(recId);
			if (inspectingItem.isRelevant()) {
				++numOfRelevantDocuments;
			}
			if (++counter>=rankingCutoff) {
				break;
			}
		}

		// Lookup every records in the benchmark file and check the relevance.
		counter = 0;
		for (int recId : irRec.keySet()) {
			inspectingItem = benchmarkRec.get(recId);
			inspectingIrOutput = irRec.get(recId);

			if (inspectingIrOutput.isRelevant()) {
				++numOfRelevantAndRetrievedDoc;
			}
			++numOfRetrievedDocuments;

			if (++counter>=rankingCutoff) {
				break;
			}
		}
	}
	
	/**
	 * Get the number of relevant documents in the benchmark data set.
	 * @since 1.0
	 * 
	 * @return the total number of relevant documents.
	 */
	public int getNumOfRelevantDocuments() {
		return numOfRelevantDocuments;
	}
	
	/**
	 * Get the number of retrieved documents in the output of IR model data set.
	 * @since 2.0
	 * 
	 * @return the total number of retrieved documents.
	 */
	public int getNumOfRetrievedDocuments() {
		return numOfRetrievedDocuments;
	}
	
	/**
	 * Get the number of documents that is both retrieved and relevant.
	 * @since 1.0
	 * 
	 * @return the number of documents in both sets.
	 */
	public int getNumOfRetrievedAndRelevantDocuments() {
		return numOfRelevantAndRetrievedDoc;
	}
	
	/**
	 * Calculate the recall value of this model.
	 * @since 1.0
	 * 
	 * @return the recall value.
	 * @throws ArithmeticException if no relevant document found.
	 */
	public double calculateRecall() throws ArithmeticException {
		/* detect any possible error in the calculation */
		if (numOfRelevantDocuments == 0) {
			return 0;
			/*
			throw new ArithmeticException(
					"Cannot calculate the recall as there is no"
					+ " relevant document found.");
					*/
		}
				
		/*
		 * Let A = set of relevant documents.
		 * Let B = set of retrieved documents.
		 * 
		 * Recall = (A and B) divided by A */
		return (double)numOfRelevantAndRetrievedDoc / numOfRelevantDocuments;
	}

	/**
	 * Calculate the precision value of this model.
	 * 
	 * @return the precision value.
	 * @throws ArithmeticException if no retrieved document found.
	 */
	public double calculatePrecision() throws ArithmeticException {
		/* detect any possible error in the calculation */
		if (numOfRetrievedDocuments == 0) {
			return 0;
			/*
			throw new ArithmeticException(
					"Cannot calculate the precision as there is no"
					+ " retrieved document found.");
					*/
		}
		
		/*
		 * Let A = set of relevant documents.
		 * Let B = set of retrieved documents.
		 * 
		 * Recall = (A and B) divided by B */
		return (double)numOfRelevantAndRetrievedDoc / numOfRetrievedDocuments;
	}

	/**
	 * Calculate the F-Measure value of this model.
	 * 
	 * @return the F-Measure value.
	 * @throws ArithmeticException if no document found.
	 */
	public double calculateFMeasure() throws ArithmeticException{
		double recall = calculateRecall();
		double precision = calculatePrecision();
		
		/* detect any possible error in the calculation */
		if (recall == 0 && precision == 0) {
			return 0;
			/*
			throw new ArithmeticException(
					"Cannot calculate the F-Measure value as there is no "
					+ "relevant document nor retrieved document found.");
					*/
		}
		
		// return the result of the F-Measure formula in the task sheet.
		return (2 * recall * precision) / (recall + precision);
	}

	/**calculateAveragePrecision 
	 * @since
	 *
	 * @return
	 */
	public double calculateAveragePrecision() {
		int counter = 1;
		double totalPrecision = 0.0;
		int noOfDocRetrived = 0;
		Log.out("Top 10 Precision:");
		for (Entry<Integer, RelevanceJudgment> record : irRec.entrySet()) {
			if (record.getValue().isRelevant()) {
				totalPrecision += ++noOfDocRetrived / (double)counter;
			}
			Log.out("#%2d: %.2f",
					counter, noOfDocRetrived / (double)counter);
			if (counter++ >= rankingCutoff) {
				break;
			}
		}
		return totalPrecision / (double)rankingCutoff;
	}

	/**tTest 
	 * use to reject null hypothesis
	 * @since
	 *
	 * @return
	 */
	public static boolean tTest(double[] mapBlModel, double[] mapIfModel) {
		TTest relevenceTest = new TTest();
		boolean testResult = false;
		double error = 0;
		double tValue = 0;
		
		// For TTest, significantLevel alpha : 0 < alpha < 0.5
		double significantLevel = 0.05;
		
		double meanBlModel = StatUtils.mean(mapBlModel);
		double meanIfModel = StatUtils.mean(mapIfModel);
		

		Log.out("Perform T-Test:");
		try {
			testResult = relevenceTest.tTest(mapBlModel, mapIfModel, significantLevel);
			error = relevenceTest.tTest(mapBlModel, mapIfModel);
			tValue = relevenceTest.t(mapBlModel, mapIfModel); 
			Log.out(">>> p-value %.5f and tValue %.5f",
					error, tValue);
			if (testResult) {
				Log.out(">>> The null hypothesis is rejected. Two models are different.");
				if (meanBlModel > meanIfModel) {
					Log.out(">>> The baseline model is better than the IF model.");
				} else {
					Log.out(">>> The IF model is better than the baseline model.");
				}
			} else {
				Log.out(">>> The null hypothesis is accepted. Two models are the same.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return testResult;
	}
}
