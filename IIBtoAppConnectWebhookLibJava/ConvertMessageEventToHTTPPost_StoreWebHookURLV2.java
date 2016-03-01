import java.util.HashSet;
import java.util.Set;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class ConvertMessageEventToHTTPPost_StoreWebHookURLV2 extends
		MbJavaComputeNode {

	private void addReplyCode(MbMessage localenvironment, int code) throws MbException{
		MbElement dest = localenvironment.getRootElement().getFirstElementByPath("/Destination");
		MbElement http = dest.getFirstElementByPath("HTTP");
		http.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"ReplyStatusCode",code);
	}
	private void addSubscriptionToTree(MbElement item, WebhookSubscription ws) throws MbException{
		item.createElementAsLastChild(MbElement.TYPE_NAME,"id",ws.getId());
		MbElement callback = item.createElementAsLastChild(MbElement.TYPE_NAME,"callback",null);
		callback.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"url",ws.getCallbackUrl());
		MbElement events = item.createElementAsLastChild(MbJSON.ARRAY,"event_types",null);
		String[] event_types = ws.getEvents();
		for(int j = 0; j< event_types.length;j++){
			events.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,MbJSON.ARRAY_ITEM_NAME,event_types[j]);
		}
	}
	
	private void createASubscription(String path,MbMessageAssembly inputMessageAssembly,MbMessageAssembly outputMessageAssembly) throws MbException{
		MbElement callbackUrlField = inputMessageAssembly.getMessage().getRootElement().getFirstElementByPath("/JSON/Data/callback/url");
		if(callbackUrlField == null){
			MbElement dest = outputMessageAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Destination");
			MbElement http = dest.getFirstElementByPath("HTTP");
			http.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"ReplyStatusCode",400);
		}
		else{
			String callbackUrl = callbackUrlField.getValueAsString();
			MbElement secretField = inputMessageAssembly.getMessage().getRootElement().getFirstElementByPath("/JSON/Data/callback/secret");
			String secret = null;
			if(secretField != null){
				secret = secretField.getValueAsString();
			}
			MbElement eventTypes = inputMessageAssembly.getMessage().getRootElement().getFirstElementByPath("/JSON/Data/event_types");
			Set<String> eventSet = new HashSet<String>();
			if(eventTypes != null){
				MbElement currentChild = eventTypes.getFirstChild();
				while(currentChild != null){
					eventSet.add(currentChild.getValueAsString());
					currentChild = currentChild.getNextSibling();
				}
			}
			String[] events = new String[0]; 
			events = eventSet.toArray(events);
			WebhookSubscription sub = WebhookDirector.getWebhookDirector().getManagerByPath(path).subscribeToWebHook(callbackUrl, secret, events);
			MbElement json = outputMessageAssembly.getMessage().getRootElement().createElementAsFirstChild("JSON");
			MbElement data = json.createElementAsFirstChild(MbElement.TYPE_NAME,"Data",null);
			data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"id",sub.getId());
			addReplyCode(outputMessageAssembly.getLocalEnvironment(),201);
		}
	}
	
	private void changeASubscription(String path,long id, MbMessageAssembly inputMessageAssembly,MbMessageAssembly outputMessageAssembly) throws MbException{
		MbElement callbackUrlField = inputMessageAssembly.getMessage().getRootElement().getFirstElementByPath("/JSON/Data/callback/url");
		if(callbackUrlField == null){
			MbElement dest = outputMessageAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Destination");
			MbElement http = dest.getFirstElementByPath("HTTP");
			http.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"ReplyStatusCode",400);
		}
		else{
			String callbackUrl = callbackUrlField.getValueAsString();
			MbElement secretField = inputMessageAssembly.getMessage().getRootElement().getFirstElementByPath("/JSON/Data/callback/secret");
			String secret = null;
			if(secretField != null){
				secret = secretField.getValueAsString();
			}
			MbElement eventTypes = inputMessageAssembly.getMessage().getRootElement().getFirstElementByPath("/JSON/Data/event_types");
			Set<String> eventSet = new HashSet<String>();
			if(eventTypes != null){
				MbElement currentChild = eventTypes.getFirstChild();
				while(currentChild != null){
					eventSet.add(currentChild.getValueAsString());
					currentChild = currentChild.getNextSibling();
				}
			}
			String[] events = new String[0]; 
			events = eventSet.toArray(events);
			WebhookSubscription sub = WebhookDirector.getWebhookDirector().getManagerByPath(path).subscribeToWebHook(callbackUrl, secret, events);
			MbElement json = outputMessageAssembly.getMessage().getRootElement().createElementAsFirstChild("JSON");
			MbElement data = json.createElementAsFirstChild(MbElement.TYPE_NAME,"Data",null);
			data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"id",sub.getId());
	        boolean updated = WebhookDirector.getWebhookDirector().getManagerByPath(path).changeSubscription(id, callbackUrl, secret, events );
	        if(updated == true){
		      addReplyCode(outputMessageAssembly.getLocalEnvironment(),204);
	        }
	        else{
			  addReplyCode(outputMessageAssembly.getLocalEnvironment(),404);
	        }
		}
	}	
	private void getASubscription(String path,long id, MbMessageAssembly inputMessageAssembly,MbMessageAssembly outputMessageAssembly) throws MbException{
		 WebhookSubscription ws = WebhookDirector.getWebhookDirector().getManagerByPath(path).getSubscription(id);
		  if(ws != null){
			  MbElement json = outputMessageAssembly.getMessage().getRootElement().createElementAsFirstChild("JSON");
			  MbElement data = json.createElementAsFirstChild(MbElement.TYPE_NAME,"Data",null);
			  addSubscriptionToTree(data,ws);
			  addReplyCode(outputMessageAssembly.getLocalEnvironment(),200);
		  }
		  else{
				addReplyCode(outputMessageAssembly.getLocalEnvironment(),404);
		  }
	}
	private void deleteASubscription(String path,long id, MbMessageAssembly inputMessageAssembly,MbMessageAssembly outputMessageAssembly) throws MbException{
		WebhookManager wm = WebhookDirector.getWebhookDirector().getManagerByPath(path);
		if(wm.deleteSubscription(id) == true){
			addReplyCode(outputMessageAssembly.getLocalEnvironment(),204);
		}
		else{
			addReplyCode(outputMessageAssembly.getLocalEnvironment(),404);
		}
	}
	private void listAllSubscriptions(String path,MbMessageAssembly inputMessageAssembly,MbMessageAssembly outputMessageAssembly) throws MbException{
		WebhookSubscription[] subs = WebhookDirector.getWebhookDirector().getManagerByPath(path).listAllSubscriptions();
		MbElement json = outputMessageAssembly.getMessage().getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME);
		MbElement data = json.createElementAsLastChild(MbJSON.ARRAY,"Data",null);
		for(int i = 0; i< subs.length;i++){
			MbElement item = data.createElementAsLastChild(MbElement.TYPE_NAME,MbJSON.ARRAY_ITEM_NAME,null);
			addSubscriptionToTree(item,subs[i]);
		}
		addReplyCode(outputMessageAssembly.getLocalEnvironment(),200);
	}
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out"); 

		MbMessage outMessage =  new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);
		try {
			String command = inAssembly.getMessage().getRootElement().getFirstElementByPath("/HTTPInputHeader/X-Original-HTTP-Command").getValueAsString();
			String path    = command.substring(command.indexOf(" ")+1,command.lastIndexOf(" "));
			path = path.substring(2+path.indexOf("//"));
			path = path.substring(path.indexOf("/"));
			String webhookPath = (String)this.getUserDefinedAttribute("webhookUrl");
			if(path.equals(webhookPath)){
				if(command.startsWith("POST")){ 
					createASubscription(path,inAssembly,outAssembly);
				}
				else if(command.startsWith("GET")){
					listAllSubscriptions(path,inAssembly,outAssembly);
				}
				else if(command.startsWith("DELETE")){
					WebhookDirector.getWebhookDirector().deleteManagerByPath(path);
					addReplyCode(outAssembly.getLocalEnvironment(),204);
				}
			}
			else { 
				int lastSlash = path.lastIndexOf("/");
				String subPath = path.substring(0,lastSlash);
				String id = path.substring(lastSlash+1);
				if(subPath.equals(webhookPath)){
					if(command.startsWith("DELETE")){
						deleteASubscription(subPath,Long.parseLong(id),inAssembly,outAssembly);
					}
					else if(command.startsWith("GET")){;
						getASubscription(subPath,Long.parseLong(id),inAssembly,outAssembly);
					}
					else if(command.startsWith("PUT")){
						changeASubscription(subPath,Long.parseLong(id),inAssembly,outAssembly);
					}
				}
			}
			// ----------------------------------------------------------
			// Add user code below
			
			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
