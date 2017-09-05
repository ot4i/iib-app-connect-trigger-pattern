# iib-app-connect-trigger-pattern

This repo contains an example IBM Integration Bus solution showing how to push events from IBM Integration Bus (IIB) into IBM App Connect. The IIB solution behaves as a custom application in App Connect. The App Connect flow can be triggered based on events in that Application.

## Introduction
Here are examples of projects to use in [IBM Integration Bus](http://www.ibm.com/software/products/en/ibm-integration-bus) to integrate with [IBM App Connect](http://www-03.ibm.com/software/products/sv/ibm-app-connect).

This example uses a fictional Warehouse application which is connected to IIB, and where IIB does detailed conversion of events emitted by the Warehouse app into a form which can be consumed in App Connect, so that simple triggers can be created in App Connect without requiring deep integration skill each time.

This demonstrates how IIB and App Connect can be setup and configured to work together to allow all the sophisticated integration function of IIB to be used within the simple user experience of App Connect.

The following IIB toolkit projects are provided as part of this repo:

* `IIBtoAppConnectWebhookLib` - Sample IIB library that contains subflows that can be used to integrate with IBM App Connect.
* `IIBtoAppConnectWebhookLib_unittest` - Unit tests for the sample IIB webhook subflows.
* `IIBtoAppConnectWebhookLibJava` - Sample java project that is used by the subflows in the `IIBtoAppConnectWebhookLib` library.
* `IIBtoAppConnectWebhookLibTest` - Simple example HTTP driven flows that show the `IIBtoAppConnectWebhookLib` library in use.
* `WarehouseNewStockEventToAppConnect` - IIB Application showing a full example of how to use the other projects to integrate between a fictional Warehouse system and App Connect.

The following topics explain how to use these projects to run the Warehouse sample and then to construct your own integrations:

* [What the Warehouse sample does](./doc/whatwarehouse.md).
* [How to setup and run the Warehouse sample](./doc/runwarehouse.md).
* [How to change the Warehouse sample to use your own end system to integrate with](./doc/modwarehouse.md).



## Requirements
Install and configure  [IBM Integration Bus](http://www.ibm.com/software/products/en/ibm-integration-bus)
 or use [IIB on Cloud](http://www.ibm.com/software/products/ibm-integration-bus-on-cloud).

The IIB projects provided have been authored using IIB 10 and will work with any version of IIB from v10 onwards.

An online account with [IBM App Connect](http://www-03.ibm.com/software/products/en/ibm-app-connect). A free version is available.



## License and Authors
Copyright 2016 IBM Corp. under the [Eclipse Public license](http://www.eclipse.org/legal/epl-v10.html).

* Author:: John Reeve <jreeve@uk.ibm.com>
* Author:: Mark Frost <frostmar@uk.ibm.com>
* Author:: Karen Brent <Karen_Brent@uk.ibm.com>
