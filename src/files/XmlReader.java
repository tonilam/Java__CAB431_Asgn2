package lib.files;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * XmlReader is used to read a target xml file and return the content in XmlStructure.
 * 
 * @author Toni Lam
 * @since 1.0
 * @version 2.0, Apr 22, 2017
 */
public class XmlReader {
	private static final String FILENAME_SUFFIX = "newsML.xml";

	private String datasetDirectory;
	private String fileNo;
	private XmlStructure xml;
	
	/**
	 * Constructor to initialize all attributes.
	 * 
	 * @param dir is the directory path of the data source.
	 * @since 1.0
	 */
	public XmlReader(String dir) {
		datasetDirectory = dir;
		fileNo = "";
		xml = new XmlStructure();
	}
	
	/**
	 * Setter for the attribute fileNo.
	 * 
	 * @param fileNo indicate the file id of the XML file. It can not be null.
	 * @since 1.0
	 */
	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}

	/**
	 * It reads the XML file and store the relevant information into a XmlStructure
	 * object.
	 * 
	 * @return XmlStruture object containing information of the XML file.
	 * @since 1.0
	 */
	public XmlStructure ReadXml() {
		XmlHandler handler = new XmlHandler(fileNo + FILENAME_SUFFIX);
		xml = handler.getXmlStructure();
		
		try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();         
            saxParser.parse(datasetDirectory + xml.getFileName(), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return xml;
	}

}
