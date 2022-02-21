INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, status, total_price, currency)
    VALUES (1, 1, 11, 1, 0, 900, 'XAF');

INSERT INTO T_ORDER_ITEM(order_fk, product_id, quantity, unit_price, currency)
    VALUES
        (1, 11, 4, 100, 'XAF'),
        (1, 12, 2, 250, 'XAF')
    ;

