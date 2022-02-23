# Event Handling

# Event Consumed

| Event                                             | Source          | Description                                                                                           |
|---------------------------------------------------|-----------------|-------------------------------------------------------------------------------------------------------|
| `urn:wutsi:event:payment:transaction-successful`  | wutsi-payment   | This event notify that a payment is received. this module will update the payment status or the Order |

# Event Emmitted

| Event                               | Destination | Description                                                                                   |
|-------------------------------------|-------------|-----------------------------------------------------------------------------------------------|
| `urn:wutsi:event:order:order-ready` | wutsi-order | This event is fired after reception of payment event, if the amount of payment >= order price |
