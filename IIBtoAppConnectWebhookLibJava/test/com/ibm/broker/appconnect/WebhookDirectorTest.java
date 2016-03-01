package com.ibm.broker.appconnect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;


public class WebhookDirectorTest {

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
		WebhookDirector wd = new WebhookDirector();
		assertNotNull(wd.getManagerByPath("/Test"));
	}
	@Test
	public void GetManagerByPath_returnSameManager() {
		WebhookDirector wd = new WebhookDirector();
		assertEquals(wd.getManagerByPath("/Test"),wd.getManagerByPath("/Test"));
	}
	@Test
	public void GetManagerByPathDifferentPaths_returnDifferentManager() {
		WebhookDirector wd = new WebhookDirector();
		assertNotSame(wd.getManagerByPath("/Test1"),wd.getManagerByPath("/Test2"));
	}

}
