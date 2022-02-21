INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, status, total_price, currency)
    VALUES (100, 1, 11, 1, 0, 900, 'XAF');

INSERT INTO T_ORDER_ITEM(order_fk, product_id, quantity, unit_price, currency)
    VALUES
        (100, 11, 4, 100, 'XAF'),
        (100, 12, 2, 250, 'XAF')
    ;

