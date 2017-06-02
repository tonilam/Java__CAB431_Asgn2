package lib.preprocessing;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * StoppingWords is used for defining a list of stopping words and identify if any given word is listed
 * in the stopping words.
 * 
 * @author Toni Lam
 * @since 1.1
 * @version 2.0, Apr 22 2017
 */
public class StoppingWords {
	private String stopwordfile;
	private boolean enabled;
	private ArrayList<String> stopwords;
	private int validLength;
	
	/**
	 * Constructor to initialize the attributes.
	 * @since 1.1
	 * 
	 * @param uri is the file that stored the stop words.
	 */
	public StoppingWords(String uri) {		
		stopwordfile = uri;
		stopwords = new ArrayList<String>();
		enabled = false;
		validLength = 0;
		loadStopWord();
	}
	
	/**
	 * Switch the stopping words mechanism to "enabled".
	 * @since 1.1
	 */
	public void enable() {
		this.enabled = true;
	}
	
	/**
	 * Switch the stopping words mechanism to "disabled".
	 * @since 1.1
	 */
	public void disable() {
		this.enabled = false;
	}
	
	/**
	 * Set the valid length of a word, the class will ignore those words that shorter than this length.
	 * @since 1.1
	 * 
	 * @param length is a number represents the length of valid words.
	 */
	public void setValidLength(int length) {
		this.validLength = length;
	}
	
	/**
	 * It loads the stopping words from a specific file.
	 * @since 1.1
	 */
	private void loadStopWord() {
		Scanner in;
		try {
			in = new Scanner(new FileReader(stopwordfile));
	        in = in.useDelimiter(",");
	        while (in.hasNext()) {
	        	stopwords.add(in.next());
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	/**
	 * It checks if the given word is listed in stopping words.
	 * @since 1.1
	 * 
	 * @param thisWord is the word inspecting.
	 * @return true if it is a stop word and is not in valid word length;
	 * 		   otherwise, return false.
	 */
	public boolean isStopWord(String thisWord) {
		return (
					(
						this.enabled &&
						stopwords.contains(thisWord)
					)
					||
					(
						thisWord.length() < validLength
					)
				);
	}

}
