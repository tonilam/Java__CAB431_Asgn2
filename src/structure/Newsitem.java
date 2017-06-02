/**
 * 
 */
package models.structure;

import java.util.Date;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 20, 2017
 */
public class Newsitem {
	public int itemid;
	public String id;
	public Date date;
	public String lang;
	
	/**
	 * Constructor to initialize attributes.
	 * @since 1.0
	 */
	public Newsitem() {
		itemid = -1;
		id = "";
		date = null;
		lang = "";
	}
}
