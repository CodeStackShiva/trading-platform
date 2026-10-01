CREATE TABLE orders (
    id               UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    idempotency_key  VARCHAR(100)     NOT NULL,
    client_id        VARCHAR(64)      NOT NULL,
    symbol           VARCHAR(12)      NOT NULL,
    side             VARCHAR(4)       NOT NULL,
    quantity         BIGINT           NOT NULL,
    price            DECIMAL(19,4)    NOT NULL,
    status           VARCHAR(16)      NOT NULL,
    reject_reason    VARCHAR(255)     NULL,
    created_at       DATETIME2        NOT NULL,
    version          BIGINT           NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX ux_orders_idem ON orders (idempotency_key);
CREATE INDEX ix_orders_client_created ON orders (client_id, created_at DESC);

CREATE TABLE outbox (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    topic        VARCHAR(100)  NOT NULL,
    message_key  VARCHAR(100)  NOT NULL,
    payload      NVARCHAR(MAX) NOT NULL,
    published    BIT           NOT NULL DEFAULT 0,
    created_at   DATETIME2     NOT NULL
);
-- filtered index keeps the relay's poll cheap no matter how big the table grows
CREATE INDEX ix_outbox_unpublished ON outbox (id) WHERE published = 0;
