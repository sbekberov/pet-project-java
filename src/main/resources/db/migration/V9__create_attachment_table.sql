CREATE TABLE attachment
(
    id            uuid PRIMARY KEY,
    link          VARCHAR(255)                    ,
    name          VARCHAR(200)                    NOT NULL,
    updated_by    varchar(50)                     ,
    created_by    varchar(50)                     NOT NULL,
    created_date  DATE                       NOT NULL DEFAULT now(),
    updated_date  DATE,
    card_id      UUID,
    FOREIGN KEY (card_id) REFERENCES card (id)

);