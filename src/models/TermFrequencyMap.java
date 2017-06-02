/**
 * 
 */
package models;

import java.util.HashMap;

/**
 * @author Toni Lam
 *
 * @since
 * @version ?, May 31, 2017
 */
public class TermFrequencyMap extends HashMap<String, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TermFrequencyMap() {
		super();
	}
	
	public void accumulate(String key, int step) {
		if (this.containsKey(key)) {
			this.replace(key, this.get(key)+step);
		} else {
			this.put(key, step);
		}
	}
	
	public void accumulate(String key) {
		this.accumulate(key, 1);
	}
}
