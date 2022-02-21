CREATE TABLE T_ORDER(
    id              SERIAL NOT NULL,

    tenant_id       BIGINT NOT NULL,
    merchant_id     BIGINT NOT NULL,
    account_id      BIGINT NOT NULL,

    status          INT NOT NULL DEFAULT 0,
    total_price     DECIMAL(20,4) NOT NULL DEFAULT 0,
    currency        VARCHAR(3) NOT NULL,

    created         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated         TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);

CREATE TABLE T_ORDER_ITEM(
    id              SERIAL NOT NULL,

    order_fk        BIGINT NOT NULL REFERENCES T_ORDER(id),
    product_id      BIGINT NOT NULL,

    unit_price      DECIMAL(20,4) NOT NULL DEFAULT 0,
    currency        VARCHAR(3) NOT NULL,
    quantity        INT NOT NULL,

    UNIQUE(order_fk, product_id),
    PRIMARY KEY (id)
);

CREATE OR REPLACE FUNCTION order_updated()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_order_updated
BEFORE UPDATE ON T_ORDER
FOR EACH ROW
EXECUTE PROCEDURE order_updated();
