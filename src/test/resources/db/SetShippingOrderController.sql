INSERT INTO T_ADDRESS(id, account_id, tenant_id, city_id, country) VALUES(100, 1, 1, 1000, 'FR');

INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, reservation_id, status, total_price, sub_total_price, delivery_fees, savings_amount, currency, shipping_address_fk)
    VALUES
        (100, 1, 11, 1, 777, 0, 800, 900, 0, 100, 'XAF', 100),
        (130, 1, 11, 1, 777, 4, 800, 900, 0, 100, 'XAF', null),
        (140, 1, 11, 1, 777, 3, 800, 900, 0, 100, 'XAF', null)
;


INSERT INTO T_ORDER_ITEM(order_fk, product_id, quantity, unit_price, unit_comparable_price, currency)
    VALUES
        (100, 11, 4, 100, null, 'XAF'),
        (100, 12, 2, 250, 300, 'XAF')
    ;
