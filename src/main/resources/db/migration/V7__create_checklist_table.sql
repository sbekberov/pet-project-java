CREATE TABLE checklist
(
    id           uuid PRIMARY KEY NOT NULL,
    name         VARCHAR(200)     NOT NULL,
    updated_by   VARCHAR(50),
    created_by   VARCHAR(50)      NOT NULL,
    created_date DATE        NOT NULL,
    updated_date DATE,
    card_id      UUID             NOT NULL,
    FOREIGN KEY (card_id) REFERENCES card (id)
);