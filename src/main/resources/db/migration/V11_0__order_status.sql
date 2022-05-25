CREATE TABLE T_ORDER_STATUS(
    id              SERIAL NOT NULL,

    order_fk        VARCHAR(36) NOT NULL REFERENCES T_ORDER(id),

    status          INT NOT NULL DEFAULT 0,
    previous_status INT,
    reason          VARCHAR(30),
    comment         TEXT,
    created         TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);

ALTER TABLE T_ORDER DROP COLUMN cancelled;
