
public class WebhookSubscription {
	private String callbackUrl;
	private String secret;
	private long id;
	private String[] events;

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
