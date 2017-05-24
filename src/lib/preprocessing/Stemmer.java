package lib.preprocessing;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

/**
 * Stemmer is the engine used for stemming functions.
 * @author Toni Lam
 *
 * @since 1.0
 * @version 2.0, Apr 24, 2017
 */
public class Stemmer extends englishStemmer {
	
	private SnowballStemmer stemmer;
	private boolean stemmingEnabled;

	/**
	 * Constructor.
	 * @param stemmingEnabled indicates whether stemming is used.
	 */
	public Stemmer(boolean stemmingEnabled) {
		super();
		this.stemmer = new englishStemmer();
		this.stemmingEnabled = stemmingEnabled;
	}
	
	/**
	 * It uses the Snowball algorithm to stem the given word when stemming enabled.
	 * 
	 * @param word a String for stemming
	 * @return a stemmed form of given word
	 */
	public String stemming(String word) {
		if (stemmingEnabled) {
			stemmer.setCurrent(word);
			stemmer.stem();
			return stemmer.getCurrent();
		}
		return word;
	}

}
