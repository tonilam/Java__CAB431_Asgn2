package lib.preprocessing;

/**
 * XmlStructure is a data structure to store the relevant information of a XML file.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017 
 */
public class XmlStructure {
	private String fileName;
	private int itemId;
	private String content;
	
	/**
	 * Constructor to initialize the attributes.
	 */
	public XmlStructure() {
		fileName = "";
		itemId = 0;
		content = "";
	}
	
	
	// *************** Basic getter/setter for this class ***************//
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemNo) {
		this.itemId = itemNo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	// End of getter/setter definition.

}
