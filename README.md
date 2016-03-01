# iib-to-appconnect-sample
Example IIB project showing integration with IBM App Connect

## Introduction
Here are examples of projects to use in [IBM Integration Bus](http://www.ibm.com/software/products/en/ibm-integration-bus) to integrate with [IBM App Connect](http://info.appconnect.ibmcloud.com/).
This integration allows systems that IIB can integrate with to be exposed in IBM App Connect using its simple flow editor.


Three IIB toolkit projects are provided as part of the repo:

* IIBtoAppConnectWebhookLib - Sample IIB library that contains subflows that can be used to integrate with IBM App Connect.
* IIBtoAppConnectWebhookLibJava - Sample java project that is used by the subflows in the IIBtoAppConnectWebhookLib library.
* IIBtoAppConnectWebhookLibTest - Simple example HTTP driven flows that show the IIBtoAppConnectWebhookLib library in use.



## Requirements
Install and configure [IBM Integration Bus](http://www.ibm.com/software/products/us/en/integration-bus/).

The IIB projects provided have been authored using IIB 10 and will work with any version of IIB from v10 onwards.



##License and Authors
Copyright 2016 IBM Corp. under the [Eclipse Public license](http://www.eclipse.org/legal/epl-v10.html).

* Author:: John Reeve <jreeve@uk.ibm.com>
* Author:: Mark Frost <frostmar@uk.ibm.com>
