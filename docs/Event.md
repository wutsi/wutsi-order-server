# Event Handling

## Event Consumed

| Event                                             | Source          | Description                                                                          |
|---------------------------------------------------|-----------------|--------------------------------------------------------------------------------------|
| `urn:wutsi:event:payment:transaction-successful`  | wutsi-payment   | This event will update the payment status or the Order or `PAID` or `PARTIALLY_PAID` |

## Event Emitted

| Event                                   | Description                                                                                 |
|-----------------------------------------|---------------------------------------------------------------------------------------------|
| `urn:wutsi:event:order:order-opened`    | This event is fired to notify that the order is ready to be processed by the merchant       |
| `urn:wutsi:event:order:order-cancelled` | This event is fired to notify that the order has been cancelled                             |
| `urn:wutsi:event:order:order-dona`      | This event is fired to notify that the order has has been completed, and ready for shipping |
