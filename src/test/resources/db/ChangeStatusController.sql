INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, status, total_price, currency, reservation_id)
    VALUES
        (100, 1, 11, 1, 0, 900, 'XAF', null),
        (101, 1, 11, 1, 1, 900, 'XAF', 1001),
        (102, 1, 11, 1, 2, 900, 'XAF', 1002),
        (103, 1, 11, 1, 3, 900, 'XAF', 1003),
        (104, 1, 11, 1, 4, 900, 'XAF', 1004),
        (105, 1, 11, 1, 5, 900, 'XAF', 1005),
        (106, 1, 11, 1, 6, 900, 'XAF', 1006),
        (107, 1, 11, 1, 7, 900, 'XAF', 1007)
;

INSERT INTO T_ORDER_ITEM(id, order_fk, product_id, quantity, unit_price, unit_comparable_price, currency)
    VALUES
        (1001, 100, 11, 4, 100, null, 'XAF'),
        (1002, 100, 12, 2, 250, 300, 'XAF')
    ;
