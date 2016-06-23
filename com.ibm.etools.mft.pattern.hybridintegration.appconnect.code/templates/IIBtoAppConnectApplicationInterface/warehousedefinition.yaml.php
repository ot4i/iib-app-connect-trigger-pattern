<?php 
	$pim = $_MB["PATTERN_INSTANCE_MANAGER"]; 
?>
swagger: '2.0'
info:
  title: <?php echo $_MB['PP']['ppInterfaceTitle'] . "\n";?>
  description: <?php echo $_MB['PP']['ppInterfaceDescription'] . "\n";?>
  version: 1.0.0
host: localhost:7800
schemes:
  - http
basePath: /
produces:
  - application/json

x-hooks:
  <?php echo $_MB['PP']['ppInterfaceTitle'] .":\n"; ?>
    description: Events originating from the <?php echo $_MB['PP']['ppInterfaceTitle'] . " application\n"; ?>
    x-register-subscription:   warehouseSubscribeOperation
    x-list-all-subscriptions:  warehouseSubscriptionListOperation
    x-deregister-subscription: warehouseUnsubscribeOperation
    x-change-subscription:     warehouseChangeSubscribeOperation
    x-retrieve-subscription:   warehouseRetrieveSubscribeOperation
    x-events:
      <?php echo $_MB['PP']['ppTriggerName'].":\n"; ?>
        description: '<?php echo $_MB['PP']['ppTriggerDescription'] . "'\n"; ?>
        x-eventId: <?php echo "'" . $_MB['PP']['ppTriggerName']."'\n"; ?>
        parameters:
            - name: event
              in: body
              required: true
              schema:
                $ref: '#/definitions/Product'

paths:
  /user01/warehouse/stock/hook:
    post:
      operationId: 'warehouseSubscribeOperation'
      summary: Create a subscription to webhook for warehouse stock
      description: |
        Call this API to create a new subscription on the webhook. Provide a list of events to subscribe for and a HTTP callback URL.
      parameters:
        - name: subscription
          in: body
          description: Details to subscribe for a callback on a given set of events
          required: true
          schema:
            $ref: '#/definitions/Subscription'

      tags:
        - Webhook
      responses:
        '202':
          description: Details of the subscription created to the webhook
          schema:
            $ref: '#/definitions/Subscribe_response'

    get:
      operationId: 'warehouseSubscriptionListOperation'
      summary: Get a list of subscriptions on webhook for warehouse stock
      description: |
        Call this API to get a list of subscriptions defined for this webhook
      tags:
        - Webhook
      responses:
        '200':
          description: Ids of all subscriptions setup for this webhook
          schema:
            type: array
            items:
              type: string

  /user01/warehouse/stock/hook/{id}:
    delete:
      operationId: 'warehouseUnsubscribeOperation'
      summary: Delete a subscription to the webhook for warehouse stock
      description: |
        Call this API to delete a subscription call id on the webhook.
      parameters:
        - name: id
          in: path
          description: Subscription to delete
          required: true
          type: number
      tags:
        - Webhook
      responses:
        '204':
          description: Details of the subscription created to the webhook

    get:
      operationId: 'warehouseRetrieveSubscribeOperation'
      summary: Get details of a subscription to webhook
      description: |
        Call this API to get details of a subscription for warehouse stock.
      parameters:
        - name: id
          in: path
          description: Subscription to get
          required: true
          type: number
      tags:
        - Webhook
      responses:
        '200':
          description: Details of the subscription created to the webhook
          schema:
            $ref: '#/definitions/Subscription'

    put:
      operationId: 'warehouseChangeSubscribeOperation'
      summary: Change details of a subscription to webhook for warehouse stock
      description: |
        Call this API to change details of a subscription.
      parameters:
        - name: id
          in: path
          description: Subscription to change
          required: true
          type: string
        - name: subscription
          in: body
          description: New subscription details
          required: true
          schema:
            $ref: '#/definitions/Subscription'
      tags:
        - Webhook
      responses:
        '204':
          description: Change successful

definitions:
  Subscription:
    type: object
    properties:
      callback:
        $ref: '#/definitions/Callback_details'
        description: 'Details about how to callback when an event occurs'
      event_types:
        type: array
        items:
          type: string

  Callback_details:
    type: object
    properties:
      url:
        type: string
        description: 'url to callback on'
      secret:
        type: string
        description: 'secret to use in callback'

  Subscribe_response:
    type: object
    properties:
      id:
        type: number
        description: 'Unique identifier representing the subscription to the webhook'

  Product:
    type: object
    properties:
<?php 
	$table = $pim->getParameterTable("ppTriggerFields");
	$count = $table->getRowCount();
	for ($j=0;$j<$count ;$j++ ) {
		echo "      ".$_MB['PP']['ppTriggerFields'][$j]['fieldName'].":\n";
		echo "        type:  string\n";
	} 
?>
