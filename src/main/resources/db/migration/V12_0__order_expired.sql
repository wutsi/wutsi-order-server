-- All CANCELLED orders to EXPIRED
UPDATE T_ORDER_STATUS set status=4 WHERE status=3;
UPDATE T_ORDER set status=4 WHERE status=3;
