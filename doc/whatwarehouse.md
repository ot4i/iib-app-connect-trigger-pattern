# Explanation of Warehouse sample
A detailed explanation of what the Warehouse sample is showing and how it was constructed.

## Introduction
The Warehouse sample show how the integration between a backend on-premises system and another system can be done using App Connect with the help of IIB.

Lets first take a look at the use case before diving into the detail.

## Setting the scene

There is a retail company that has an online store. The inventory for the componay is managed by commercial software to keep track of stock levels of each item that is sold through the online storefront. The IT department uses IBM Integration Bus to keep the inventory system in sync with other systems such as procurement, order fulfillment and shipping.

Now the marketing department wants to organize some campaigns around a few popular items. Because the items are in short supply, inventory has been consistently low. The department wants to engage their customers who have visited the store and searched for these items, but found that they were out of stock.

So the idea is to send instant notifications to these customers as soon as the inventory application has updated the stock for the items. Because App Connect at the moment only supports a limited number of applications, the Warehouse sample will use Google Sheets as the target action in the place of IBM Silverpop. The goal is to get the newly stocked items to show up in a Google Spreadsheet as a new row. This simulates the API call to IBM Silverpop to notify customers of the stock level update. The flow of data through the system when a new stock item occurs will be as follows:

1.	Inventory management application fires the stock increase event
2.	An integration flow in IBM Integration Bus (IIB) picks up this event and passes it along to IBM App Connect
	*	For this to happen, you must first use IBM App Connect to subscribe to this event with IIB
3.	An integration flow in IBM App Connect picks up the event and passes it along to the digital campaign application for customer engagement, such as Marketo or IBM Silverpop
4.	Customers get personalized notifications via digital channels like email, phones, SMS, etc.

![The chain of events when a new stock arrives](./warehouse_sample_workflow.png)


In addition, the creation of the integration flow in IBM Integration Bus is not the main focus of this lab, so that part has been pre-built. What this entails is that an integration flow in IIB has been enhanced to become a Webhook event source. Webhook is a popular pattern for cloud applications to support external event pub/sub. In a Webhook pattern, the event source publishes a URL for subscribers to register an HTTP POST callback. When the event happens, the event source, which in this scenario is the IIB integration flow for handling stock level updates in the inventory application, will push the event to the subscribers via the callback POST URL. 

The subscriber of the Webhook event in this scenario is the App Connect integration flow you will build next.

OK. Enough background. Let’s get started.

Defining the custom application for “Warehouse Stock”

Remember the trigger event comes from a Webhook call? This first step is to set up the Webhook between IIB and App Connect.

In App Connect, event sources are represented as “Applications”. So as you have seen from Part I, App Connect currently only supports Salesforce and Google Sheets. There’s no such thing as “Warehouse Stock” application to supply the event we are interested in. So step one is to define that in the system.

Open Google Drive in your browser (drive.google.com), and log in with the gmail account as specified on page 4. Download the file “lab-HIA-3826.zip”.

Unzip the file and open the folder “lab/windows/data/userXX” where “XX” is your assigned user number (a number from 01 to 40).

Let’s first review the files in this folder, using user “01” as example:

warehousedefinition01.yaml	
warehousedescription01.yaml

createWarehouseApp01.bat	
warehousepost01.txt

driveWarehouseApp01.bat		
purple.txt			

“warehousedefinition01.yaml” is the API description for the Webhook subscriber registration endpoint. It’s written in Swagger syntax (http://swagger.io) which is in JSON format. You can review the content in a text editor, or for a better presentation open the online Swagger editor (http://editor.swagger.io/) and paste the content in the left pane:

 

The key parts to the content are the POST URL for the subscriber registration and event data format.

Webhook subscriber registration:

POST /user01/warehouse/stock/hook

Event data format:

Product:
  type: object
  properties:
    id:
      type:  string
    name:
      type:  string
    description:
      type:  string
    color:
      type:  string
    size:
      type:  string

In the subsequent steps, you will upload the content of this swagger file to IBM App Connect, which will parse it and understand the registration URL it needs to call, as well as the event data format to expect for the integration flow.

The next file is “warehousedescription01.yaml”. This is a metadata file that will describe how the custom application will display in the IBM App Connect user interface. 

The ”createWarehouseApp01.bat” file is the command that calls the App Connect API to define the custom “Warehouse” application. It does this by posting the content of the two files above. The body of the POST call is saved in the “warehousePost01.txt” file. Open the file in a text editor.

The name of the custom application is at the very beginning of the file “warehousePost01.txt”:

description_yaml=name: Warehouse01%0Adescription: …

Feel free to change the name to a more personalized value. Note that the custom applications from all the lab attendees will display in the same application catalog, so make sure the name of the application is unique.

Let’s get to work: open a command line window, change directory to “lab/windows/data/userXX” (again “XX” is your assigned number). Fire the command:

createWarehouseAppXX.bat

This will result in the custom application to be defined in App Connect. To verify the result, go back to the App Connect user interface in the web browser and click “Applications” link on top of the page to open the application catalog. Verify that the named application is displayed:

 



Creating the flow in App Connect for the stock notification

Now that we have the Warehouse application, let’s create a new flow using that application as the trigger.

Note that the application will show up for selection as both trigger (source) and action (target). For this lab the application only works as a trigger.

Select the new application as the trigger and Google Sheet as the action.

In step 2, select the “newStock” event as the trigger and “Create Row” as the action:

 

In step 3, for the trigger you don’t need to select an account because the Webhook subscription has been created specifically for your account already. For the Google Sheet action, select the previously authorized account (appconnect.ibm@gmail.com), and select “InterConnect Lab” spreadsheet, “StockItem” worksheet:

 

Click “Auto map fields”:

 

Click “Continue”, give your new flow a name and click “Save”.

Back in the dashboard, activate the new flow.

Update inventory with a new stock item to execute the new flow

Now everything is in place, let’s give this new hybrid integration solution a whirl.

Back in the command line window, find the file “driveWarehouseApp01.bat” (with “01” replaced with your assigned number) and execute it. This script command simulates a COBOL application operation that updates the inventory system, which triggers the integration flow in IBM Integration Bus.

The flow in IIB then calls IBM App Connect via the Webhook callback, and passes along the new stock item information. The new flow created above will then capture the new item in a new row in Google Sheet:

 

Congratulations! You have successfully completely this lab.









## Reference
For IIB to interact with App Connect it needs to implement a Webhook pattern. There are example artefacts in `IIBtoAppConnectWebhookLib` that do this for you but you can constrict this anyway you like as long as it conforms to the following spec:

* [IIB webhook specification](./IIB_webhook_description.md)
