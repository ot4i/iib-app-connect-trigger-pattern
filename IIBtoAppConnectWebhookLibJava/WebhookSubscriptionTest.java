import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WebhookSubscriptionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void newWebHooksubscription_createWebhokSubscriptionWithCorrectProperties() {
		WebhookSubscription ws = new WebhookSubscription("http://localhost:7800/Test", "******",null, 1);
		assertEquals(1,ws.getId());
		assertEquals("http://localhost:7800/Test",ws.getCallbackUrl());
		assertEquals( "******",ws.getSecret());
	}

}
