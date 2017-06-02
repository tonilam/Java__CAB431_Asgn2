/**
 * 
 */
package models;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 7, 2017
 */
public class RelevanceJudgment {
	public String topicId;
	public int docId;
	public boolean relevance;
	public double degree;
	
	/**
	 * Constructor to initialize attributes.
	 * @since 1.0
	 */
	public RelevanceJudgment() {
		topicId = "";
		docId = -1;
		relevance = false;
		degree = -1;
	}
}
