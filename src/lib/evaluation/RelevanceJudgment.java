package lib.evaluation;

/**
 * RelevanceJudgment stored the relevance judgment record from a file.
 * The file contain information in this format:
 * topic | documentID | Relevance judgment
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 23, 2017
 */
public class RelevanceJudgment {
	private String topic;
	private int docId;
	Integer relevanceJudgment;

	/**
	 * Constructor.
	 * @param topic indicates which topic is these data sets.
	 */
	public RelevanceJudgment(String topic) {
		this.topic = topic;
		docId = 0;
		relevanceJudgment = 0;
	}

	/**
	 * Accessor of the attribute topic. 
	 * @since 1.0
	 *
	 * @return the string value of topic.
	 */
	public String getTopic() {
		return topic;
	}
	
	/**
	 * Accessor of the attribute docId. 
	 * @since 1.0
	 *
	 * @return the integer value of docId.
	 */
	public int getDocId() {
		return docId;
	}

	/**
	 * Mutator of the attribute docId.
	 * @since 1.0
	 *
	 * @param docId is the document id of this relevance document.
	 */
	public void setDocId(int docId) {
		this.docId = docId;
	}

	/**
	 * Accessor of the attribute relevanceJudgment.
	 * @since 1.0
	 *
	 * @return the integer value of relevanceJudgment.
	 */
	public int getRelevanceJudgment() {
		return relevanceJudgment;
	}

	/**
	 * Mutator of the attribute relevanceJudgment. 
	 * @since 1.0
	 *
	 * @param relevanceJudgment is the relevance judgment of this document.<br>
	 * 		  0 = not relevant, 1 = relevant
	 */
	public void setRelevanceJudgment(Integer relevanceJudgment) {
		this.relevanceJudgment = relevanceJudgment;
	}
	
	/**
	 * It interprets the given string and store the relevant information. 
	 * @since 1.0
	 *
	 * @param text is the string that stored the relevant information.
	 * @throws NumberFormatException when the token cannot be parsed.
	 * @throws RelevanceJudgmentException when the data provided is not usable.
	 */
	public void setJudgmentByString(String text)
			throws NumberFormatException, RelevanceJudgmentException {
		final String DELIMITER = "\t";
		String[] recordTokens;
		double ranking;
		int docId,
		    relevance;
		if (text.matches("^[0-9]+.*")) {
			// splits the given text into tokens, and stores the information into class
			// attributes.
			recordTokens = text.split(DELIMITER);
			docId = Integer.parseInt(recordTokens[0]);
			ranking = Double.parseDouble(recordTokens[2]);
			if (recordTokens[1].matches("[+-]")) {
				relevance = (recordTokens[1].equals("+"))? 1 : 0;
			} else {
				throw new RelevanceJudgmentException("Relevance Judgment not recognized.");
			}

			setDocId(docId);
			setRelevanceJudgment(relevance);

		}
	}
	
	/**
	 * It interprets the given string and store the relevant information. 
	 * @since 1.0
	 *
	 * @param text is the string that stored the relevant information.
	 * @throws NumberFormatException when the token cannot be parsed.
	 * @throws RelevanceJudgmentException when the data provided is not usable.
	 */
	public void setJudgmentByString2(String text)
			throws NumberFormatException, RelevanceJudgmentException {
		final String DELIMITER = " ";
		String[] recordTokens;
		String topic;
		int docId,
		    relevance;
		
		// splits the given text into tokens, and stores the information into class
		// attributes.
		recordTokens = text.split(DELIMITER);
		topic = recordTokens[0];
		docId = Integer.parseInt(recordTokens[1]);
		if (recordTokens[2].matches("[01]")) {
			relevance = Integer.parseInt(recordTokens[2]);
		} else {
			throw new RelevanceJudgmentException("Relevance Judgment not recognized.");
		}
		
		// validate the topic before storing the data.
		if (topic.equals(this.topic)) {
			setDocId(docId);
			setRelevanceJudgment(relevance);
		} else {
			throw new RelevanceJudgmentException("Topic not match.");
		}
	}
	
	/**
	 * A predicate to return true/false value of the relevanceJudgment.
	 * @since 1.0
	 *
	 * @return true if the value of relevanceJudgment = 1,<br>
	 * 		   false if the value is not equal to 1, i.e. 0
	 */
	public boolean isRelevant() {
		return relevanceJudgment == 1;
	}
	
	/**
	 * It works as same as the isRelevant() function. But some instances are
	 * retrieved documents instead of relevant documents, so it is more clear
	 * to call the same algorithm in different name.
	 * @since 1.0
	 *
	 * @return true if the value of relevanceJudgment = 1,<br>
	 * 		   false if the value is not equal to 1, i.e. 0
	 */
	public boolean isRetrieved() {
		return isRelevant();
	}
}
