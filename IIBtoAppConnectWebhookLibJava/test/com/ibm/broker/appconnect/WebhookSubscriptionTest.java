package com.ibm.broker.appconnect;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class WebhookSubscriptionTest {

	@Test
	public void newWebHooksubscription_createWebhokSubscriptionWithCorrectProperties() {
		WebhookSubscription ws = new WebhookSubscription("http://localhost:7800/Test", "******",null, 1);
		assertEquals(1,ws.getId());
		assertEquals("http://localhost:7800/Test",ws.getCallbackUrl());
		assertEquals( "******",ws.getSecret());
	}

}
