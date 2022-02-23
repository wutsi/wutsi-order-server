# Event Handling

# Event Consumed

| Event                                             | Source          | Description                                                                          |
|---------------------------------------------------|-----------------|--------------------------------------------------------------------------------------|
| `urn:wutsi:event:payment:transaction-successful`  | wutsi-payment   | This event will update the payment status or the Order or `PAID` or `PARTIALLY_PAID` |

# Event Emmitted

| Event                               | Destination | Description                                                           |
|-------------------------------------|-------------|-----------------------------------------------------------------------|
| `urn:wutsi:event:order:order-ready` | wutsi-order | This event is fired to notify that the order is ready to be processed |
