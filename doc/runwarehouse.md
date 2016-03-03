# How to setup and run the Warehouse sample
An end-to-end run through of setting up and running the Warehouse sample in IIB and App Connect.

## Introduction
The Warehouse sample is very easy to setup and run. It shows all the parts in action to allow App Connect to integrate with the vast number of end points IIB can cope with.

To run the sample requires both a running IIB system and a running App Connect system.
For IIB there are two possibilities:

* Using [IIB on Cloud](http://www.ibm.com/software/products/ibm-integration-bus-on-cloud)
* Using [IIB installed on premise](http://www.ibm.com/software/products/en/ibm-integration-bus)

If you do not currently have either of these then follow the links given to set up a free system. 

For the sample to work App Connect must be able to make direct HTTP calls to IIB. If using the on-premises option it will require the HTTP port in the IIB server to be accessible from the internet.

[IBM App Connect](http://info.appconnect.ibmcloud.com/) only runs in the cloud as there is no on-premises option.

## Setting up IIB
Deploy the bar file provided in the `WarehouseNewStockEventToAppConnect` project called [`Warehouse.bar`](../WarehouseNewStockEventToAppConnect/Warehouse.bar) to an IIB server.
Modify the [Warehouse Webhook definition file](./warehousedefinition01.yaml) file using either a text editor, or for a better presentation open the online [Swagger editor](http://editor.swagger.io/). Change the host and port to have the correct values for your IIB system. The HTTP port is usually 7800 and you can chang ethe protocol to HTTPs if you want to.
This is all that is required to have the IIB part running and waiting for App Connect to register.

## Creating a new Warehouse App in App Connect
Take the modified Warehouse Webhook definition file and use it in App Connect to make a new App. You can call the App what ever you like (for example: Warehouse).

## Create and start a new flow to use App in App Connect
Now everything is ready in App Connect to create a flow receive the event from IIB and map it to another App. Create a flow in the App Connect UI selecting the new Warehouse App as the trigger and any other App as the action to be done. Google sheets is the most straight forward if you do not have any real system to send the data to.

Once created, turn the flow on.

App Connect should now subscribe with IIB for any available `newStock` events. You can check this by doing a HTTP get to the Webhook URL running in IIB. For example:

`curl -X GET http://localhost:7800/warehouse/stock/hook`.

## Driving the integration end-to-end

To drive the integration you need to POST an event contained in [purple.txt](./purple.txt) to the IIB message flow at the path /drive/newstock. For example: 

`curl -X POST http://localhost:7800/drive/newstock purple.txt`.

The IIB message flow will then trigger process the newStock request and send it to any one subscribed to the Webhook. In this case that will be App Connect. App connect will recieve the event and trigger it's flow. The trigger data will be mapped to the going going action and then executed. For example: updating a Google sheet.


Now you have the Warehouse sample working why not try modifying it to interact with your own real systems: [How to change the Warehouse sample to use your own end system to integrate with](./modwarehouse.md).
