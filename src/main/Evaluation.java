package main;

import java.io.File;

import config.AppConfig;
import lib.evaluation.Evaluator;

/**
 * <p>TASK 1: read a topic-doc-assignments file (e.g., topicdocassign.txt, the benchmark) 
 * and a retrieved topic-doc-assignments file (e.g., topicdocassigntest.txt, the output 
 * of an IR model); calculate three evaluation measures of Recall, Precision, and F-Measure.
 * <ul>
 * 	<li>Please download two topic-doc-assignment files, open and read through. The given 
 * 		topic-assignment files are in format of ¡§topic documentID Relevance judgment¡¨ using 1 
 * 		to indicate relevant and 0 for non-relevant.</li>
 * 	<li>Create a new class evaluator, read in two given topic-assignment files. Build up 
 * 		a set of relevant documents containing all relevant documents based on 
 * 		topic-doc-assignment file, a set of retrieved documents containing all retrieved 
 * 		documents based on a rank file.
 *	<li>Create a method to calculate Recall, a method to calculate Precision, and a method 
 *		to calculate F-measure. Display three evaluation measures.</li>
 * </ul>
 * @author Toni Lam
 *
 * @since 1.0
 * @version 2.0, Apr 23, 2017
 */
public class Evaluation {
	
	public static int topicId = 1;

	public static void main(String[] args) {
		for (int i = 1; i <= 50; ++i) {
			System.out.println("==================================================================================");
			System.out.format("=======================      Topic ID R1%02d      ==============================\n", i);
			System.out.println("==================================================================================");
			
			evaluateTopic(i);
		}
	}
		
	public static void evaluateTopic(int topicId) {
		File benchmark = new File(String.format(".//src//resources//topicassignment101-150//Training1%02d.txt", topicId));
		File irOutput = new File(String.format(".//src//resources//result//BaselineResult%d.txt", topicId));
		File ifOutput = new File(String.format(".//src//resources//result//Result%d.txt", topicId));
		
		try {
			/**
			 * Evaluator may throw the following exception:
			 * IOException, NumberFormatException, RelevanceJudgmentException
			 */
			String topic = String.format("R1%02d", topicId); 
			Evaluator evaluator = new Evaluator(topic, benchmark, irOutput);
			int numOfRelevantDoc = evaluator.getNumOfRelevantDocuments();
			int numOfRetrievedAndRelevantDoc = evaluator.getNumOfRetrievedAndRelevantDocuments();
			double recall = evaluator.calculateRecall();
			double precision = evaluator.calculatePrecision();
			double fMeasure = evaluator.calculateFMeasure();
			
			System.out.format("The number of relevant documents = %d\n", numOfRelevantDoc);
			System.out.format("The number of retrieved relevant documents = %d\n", numOfRetrievedAndRelevantDoc);
			System.out.format("recall = %.6f\n", recall);
			System.out.format("precision = %.6f\n", precision);
			System.out.format("F-Measure = %.6f\n", fMeasure);

		
		
			evaluator = new Evaluator(topic, benchmark, ifOutput);
			numOfRelevantDoc = evaluator.getNumOfRelevantDocuments();
			numOfRetrievedAndRelevantDoc = evaluator.getNumOfRetrievedAndRelevantDocuments();
			recall = evaluator.calculateRecall();
			precision = evaluator.calculatePrecision();
			fMeasure = evaluator.calculateFMeasure();
			
			System.out.format("The number of relevant documents = %d\n", numOfRelevantDoc);
			System.out.format("The number of retrieved relevant documents = %d\n", numOfRetrievedAndRelevantDoc);
			System.out.format("recall = %.6f\n", recall);
			System.out.format("precision = %.6f\n", precision);
			System.out.format("F-Measure = %.6f\n", fMeasure);
		
		
		
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
