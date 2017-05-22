/**
 * 
 */
package models;

import models.structure.Newsitem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 7, 2017
 */
public class Document {
	public Newsitem attributes;
	public String title;
	public String headline;
	public String byline;
	public String dateline;
	public String text;
	public String copyright;
	public HashMap<String, ArrayList<?>> metadata;
	
	/**
	 * Constructor to initialize attributes.
	 * @since 1.0
	 */
	public Document() {
		attributes = new Newsitem();
		title = "";
		headline = "";
		byline = "";
		dateline = "";
		text = "";
		copyright = "";
		metadata = null;
	}
}
