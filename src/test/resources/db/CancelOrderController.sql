INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, status, total_price, currency, cancelled)
    VALUES
        (100, 1, 11, 1, 0, 900, 'XAF', null),
        (203, 1, 11, 1, 3, 900, 'XAF', '2020-01-01'),
        (204, 1, 11, 1, 4, 900, 'XAF', '2020-01-01')
;
