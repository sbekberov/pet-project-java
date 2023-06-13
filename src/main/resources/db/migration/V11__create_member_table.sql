CREATE TABLE member
(
    id                   uuid PRIMARY KEY               NOT NULL,
    role                 VARCHAR(255)                    NOT NULL DEFAULT 'GUEST',
    user_id      UUID             NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    updated_by   varchar(50),
    created_by   varchar(50)  ,
    created_date DATE          ,
    updated_date DATE
);