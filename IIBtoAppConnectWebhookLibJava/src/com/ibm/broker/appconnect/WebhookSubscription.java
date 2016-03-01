package com.ibm.broker.appconnect;

/**
 * Data object storing the details of a single subscription
 */
public class WebhookSubscription {
	private String callbackUrl;
	private String secret;
	private long id;
	private String[] events;

	/**
	 * Constructor
	 * @param callbackUrl - HTTP URL called
	 * @param secret - secret sent in each callback
	 * @param events - array of named events for this subscription
	 * @param id - unique subscription ID
	 */
	public WebhookSubscription(String callbackUrl, String secret, String[] events, long id){
        this.callbackUrl = callbackUrl;
		this.secret      = secret;
		this.id = id;
		this.events = events;
	}
	
	public String getCallbackUrl() {
		return callbackUrl;
	}

	public String getSecret() {
		return secret;
	}
	
	public long getId() {
		return id;
	}
	
	public String[] getEvents() {
		return events;
	}
}
