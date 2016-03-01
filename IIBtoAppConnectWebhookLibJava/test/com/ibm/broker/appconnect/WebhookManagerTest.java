package com.ibm.broker.appconnect;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.broker.appconnect.WebhookManager;
import com.ibm.broker.appconnect.WebhookSubscription;


public class WebhookManagerTest {
	WebhookManager wm;
	String[] events;
	@Before
	public void setUp() throws Exception {
		 wm = new WebhookManager("/test/hook");
         events = new String[]{"event1"};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void subscribeToWebHook_createANewSubscribeObject() {
		WebhookSubscription ws = wm.subscribeToWebHook("http://localhost:7777/callback", "mySecret",events);
		assertNotNull(ws);
		assertEquals("http://localhost:7777/callback", ws.getCallbackUrl());
		assertEquals("mySecret", ws.getSecret());
		assertEquals(1, ws.getId());
		assertArrayEquals(events, ws.getEvents());
	}

	@Test
	public void subscribeToWebHooktwice_createANewSubscribeObjectsWithDifferentIds() {
		WebhookSubscription ws1 = wm.subscribeToWebHook("http://localhost:7777/callback", "mySecret",events);
		WebhookSubscription ws2 = wm.subscribeToWebHook("http://localhost:7777/callback", "mySecret",events);
		assertNotSame(ws2.getId(), ws1.getId());
	}

	@Test
	public void deleteSubscription_subscriptionDeleted() {
		WebhookSubscription ws = wm.subscribeToWebHook("http://localhost:7777/callback", "mySecret",events);
		assertEquals(1,wm.numberOfSubscriptions());
		wm.deleteSubscription(ws.getId());
		assertEquals(0,wm.numberOfSubscriptions());
	}
	@Test
	public void callbackUrlsForEventWhenThereisOneUrlRegisteredForEvent_returnSetOfOneCallbackUrl() {
		WebhookSubscription ws = wm.subscribeToWebHook("http://localhost:7777/callback", "mySecret",events);
		assertNotNull(ws);
		assertEquals(1,wm.numberOfSubscriptions());
		assertEquals("http://localhost:7777/callback", wm.callbackUrlsForEvent("event1")[0]);
	}
	@Test
	public void callbackUrlsForEventWhenThereisTwoUrlsRegisteredForEvent_returnSetOfTwoCallbackUrl() {
		wm.subscribeToWebHook("http://localhost:7771/callback", "mySecret",events);
		wm.subscribeToWebHook("http://localhost:7772/callback", "mySecret",events);
		assertEquals(2,wm.numberOfSubscriptions());
		if(wm.callbackUrlsForEvent("event1")[0].equals("http://localhost:7772/callback")){
			assertEquals("http://localhost:7771/callback", wm.callbackUrlsForEvent("event1")[1]);
		}
		else{
			assertEquals("http://localhost:7771/callback", wm.callbackUrlsForEvent("event1")[0]);
			assertEquals("http://localhost:7772/callback", wm.callbackUrlsForEvent("event1")[1]);
		}
	}
	@Test
	public void callbackUrlsForEventWhenThereisNoUrlsRegisteredForEvent_returnSetOfNoCallbackUrl() {
		assertEquals(0,wm.callbackUrlsForEvent("event2").length);
		wm.subscribeToWebHook("http://localhost:7771/callback", "mySecret",events);
		wm.subscribeToWebHook("http://localhost:7772/callback", "mySecret",events);
		assertEquals(2,wm.numberOfSubscriptions());
		assertEquals(0,wm.callbackUrlsForEvent("event2").length);
	}
	@Test
	public void deleteAllSubscriptions_clearsOutAllSubscriptions() {
		wm.subscribeToWebHook("http://localhost:7771/callback", "mySecret",events);
		wm.subscribeToWebHook("http://localhost:7772/callback", "mySecret",events);
		assertEquals(2,wm.numberOfSubscriptions());
		wm.deleteAllSubscriptions();
		assertEquals(0,wm.numberOfSubscriptions());
	}
	@Test
	public void getSubscriptionWhenExists_returnSubscription() {
		WebhookSubscription ws = wm.subscribeToWebHook("http://localhost:7771/callback", "mySecret",events);
		assertNotNull(wm.getSubscription(ws.getId()));
	}
	@Test
	public void getSubscriptionNotExists_returnNull() {
		wm.subscribeToWebHook("http://localhost:7771/callback", "mySecret",events);
		assertNull(wm.getSubscription(10101010)); 
	}

	@Test
	public void changeSubscriptionNotExists_returnFalse() {
		wm.subscribeToWebHook("http://localhost:7771/callback", "mySecret",events);
		assertFalse(wm.changeSubscription(10100101,"new url","sshhh",new String[]{"test2"}));
	}
	@Test
	public void changeSubscriptionExists_subscriptionChanged() {
		WebhookSubscription ws = wm.subscribeToWebHook("http://localhost:7771/callback", "mySecret",events);
		assertTrue(wm.changeSubscription(ws.getId(),"new url","sshhh",new String[]{"test2"}));
		WebhookSubscription wsAfterChange = wm.getSubscription(ws.getId());
		assertEquals("new url",wsAfterChange.getCallbackUrl());
		assertEquals("sshhh",wsAfterChange.getSecret());
	}

}
