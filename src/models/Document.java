/**
 * 
 */
package models;

import models.structure.Newsitem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 7, 2017
 */
public class Document {
	public String filename;
	public Newsitem attributes;
	public String title;
	public String headline;
	public String byline;
	public String dateline;
	public String text;
	public String copyright;
	public HashMap<String, ArrayList<?>> metadata;

	// post-process attributes
	public int wordCount;
	public Map<String, Integer> docTermFreq;
	
	/**
	 * Constructor to initialize attributes.
	 * @since 1.0
	 */
	public Document() {
		filename = "";
		attributes = new Newsitem();
		title = "";
		headline = "";
		byline = "";
		dateline = "";
		text = "";
		copyright = "";
		metadata = null;
		wordCount = 0;
		docTermFreq = new HashMap<String, Integer>();
	}
}
