package com.ibm.broker.appconnect;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;

/**
 * Java compute node class for sending an event to each subscribed callbacks.
 * Used by the 'Lookup HTTP callback URL' JCN.
 */
public class LookupHTTPCallback extends
		MbJavaComputeNode {

	/**
	 * Standard entry point for an IIB Java Compute node
	 * 
	 * Processes a message assembly (containing JSON object)
	 * propagates once for each subscribed HTTP callback, setting localEnvironment properties as needed.
	 * @param inAssembly - input message assembly containing JSON object
	 */
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;

		WebhookManager wm = WebhookDirector.getWebhookDirector().getManagerByPath((String)this.getUserDefinedAttribute("webhookUrl"));
		String eventType = (String)this.getUserDefinedAttribute("eventType");
		String[] callbacks = wm.callbackUrlsForEvent(eventType);
		// create new message as a copy of the input		
		for(int i = 0 ; i < callbacks.length ; i++){
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);	
			MbElement dest = outAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("Destination");
			if(dest == null){
				dest = outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME,"Destination",null);
			}
			MbElement http = dest.getFirstElementByPath("HTTP");
			if(http == null){ 
				http = dest.createElementAsLastChild(MbElement.TYPE_NAME,"HTTP",null);
			}
			MbElement requestURL = http.getFirstElementByPath("RequestURL");
			if(requestURL != null){
				requestURL.detach(); 
			}
			http.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"RequestURL",callbacks[i]);
			MbElement rootElement = outMessage.getRootElement();
			MbElement blob = rootElement.createElementAsLastChild("JSON");
			blob.copyElementTree(inMessage.getRootElement().getLastChild());
			MbElement httpRequest = rootElement.createElementAsFirstChild("WSREQHDR");
			httpRequest.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"Content-Type","application/json");
			out.propagate(outAssembly);
		}	
		
	}

}
