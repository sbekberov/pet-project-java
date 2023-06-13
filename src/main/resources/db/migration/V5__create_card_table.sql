CREATE TABLE card
(
    id           uuid PRIMARY KEY              NOT NULL,
    card_list_id  uuid NOT NULL ,
    FOREIGN KEY (card_list_id) REFERENCES card_list (id),
    reminder_id  UUID,
    FOREIGN KEY (reminder_id) REFERENCES reminder (id),
    updated_by   varchar(50) ,
    created_by   varchar(50)                   NOT NULL,
    created_date DATE                     NOT NULL DEFAULT now(),
    updated_date DATE,
    name         VARCHAR(200)                  NOT NULL,
    description  VARCHAR(200),
    archived     BOOLEAN                       NOT NULL DEFAULT FALSE
);