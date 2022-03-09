ALTER TABLE T_ORDER ADD COLUMN expires TIMESTAMPTZ DEFAULT now() + (30 * interval '1 minute');
CREATE INDEX T_ORDER_expires ON T_ORDER (expires);

UPDATE T_ORDER set expires = created + (30 * interval '1 minute');
ALTER TABLE T_ORDER ALTER COLUMN expires SET NOT NULL;
