INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, reservation_id, status, total_price, sub_total_price, delivery_fees, savings_amount, currency)
    VALUES
        (100, 1, 11, 1, 777, 0, 1050, 900, 250, 100, 'XAF'),
        (101, 1, 11, 1, 777, 0, 1050, 900, 250, 100, 'XAF'),
        (102, 1, 11, 1, 777, 0, 1050, 900, 250, 100, 'XAF'),

        (200, 1, 11, 1, 777, 0, 1050, 900, 250, 100, 'XAF')
;

INSERT INTO T_ORDER_ITEM(order_fk, product_id, quantity, unit_price, unit_comparable_price, currency)
    VALUES
        (100, 11, 4, 100, null, 'XAF'),
        (100, 12, 2, 250, 300, 'XAF')
    ;

