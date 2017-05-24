package factories.handler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import models.TopicOfInterest;

/**
 * XmlHandler extends DefaultHandler to decode the XML file into self-defined
 * XmlStructure. 
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017 
 */
public class TopicOfInterestHandler {
 
	private HashMap<String, TopicOfInterest> topicStatements;
    private ArrayList<String> elements; 
    
	/**
	 * Constructor.
	 * @param filename indicates which file to be handled.
	 */
	public TopicOfInterestHandler() {
		String[] tags = new String[]{"num", "desc", "narr", "title"};

		topicStatements = new HashMap<String, TopicOfInterest>();
		
		elements = new ArrayList<String>();
		for(String name : tags) {
			elements.add(name);
		}
		
		
	}
	

	/**
     * It return the XmlStructure of the current handling XML file.
	 * @param topic	is the topic number of a topic of interest
	 * @param uri	is the location of the topic of interest file
     * @return XmlStructure that stored the context of the corresponding file.
     * @since 1.0
     */
    public TopicOfInterest getTopicOfInterest(String topic, String uri) {
    	try {
    		topicStatements = readStatements(uri);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.\n" + e.getMessage());
			return new TopicOfInterest();
		}
    	return topicStatements.get(topic);
    }


	/**readStatements 
	 * @since
	 *
	 * @param uri
	 * @return
	 * @throws FileNotFoundException 
	 */
	private HashMap<String, TopicOfInterest> readStatements(String uri) throws FileNotFoundException {
		HashMap<String, TopicOfInterest> map = new HashMap<String, TopicOfInterest>();
		ArrayList<TopicOfInterest> items = tokenizeFile(uri);
		for (TopicOfInterest item : items) {
			map.put(item.topicNum, item);
		}
		return map;
	}


	/**tokenizeFile 
	 * @since
	 *
	 * @param uri
	 * @return
	 * @throws FileNotFoundException 
	 */
	private ArrayList<TopicOfInterest> tokenizeFile(String uri) throws FileNotFoundException {
		ArrayList<TopicOfInterest> items = new ArrayList<TopicOfInterest>(); 
		File file = new File(uri);
		Scanner scanner;
		TopicOfInterest toi = null;
		String content = "";
		boolean start = false;
		scanner = new Scanner(file);
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.indexOf("<top>") >= 0) {
				start = true;
				toi = new TopicOfInterest();
			}
			if (line.indexOf("</top>") >= 0) {
				String tokens[] = content.split("<[a-zA-Z]*>");
				toi.topicNum = tokens[2].split(":", 2)[1].trim();
				toi.title = tokens[3].trim();
				if (tokens[4].indexOf(":") >= 0) {
					toi.desc = tokens[4].split(":", 2)[1].trim();
				} else {
					toi.desc = tokens[4].trim();
				}
				toi.narr = tokens[5].split(":", 2)[1].trim();
				items.add(toi);
				content = "";
				start = false;
			}
			if (start) {
				content += line + " ";
			}
		}
		scanner.close();
		
		return items;
	}
}
