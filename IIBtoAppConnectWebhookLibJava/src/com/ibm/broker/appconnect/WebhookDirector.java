package com.ibm.broker.appconnect;
import java.util.HashMap;
import java.util.Map;

/**
 * WebhookDirector class owns one or more WebhookManagers
 * and can get them by their path
 */
public class WebhookDirector {
	static WebhookDirector webhookDirector;
	private Map<String,WebhookManager> mapOfManagers = new HashMap<String,WebhookManager>();
	synchronized static public WebhookDirector getWebhookDirector(){
		if(webhookDirector == null){
			webhookDirector = new WebhookDirector();
		}
		return webhookDirector;
	}

	/**
	 * Return a WebhookManager by it's unique path
	 * @param path
	 * @return
	 */
	synchronized public WebhookManager getManagerByPath(String path){
		WebhookManager wm = mapOfManagers.get(path);
		if(wm == null){
			wm = new WebhookManager(path);
			mapOfManagers.put(path, wm);
		}
		return wm;
	}
	synchronized public void deleteManagerByPath(String path){
		mapOfManagers.remove(path);
	}
	
}
