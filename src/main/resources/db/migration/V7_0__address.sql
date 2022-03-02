CREATE TABLE T_ADDRESS(
    id              SERIAL NOT NULL,

    account_id      BIGINT NOT NULL,
    tenant_id       BIGINT NOT NULL,

    first_name      VARCHAR(80),
    last_name       VARCHAR(80),
    email           VARCHAR(160),
    street          VARCHAR(160),
    zip_code        VARCHAR(20),
    city_id         BIGINT,
    country         VARCHAR(2),

    PRIMARY KEY (id)
);

ALTER TABLE T_ORDER ADD COLUMN shipping_address_fk BIGINT REFERENCES T_ADDRESS(id);
