/**
 * 
 */
package UnitTest;

import static org.junit.Assert.*;

import org.junit.Test;

import factories.DocumentFactory;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 20, 2017
 */
public class DocumentFactoryTest {

		@Test
		public void documentShouldBeIntialized() {
			DocumentFactory document = new DocumentFactory();
			assertNotNull(document);
		}
		
		@Test
		public void setDocumentShouldNotHaveDocIfUriDoesNotExist() {
			DocumentFactory document = new DocumentFactory();
			DocumentFactory counterDocument = new DocumentFactory();
			document.setDocument("/src/resources/xxx.xml");
			assertEquals("Doc Id changed.",
						 counterDocument.getDocId(), document.getDocId());
			assertEquals("Title changed.",
						 counterDocument.getTitle(), document.getTitle());
			assertEquals("Text changed.",
						 counterDocument.getText(), document.getText());
		}
		
		@Test
		public void setDocumentShouldReadTheFile() {
			DocumentFactory document = new DocumentFactory();
			DocumentFactory counterDocument = new DocumentFactory();
			document.setDocument(".//src//resources//dataset101-150//Training101//18586.xml");
			assertNotEquals("Doc Id not changed.",
							counterDocument.getDocId(), document.getDocId());
			assertNotEquals("Title not changed.",
							counterDocument.getTitle(), document.getTitle());
			assertNotEquals("Text not changed.",
							counterDocument.getText(), document.getText());
		}
		
		@Test
		public void setDocumentShouldReadTheFile_Training101_18586() {
			DocumentFactory document = new DocumentFactory();
			document.setDocument(".//src//resources//dataset101-150//Training101//18586.xml");
			assertEquals("Invalid Doc Id.",
						 18586, document.getDocId());
			assertEquals("Invalid Title.",
						 "GERMANY: FOCUS-VW unveils new Passat, says sales well up.", document.getTitle());
			String abstractText = document.getText().substring(0, 52);
			assertEquals("Invalid Text.",
						 "Germany's Volkswagen AG , unveiling a new Passat car", abstractText);
		}
}
