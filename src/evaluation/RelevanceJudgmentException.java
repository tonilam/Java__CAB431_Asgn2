package lib.evaluation;

/**
 * RelevanceJudgmentException completely inherits java.lang.Exception without modification.
 * It aims to throw the exception in an apt name.
 * @author Toni Lam
 *
 * @since 1.0
 * @version 2.0, Apr 24, 2017
 */
public class RelevanceJudgmentException extends Exception {

	/**
	 * Implementation of Serializable needs a serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	public RelevanceJudgmentException() {
		// TODO Auto-generated constructor stub
	}

	public RelevanceJudgmentException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RelevanceJudgmentException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public RelevanceJudgmentException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RelevanceJudgmentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
