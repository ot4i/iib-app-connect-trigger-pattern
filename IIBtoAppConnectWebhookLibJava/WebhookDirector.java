import java.util.HashMap;
import java.util.Map;

public class WebhookDirector {
	static WebhookDirector webhookDirector;
	private Map<String,WebhookManager> mapOfManagers = new HashMap<String,WebhookManager>();
	WebhookPersister persister;
	synchronized static public WebhookDirector getWebhookDirector(){
		if(webhookDirector == null){
			webhookDirector = new WebhookDirector(null);
		}
		return webhookDirector;
	}

	synchronized public WebhookManager getManagerByPath(String path){
		WebhookManager wm = mapOfManagers.get(path);
		if(wm == null){
			wm = new WebhookManager(persister,path);
			mapOfManagers.put(path, wm);
		}
		return wm;
	}
	synchronized public void deleteManagerByPath(String path){
		mapOfManagers.remove(path);
	}
	
	public WebhookDirector(WebhookPersister persister){
		this.persister = persister;
	}
}
