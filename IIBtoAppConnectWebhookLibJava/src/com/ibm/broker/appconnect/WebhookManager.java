package com.ibm.broker.appconnect;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * WebhookManager class manages the subscriptions for a single webhook path
 */
public class WebhookManager {
	String path; 
	long nextSubId = 1;
	private Map<Long,WebhookSubscription> mapOfSubscriptions = new HashMap<Long,WebhookSubscription>();
	
	/**
	 * Constructor
	 * @param path
	 */
	public WebhookManager(String path){
		this.path = path;
	}

	/**
	 * Add a subscription
	 * @param url - callback HTTP URL
	 * @param secret - secret to sent in callback events
	 * @param events - array of events to subscribe for
	 * @return object representing subscription
	 */
	synchronized public WebhookSubscription subscribeToWebHook(String url, String secret, String[] events){
		WebhookSubscription ws = new WebhookSubscription(url, secret,events,nextSubId++);
		mapOfSubscriptions.put(ws.getId(),ws);
		return ws;
	}

	/**
	 * Delete a subscription
	 * @param id - subscription ID
	 * @return true if deleted
	 */
	synchronized public boolean deleteSubscription(long id){
		boolean returnResult = true;
		WebhookSubscription ws = mapOfSubscriptions.remove(id);
		returnResult = (ws != null);
		return returnResult;
	}
	
	/**
	 * Return a subscription
	 * @param id - subscription ID
	 * @return subscription object
	 */
	synchronized public WebhookSubscription getSubscription(long id){
		WebhookSubscription ws = mapOfSubscriptions.get(id);
		return ws;
	}
	
	/**
	 * Update an existing subscription
	 * @param id - subscription ID
	 * @param url - new callback HTTP URL
	 * @param secret - new secret sent in callback events
	 * @param events - new array of events to subscribe for
	 * @return
	 */
	synchronized public boolean changeSubscription(long id, String url, String secret, String[] events){
		boolean returnResult;
		WebhookSubscription ws = mapOfSubscriptions.remove(id);
		if(ws == null){
			returnResult = false;
		}
		else{
			WebhookSubscription wsChange = new WebhookSubscription(url, secret,events,id);
			mapOfSubscriptions.put(id, wsChange);
			returnResult = true;
		}
		return returnResult;
	}
	
	/**
	 * @return number of existing subscriptions
	 */
	public int numberOfSubscriptions(){
		return mapOfSubscriptions.size();
	}

	/**
	 * @param event
	 * @return array of subscribed callback HTTP URLs for the given event
	 */
	synchronized public String[] callbackUrlsForEvent(String event){
		// simple event to url look up mechanism we could be a lot clever about this but no need to optimize for now
		Set<String> callbackurls = new HashSet<String>();
		Iterator<WebhookSubscription> iter = mapOfSubscriptions.values().iterator();
		while(iter.hasNext()){
			WebhookSubscription ws = iter.next();
			String[] events = ws.getEvents();
			if(events.length == 0){
				callbackurls.add(ws.getCallbackUrl());
			}
			else{
			for(int i= 0; i< events.length;i++){
				if(events[i].equals(event)){
					callbackurls.add(ws.getCallbackUrl());
					break;
				}
			}
			}
		}
		String template[] = new String[callbackurls.size()];
		return (String[]) callbackurls.toArray(template);
	}

	/**
	 * @return array of all existing subscriptions
	 */
	synchronized public WebhookSubscription[] listAllSubscriptions() {
		WebhookSubscription[] temp = new WebhookSubscription[0];
		return mapOfSubscriptions.values().toArray(temp);	
	}
	
	/**
	 * Delete all subscriptions
	 */
	synchronized public void deleteAllSubscriptions() {
		nextSubId = 1;
		mapOfSubscriptions = new HashMap<Long,WebhookSubscription>();
	}
}
