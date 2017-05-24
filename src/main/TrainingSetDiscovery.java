/**
 * 
 */
package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import factories.RelevenceFactory;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, May 22, 2017
 */
public class TrainingSetDiscovery {

	/**main 
	 * @since
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		RelevenceFactory relfty = new RelevenceFactory();
		for (int i = 1; i <= 50; ++ i) {
			System.out.println("R"+(100+i));
			HashMap<String, HashMap<Integer, Double>> result = relfty.getReleventDocument("R"+(100+i));
			System.out.println("Topic keywords: " + relfty.getQueryString());
			System.out.printf("Total %d relevant documents.\n", result.get("rel").size());
			
			BufferedWriter output = null;
		    try {
		    	File file = new File(".//src//resources//result//BaselineResult"+i+".txt");
		    	PrintStream out = new PrintStream(new FileOutputStream(file));

	            out.println("Method = TF*IDF Ranking Function");
	            out.println("Threshold = " + relfty.getThreshold());
	            out.println("Total number of documents = " + (result.get("rel").size() + result.get("nonrel").size()));
	            out.println("No. of relevent documents = " + result.get("rel").size());
	            out.println("No. of non-relevent documents = " + result.get("nonrel").size());
	            out.println("* * * * * * * * * * * * * * * * * * * *");

	            for (Entry<Integer, Double> doc : sortByValue(result.get("rel")).entrySet()) {
	            	out.println(doc.getKey() + "\t+\t" + doc.getValue() );
				}

	            for (Entry<Integer, Double> doc : sortByValue(result.get("nonrel")).entrySet()) {
	            	out.println(doc.getKey() + "\t-\t" + doc.getValue() );
				}
	        } catch ( IOException e ) {
	            e.printStackTrace();
	        } finally {
	        	if ( output != null ) {
	        		try {
	        			output.close();
	        		} catch (IOException e) {
	        			e.printStackTrace();
	        		}
	        	}
	        }

			System.out.printf("Total %d non-relevant documents.\n", result.get("nonrel").size());
		}
		
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
