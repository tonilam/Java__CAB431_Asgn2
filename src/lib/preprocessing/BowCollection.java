package lib.preprocessing;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import config.AppConfig;

/**
 * BowCollection is used to manage the bag of wards collection.
 * @author Toni Lam
 *
 * @since 1.0
 * @version 2.0, Apr 22, 2017
 */
public class BowCollection extends LinkedHashMap<Integer,BowDocument> {
	/**
	 * Implementation of Serializable needs a serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	private Stemmer snowballStemmer;
	private boolean stemmingEnabled;
	private int totalDocLength;

	/**
	 * Constructor.
	 * @since 1.0
	 * 
	 * @param dir is the folder where the data set is being stored.
	 * @param stopwordEnabled indicates whether stop words will be used. 
	 * @param stemmingEnabled indicates whether stemming will be used.
	 */
	public BowCollection(String dir, boolean stopwordEnabled, boolean stemmingEnabled) {
		super();
		
		// initialize variables
		totalDocLength = 0;

		// Create one new stemmer instance for the whole collection use.
		snowballStemmer = new Stemmer(stemmingEnabled);
		this.stemmingEnabled = stemmingEnabled;
		
		// Load every document into the collection by giving the path of the data set.
		loadCollectionByFolderPath(dir);
		
		// Retrieve document information for later use.
		parseCollection(dir, stopwordEnabled);
	}
	
	/**
	 * Overloading constructor, in case the status of using stop words is not given by
	 * the calling method.
	 * @since 2.0
	 * @see BowCollection#BowCollection
	 * 
	 * @param dir is the folder where the data set is being stored.
	 */
	public BowCollection(String dir) {
		this(dir, true, true);		
	}

	/**
	 * Accessor of the attribute stemmingEnabled which indicates whether this collection
	 * uses stemming. 
	 * @since 2.0
	 *
	 * @return the boolean value of stemmingEnabled.
	 */
	public boolean isStemmingEnabled() {
		return stemmingEnabled;
	}
	
	/**
	 * It loads the files that stored the related documents into the bag of words collection. 
	 * @since 1.0
	 *
	 * @param dir is the directory where the data set is stored.
	 */
	private void loadCollectionByFolderPath(String dir) {
		File folder = new File(dir);
		File[] datalist = folder.listFiles();
		
		/* search for all files that contains an integer id and treat it as
		 * the input data set.
		 */
		for (File file: datalist) {
			if (file.isFile()) {
				String fileid = file.getName().replaceAll("\\D", "");
				if (fileid.length() > 0) {
					int fileno = Integer.parseInt(fileid);
					this.put(fileno, new BowDocument(Integer.toString(fileno)));
				}
			}
		}
	}
	
	/**
	 * It reads all the file in the data set and stores the related information into
	 * the BowDocument. 
	 * @since 1.0
	 *
	 * @param stopwordEnabled indicates whether stop word mechanism will be used in
	 * parsing data set.
	 */
	public void parseCollection(String dir, boolean stopwordEnabled) {
		StoppingWords sw;
		XmlReader fileReader;
		XmlStructure fileStructure;
		
		sw = new StoppingWords(AppConfig.DEFAULT_STOPWORD_FILE);
		if (stopwordEnabled) {
			sw.enable();
		}
		sw.setValidLength(AppConfig.VALID_WORD_LENGTH);

		for(BowDocument thisDoc : this.values()) {
			fileReader = new XmlReader(dir);
			fileReader.setFileNo(thisDoc.getFileNo());
			fileStructure = fileReader.ReadXml();
			thisDoc.setitemId(fileStructure.getItemId());
			String content = fileStructure.getContent();
			
			content = content.replaceAll("[\\W]", " "); // break words that contain non-word characters.
			
			StringTokenizer st = new StringTokenizer(content);
	        while (st.hasMoreTokens()) {
	            String scanner = st.nextToken();
	            if (scanner != null) {
	            	scanner = scanner.toLowerCase();
		            if (scanner.length() > 0 && scanner.matches("^[a-zA-Z]*$")) {
		            	scanner = snowballStemmer.stemming(scanner);
		            	thisDoc.addWordCount();
		            	if (!sw.isStopWord(scanner)) {
		            		thisDoc.addTerm(scanner);
		            	}
		            }
	            }
	        }
	        accumulateDocLength(thisDoc.getWordCount());
		}
	}
	
	/**
	 * It adds the given value to the existing document length.
	 * @since 1.0
	 *
	 * @param addupValue is the amount that add to the current document length.
	 */
	private void accumulateDocLength(int addupValue) {
		totalDocLength += addupValue;
	}
	
	/**
	 * It calculates the average document length. 
	 * @since 1.0
	 *
	 * @return the rounded up average document length.
	 */
	public int getAverageDocLength() {
		return totalDocLength / this.size();
	}
}
