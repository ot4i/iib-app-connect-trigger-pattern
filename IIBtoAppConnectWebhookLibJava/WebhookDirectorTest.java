import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WebhookDirectorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void GetWebhookDirector_returnAnInstanceOfWebhookDirector() {
		assertNotNull(WebhookDirector.getWebhookDirector());
	}
	@Test
	public void GetWebhookDirector_returnAlwaysTheSameWebhookDirector() {
		assertEquals(WebhookDirector.getWebhookDirector() , WebhookDirector.getWebhookDirector());
	}
	@Test
	public void GetManagerByPath_returnNonNullManager() {
		WebhookDirector wd = new WebhookDirector(null);
		assertNotNull(wd.getManagerByPath("/Test"));
	}
	@Test
	public void GetManagerByPath_returnSameManager() {
		WebhookDirector wd = new WebhookDirector(null);
		assertEquals(wd.getManagerByPath("/Test"),wd.getManagerByPath("/Test"));
	}
	@Test
	public void GetManagerByPathDifferentPaths_returnDifferentManager() {
		WebhookDirector wd = new WebhookDirector(null);
		assertNotSame(wd.getManagerByPath("/Test1"),wd.getManagerByPath("/Test2"));
	}

}
