INSERT INTO T_ADDRESS(id, account_id, tenant_id, city_id, country)
    VALUES
        (200, 1, 1, 1000, 'CM'),
        (300, 2, 1, 1000, 'CM')
;


INSERT INTO T_ORDER(id, tenant_id, merchant_id, account_id, reservation_id, status, total_price, sub_total_price, delivery_fees, savings_amount, currency)
    VALUES
        (100, 1, 11, 1, 777, 0, 1150, 900, 150, 100, 'XAF'),
        (130, 1, 11, 1, 777, 2, 1150, 900, 150, 100, 'XAF'),
        (140, 1, 11, 1, 777, 3, 1150, 900, 150, 100, 'XAF'),

        (200, 1, 11, 1, 777, 0, 1150, 900, 150, 100, 'XAF')
;

