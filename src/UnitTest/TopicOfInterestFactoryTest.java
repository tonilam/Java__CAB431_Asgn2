/**
 * 
 */
package UnitTest;

import static org.junit.Assert.*;

import org.junit.Test;

import factories.TopicOfInterestFactory;

/**
 * @author Toni Lam
 *
 * @since 1.0
 * @version 1.0, May 20, 2017
 */
public class TopicOfInterestFactoryTest {

		@Test
		public void topicOfInterestShouldBeIntialized() {
			TopicOfInterestFactory toi = new TopicOfInterestFactory();
			assertNotNull(toi);
		}
		
		@Test
		public void setTopicOfInterestShouldNotHaveValuesIfUriDoesNotExist() {
			TopicOfInterestFactory toi = new TopicOfInterestFactory();
			TopicOfInterestFactory counterToi = new TopicOfInterestFactory();
			toi.setTopic("R101");
			toi.setTopics(".//src//xxx.txt");
			assertEquals("Topic Id changed.",
						 counterToi.getTopicId(), toi.getTopicId());
			assertEquals("Title Id changed.",
					 counterToi.getTitle(), toi.getTitle());
			assertEquals("Desc Id changed.",
					 counterToi.getDesc(), toi.getDesc());
			assertEquals("Narr Id changed.",
					 counterToi.getNarr(), toi.getNarr());
		}
		
		@Test
		public void setTopicOfInterestShouldReadTheFile() {
			TopicOfInterestFactory toi = new TopicOfInterestFactory();
			TopicOfInterestFactory counterToi = new TopicOfInterestFactory();
			toi.setTopic("R101");
			toi.setTopics(".//src//resources//TopicStatements101-150.txt");
			assertNotEquals("Topic Id not changed.",
					 counterToi.getTopicId(), toi.getTopicId());
			assertNotEquals("Title not changed.",
					 counterToi.getTitle(), toi.getTitle());
			assertNotEquals("Desc not changed.",
					 counterToi.getDesc(), toi.getDesc());
			assertNotEquals("Narr not changed.",
					 counterToi.getNarr(), toi.getNarr());
		}
		
		@Test
		public void setTopicOfInterestShouldReadTheFile_TopicStatements() {
			TopicOfInterestFactory toi = new TopicOfInterestFactory();
			toi.setTopic("R101");
			toi.setTopics(".//src//resources//TopicStatements101-150.txt");
			assertEquals("Invalid Doc Id.",
						 "R101", toi.getTopicId());
			assertEquals("Invalid Title.",
						 "Economic espionage", toi.getTitle());
			assertEquals("Invalid Description.",
						 "What is being done to counter economic espionage internationally?", toi.getDesc());
			String abstractNarr = toi.getNarr().substring(0, 49);
			assertEquals("Invalid Narrative.",
						 "Documents which identify economic espionage cases", abstractNarr);
		}
}
