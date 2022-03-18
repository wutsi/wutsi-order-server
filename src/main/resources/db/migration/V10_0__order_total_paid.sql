ALTER TABLE T_ORDER ADD COLUMN total_paid DECIMAL(20,4) DEFAULT 0;
UPDATE T_ORDER SET total_paid=total_price WHERE payment_status=1;
