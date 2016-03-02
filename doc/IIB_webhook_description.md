
# Introduction

Webhooks is a simple notification pattern that works over HTTP. There is no clear standard for exactly how to implement the mechanism. At it’s simplest it allows a user to define a HTTP callback for a given hook. Whenever an event is produced for the given hook then the HTTP callback provided is called with details of the event. Many users can register for callback on the same hook. In most implementations, as well as registering for the hook itself, it is also possible to specify the set of events that the user wants to be notified about. 

For IIB  to interact with App Connect it will need to be setup to implement a Webhook pattern as described in this document. 

This can be be done using the standard HTTP nodes provided in IIB, and an example of how to do this is given in the same repo that contains this document.

# IIB REST API for providing a Webhook pattern

The IIB API for setting up a callback will be based on a REST model where the REST actions are made against a Webhook URL that is being served by IIB. There will be as many of these Webhook points as a message flow developer (Igor) wants to define. For example, they might choose to have a webhook for `/warehouse/stock/hook` and another for `/crm/customer/hook`. 

I will use the term subscribe to mean the act of registering a callback and subscription for the object created by the subscribe action. GihHub call it a hook but that is a little confusing as for me the hook is the thing you attach to. The REST operation will then work as follows:

<table>
  <tr>
    <th>REST operation</th>
    <th>Webhook path</th>
    <th>Description</th>
  </tr>
  <tr>
    <td><tt><b>POST</b></tt></td>
    <td>{IIB root}/{hookpath}/</td>
    <td>Create a subscription</td>
  </tr>
  <tr>
    <td><tt><b>GET</b></tt></td>
    <td>{IIB root}/{hookpath}/</td>
    <td>List subscriptions</td>
  </tr>
  <tr>
    <td><tt><b>GET</b></tt></td>
    <td>{IIB root}/{hookpath}/{id}/</td>
    <td>Get a subscription</td>
  </tr>
  <tr>
    <td><tt><b>PUT</b></tt></td>
    <td>{IIB root}/{hookpath}/{id}/</td>
    <td>Update a subscription</td>
  </tr>
  <tr>
    <td><tt><b>DELETE</b></tt></td>
    <td>{IIB root}/{hookpath}/{id}/</td>
    <td>Dekete a subscription</td>
  </tr>
  
</table>

The webhook or subscription will not appear directly in the API so there is nothing stopping the IIB developer (Igor) adding one of the words to the path. For example they might choose: /warehouse/stock/hook or /warehouse/stock/subscribe.  

When the HTTP callback is called to deliver an event a POST action is done. The content of the POST body can be any data the user wants to define for an event. It does not even have to be JSON or an XML document. The event_type is added into the http header as a custom property: X-EventType.
## CREATE A SUBSCRIPTION
<table>
  <tr>
    <td><tt><b>POST</b></tt></td>
    <td>{IIB root}/{hookpath}/</td>
    <td>Create a subscription</td>
  </tr>
</table>

By POSTing to the URL for the webhook a subscription is created with the details of the callback required and the type of events that should be sent.  An id is returned from the post to uniquely identify the subscription and can then be used in further REST calls to display or change the subscription.
### Parameters
<table>
  <tr>
    <th>Name</th>
    <th>Type</th>
    <th>Required</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>callback</td>
    <td>object</td>
    <td>yes</td>
    <td>Structure to provide details of the HTTP callback</td>
  </tr>
  <tr>
    <td>Event_types</td>
    <td>array</td>
    <td>no</td>
    <td>Determines what events are to be subscribed for callback. If the parameter is missing then all events will be sent. An empty array will mean no events will be sent.</td>
  </tr>
</table>

The callback object has the following fields:
<table>
  <tr>
    <th>Field</th>
    <th>Type</th>
    <th>Required</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>url</td>
    <td>string</td>
    <td>yes</td>
    <td>URL to callback on when an event is published. </td>
  </tr>
  <tr>
    <td>secret</td>
    <td>string</td>
    <td>no</td>
    <td>Secret to exchange to prove identity of event sender (not implemented yet in sample).</td>
  </tr>
</table>


The mechanism used by GitHub is to HMAC hex digest of the body, using the secret as the key and then adding the value to the HTTP header. Something like a HTTP field X-Hook-Signature.

### Response

The response will return a 201 code if the POST is successful and an object with a single id field. The id is the name of the subscription made and can be used in other REST calls to retrieve, update or delete the subscription.

Return code 404 if webhook path does not exist.

##DELETE A SUBSCRIPTION##
<table>
  <tr>
    <td><tt><b>DELETE</b></tt></td>
    <td>{IIB root}/{hookpath}/{id}</td>
    <td>Delete a subscription</td>
  </tr>
</table>

Deletes the subscription defined by the path. This stops any callbacks being made to the registered HTTP callback.

### Response

Return code 204 is return if successful.

Return code 404 if id is not a valid subscription.


##LIST SUBSCRIPTIONS##

<table>
  <tr>
    <td><tt><b>GET</b></tt></td>
    <td>{IIB root}/{hookpath}/{id}</td>
    <td>List subscriptions</td>
  </tr>
</table>

Lists all the current subscriptions. Returns an array of subscription objects currently subscribed.

### **Response**

Returns an array of the following structure:
<table>
  <tr>
    <th>Field</th>
    <th>Type</th>
    <th>Required</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>id</td>
    <td>number</td>
    <td>id for the subscription. </td>
  </tr>
  <tr>
    <td>url</td>
    <td>string</td>
    <td>URL to callback on when an event is published. </td>
  </tr>
  <tr>
    <td>secret</td>
    <td>string</td>
    <td>Secret to exchange to prove identity of event sender (not implemented yet in sample).</td>
  </tr>
</table>


Return code 200 is returned if successful with any array of all ids.

Return code 404 if id is not a valid subscription.

##GET SUBSCRIPTION##

<table>
  <tr>
    <td><tt><b>GET</b></tt></td>
    <td>{IIB root}/{hookpath}/{id}</td>
    <td>Get a subscription</td>
  </tr>
</table>

Retrieves details of a particular subscription.

### **Response**

Returns the following properties with return code 200.
<table>
  <tr>
    <th>Field</th>
    <th>Type</th>
    <th>Required</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>id</td>
    <td>number</td>
    <td>id for the subscription. </td>
  </tr>
  <tr>
    <td>url</td>
    <td>string</td>
    <td>URL to callback on when an event is published. </td>
  </tr>
  <tr>
    <td>secret</td>
    <td>string</td>
    <td>Secret to exchange to prove identity of event sender (not implemented yet in sample).</td>
  </tr>
</table>



Return code 404 if id is not a valid subscription.


## **Update subscription**

<table>
  <tr>
    <td><tt><b>PUT</b></tt></td>
    <td>{IIB root}/{hookpath}/{id}</td>
    <td>Update a subscription</td>
  </tr>
</table>


Allows the subscription to be updated. Uses exactly the same data structure as POST. Note: any id field in the message payload will be ignored and the id in the path used.

### **Response**

Return code 204 if successfully and returns no data.

Note: could also provide a PATCH operation for partial updates (like adding a new event) 

Return code 404 if id is not a valid subscription.

