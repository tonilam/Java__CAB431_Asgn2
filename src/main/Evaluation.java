package main;

import java.io.File;

import org.apache.commons.math3.stat.StatUtils;

import lib.evaluation.Evaluator;
import lib.files.Log;

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
	static double precisions_BaselineModel[];
	static double precisions_IFModel[];
	
	public static void main(String[] args) {
		Log.create(".//src//resources//result//Evaluation//EvaluationResult.dat");
		Log.verbose(true);
		int startDoc = 1;
		int maxDoc = 50;
		precisions_BaselineModel = new double[maxDoc];
		precisions_IFModel = new double[maxDoc];
		for (int i = startDoc; i < startDoc + maxDoc; ++i) {
			Log.out("==================================================================================");
			Log.out("===========================      Topic ID R1%02d      ==============================",
					i);
			Log.out("==================================================================================");
			
			evaluateTopic(i);
		}

		Log.out("==================================================================================");
		Log.out("==========================      Model Comparision      ===========================");
		Log.out("==================================================================================");
		Log.out("Mean Average Precision (MAP):");
		Log.out(">>> MAP for Baseline Model: %f",
				StatUtils.mean(precisions_BaselineModel));
		Log.out(">>> MAP for IF Model: %f",
				StatUtils.mean(precisions_IFModel));
		Evaluator.tTest(precisions_BaselineModel, precisions_IFModel);
		Log.write();
	}
		
	public static void evaluateTopic(int topicId) {
		File benchmark = new File(String.format(".//src//resources//topicassignment101-150//Training1%02d.txt", topicId));
		File irOutput = new File(String.format(".//src//resources//result//BaselineModel//result%d.txt", topicId));
		File ifOutput = new File(String.format(".//src//resources//result//IFModel//result%d.txt", topicId));

		try {
			/**
			 * Evaluator may throw the following exception:
			 * IOException, NumberFormatException, RelevanceJudgmentException
			 */
			Log.out("Baseline Model");
			String topic = String.format("R1%02d", topicId); 
			Evaluator evaluator = new Evaluator(topic, benchmark, irOutput);
			double averagePrecision = evaluator.calculateAveragePrecision();
			double fMeasure = evaluator.calculateFMeasure();

			Log.out("F1 Measure: %.12f",
					fMeasure);
			Log.out("Average Precision: %.12f", averagePrecision);
			Log.linebreak();
			precisions_BaselineModel[topicId-1] = averagePrecision;
		

			Log.out("IF Model");
			evaluator = new Evaluator(topic, benchmark, ifOutput);
			averagePrecision = evaluator.calculateAveragePrecision();
			fMeasure = evaluator.calculateFMeasure();

			Log.out("F1 Measure: %.12f",
					fMeasure);
			Log.out("Average Precision: %.12f",
					averagePrecision);
			Log.linebreak();
			precisions_IFModel[topicId-1] = averagePrecision;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
