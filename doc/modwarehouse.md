# How to modify the Warehouse sample to perform your own integration
Steps to take to modify the Warehouse sample to create your own integration from IIB To App Connect using a real back end system with in your company.

## Introduction
It is very easy to construct your own flow the follows the same lines as the [Warehouse sample](./whatwarehouse.md). If worth taking a quick look at how that sample works before creating your own integrations.

## Creating a new IIB integration to send events to App Connect
Take a look at the Warehouse sample flow that maps from a Warehouse event to App Connect:
![Warehouse IIB Message flow](./warehouse_sample_messageflow.png) 
The important part of this flow for sending events to App Connect are the mapping node and the webhook subflow that follows it. The mapping node converts the incoming data structure to a simple JSOn doc App Connect can understand and the subflow then does all the function required to implement a webhook pattern.
To create a new message flow that receives your own events and sends them to App Connect you first need to construct the first part of the flow that using nodes in IIB like MQ, SAP, File and so to receive events. Then add in a mapping node that maps to simple JSON structure and then add in the webhook subflow to set up the webhook point.
For example, if I wanted to take an event from MQ and send it to App Connect then I would construct a flow like the following:

![Warehouse IIB Message flow](./custom_messageflow.png) 

Then I would need to map the structure coming from MQ to a simple JSON structure. The JSON structure in the map must have a root element called `JSON` followed by a child called `Data` and the another child called `eventData`. There then needs to be one field beneith eventData for each field to send to App Connect. For the warehouse sample there were five fields: id, name, description, size and color. the mapp for this looks like:
![Warehouse IIB Message flow](./warehouse_sample_map.png) 
The left half of the map is from the incoming data from MQ and the right is the JSON doc to send to App Connect. Your map will contain what ever data you want on the right and left as long as the JSON doc on the left conforms to the basic structure mentioned above.

The webhook subflow needs to directly follow the map and contains three properties you need to set:

* EventType - the name of the event that will be sent to App Connect. Use a name that is meaningful for your event like `newCustomer` or `changeAddress`.
* WebhookBaseUrl - the URL that is used to register for events from this webhook. It is NOT the url that is actually called to send the event but instead the URL to POST the callback URL to. The path can be anything you like but ideally would reflect the source of events with /hook added to the end but it is up to you what the exact path is and it does not have to end in /hook. Some examples: /warehouse/stock/hook, /customer/address/hook or /customer.
* WebhookStarUrl - must be identical to WebhookBaseUrl except preceded by a *. This is only required due to a limitation in how properties are promoted from a sub flow.

 Now the flow is constructed, it can be deployed to IIB ready to be used by App Connect. It does not at this point need to know any App Connect details. 

  
## Creating a new Webhook definition file for the IIB integration


## Create new App in App Connect
Take the modified Webhook definition file and use it in App Connect to make a new App. You can call the App what ever you like (for example: Warehouse, CustomerAddress or MyCRMSystem).


## Using the new App in App Connect flows
Now everything is ready in App Connect to create a flow receive the event from IIB and map it to another App. Create a flow in the App Connect UI selecting the new App as the trigger and any other App as the action to be done. Google sheets is the most straight forward if you do not have any real system to send the data to.

Once created, turn the flow on.

App Connect should now subscribe with IIB for any available events from your new integration. Drive you IIB flow to test it now all works end-to-end.