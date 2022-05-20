INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, reservation_id, status, total_price, sub_total_price, delivery_fees, savings_amount, currency)
    VALUES
        (100, 1, 11, 1, 777, 0, 1050, 900, 250, 100, 'XAF'),

        (201, 1, 11, 1, 777, 1, 1050, 900, 250, 100, 'XAF'),
        (202, 1, 11, 1, 777, 2, 1050, 900, 250, 100, 'XAF'),
        (203, 1, 11, 1, 777, 3, 1050, 900, 250, 100, 'XAF'),
        (204, 1, 11, 1, 777, 4, 1050, 900, 250, 100, 'XAF')
;

INSERT INTO T_ORDER_ITEM(id, order_fk, product_id, quantity, unit_price, unit_comparable_price, currency)
    VALUES
        (1001, 100, 11, 4, 100, null, 'XAF'),
        (1002, 100, 12, 2, 250, 300, 'XAF')
    ;

