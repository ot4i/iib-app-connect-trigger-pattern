# How to setup and run the Warehouse sample
Steps required to setup and run the Warehouse sample in IIB and App Connect.

## Introduction
The Warehouse sample is very easy to setup and run. For a detailed explanation of the contents of the sample, see [What the Warehouse sample does](./doc/whatwarehouse.md).

To run the sample requires an IIB system and App Connect.
For IIB there are two possibilities:

* Using [IIB on Cloud](http://www.ibm.com/software/products/ibm-integration-bus-on-cloud)
* Using [IIB installed on-premises](http://www.ibm.com/software/products/en/ibm-integration-bus)

If you do not currently have access to either of these systems, click the links for information about how to get access. 

For the sample to work, App Connect must be able to make direct HTTP calls to IIB: 
* If you use the IIB on-premises option, the HTTP port in the IIB server must be accessible from the internet. You can install the IBM Secure Gateway from within App Connect to enable communication between your on-premises IIB deployment and App Connect.
* If you use the IIB on Cloud option, you must turn off basic authentication for your application.

[IBM App Connect](http://info.appconnect.ibmcloud.com/) only runs in the cloud, there is no on-premises option.

## Setting up IIB
1.  Deploy the BAR file [`Warehouse.bar`](../WarehouseNewStockEventToAppConnect/Warehouse.bar) that is provided in the `WarehouseNewStockEventToAppConnect` project to an IIB server.

IIB is now running and waiting for App Connect to register.

## Configuring the Webhook definition file
1.  Make a copy of the [Warehouse Webhook definition file](./warehousedefinition.yaml) file and open the file in either a text editor or, for a better presentation, the online [Swagger editor](http://editor.swagger.io/):
2.  Change the host and port to have the correct values for your IIB system. The HTTP port defaults to 7800 and you can change the protocol to HTTPS if you want to.

The Webhook definition file is ready to be used by App Connect

## Creating the App Connect 'Warehouse' application
1.  Log in to App Connect and upload your modified Warehouse Webhook definition file. You can call the application whatever you like (for example: 'Warehouse'). 

The 'Warehouse' application is ready to be used as a source application in an App Connect flow. 

## Creating an App Connect flow
1.	Create a new flow and select the 'Warehouse' application as the first application and Google Sheets as the second application. 
2.	Select `newStock` as the trigger, and `Create new row` as the action. 
3.	If you haven't already connected a Google Sheets account to App Connect, click the `Connect` button and provide your account details.
4.	Select the Google Sheets spreadsheet and worksheet that you want to use as your target. Note: The worksheet must have column names in the first row of the spreadsheet. 
5.	Map fields from the 'Warehouse' application to columns in the Google Sheets spreadsheet.
6.	Save your flow and turn it on.

The 'Warehouse' application in App Connect should now subscribe to IIB for any available `newStock` events. You can check this by doing a HTTP GET to the Webhook URL running in IIB. For example, type the following command at a command prompt:

`curl -X GET http://localhost:7800/user01/warehouse/stock/hook`

You should get a response similar to the following:

`[{"id":1,"callback":{"url":"https:\/\/webhook-connector-provider-conntest.mybluemix.net\/webhooks\/4c290d9e629b84a5b778e392314ef0f0\/a10953e0-fb1b-11e5-a1d3-8f07ad38ea36\/Events"},"event_types":["newStock"]}]`

If the response is `[]`, The 'warehouse' application has not subscribed to IIB successfully.

## Driving the integration end-to-end

1.  POST the event that is contained in [purple_jumper.txt](./purple_jumper.txt) to the IIB message flow at the path `/drive/newstock`. For example, type the following command at a command prompt: 

`curl -X POST http://localhost:7800/drive/newstock --data @purple_jumper.txt`

You should get the following response:

`{"id":"4444671","name":"woollen jumper","description":"woollen jumper","size":"12","color":"purple"}`

The IIB message flow processes the newStock request and sends it to anyone that is subscribed to the Webhook; in this case, the 'Warehouse' application in App Connect. The application receives the event and triggers the flow. The flow updates the Google Sheets spreadsheet with the fields from the 'Warehouse' application.


Now you have the Warehouse sample working why not try modifying it to interact with your own systems; see [How to change the Warehouse sample to integrate with your own backend system](./modwarehouse.md).
