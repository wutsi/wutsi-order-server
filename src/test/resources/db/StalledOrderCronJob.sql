INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, status, total_price, currency, cancelled, created, expires)
    VALUES
        (110, 1, 10, 1, 0, 900, 'XAF', null, now(), '2030-01-01'),
        (111, 1, 10, 1, 0, 1000, 'XAF', null, now(), '2010-01-01'),
        (120, 1, 20, 1, 2, 1000, 'XAF', null, '2021-01-01', '2010-01-01'),
        (200, 2, 11, 1, 1, 1000, 'XAF', null, now(), '2010-01-01'),
        (900, 9, 11, 1, 0, 900, 'XAF', null, '2019-01-01', '2010-01-01')
;
