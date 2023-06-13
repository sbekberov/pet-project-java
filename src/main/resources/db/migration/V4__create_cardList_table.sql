CREATE TABLE card_list
(
    id           uuid PRIMARY KEY           NOT NULL,
    board_id     uuid NOT NULL ,
    FOREIGN KEY (board_id) REFERENCES board (id),
    updated_by   varchar(50),
    created_by   varchar(50)                NOT NULL,
    created_date DATE                 NOT NULL,
    updated_date DATE,
    name         VARCHAR(200)               NOT NULL,
    archived     BOOLEAN                    NOT NULL DEFAULT FALSE
);