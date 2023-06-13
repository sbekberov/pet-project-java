CREATE TABLE workspaces
(
    id                   UUID PRIMARY KEY NOT NULL,
    updated_by           VARCHAR(50),
    created_by           VARCHAR(50)      NOT NULL,
    created_date         DATE             NOT NULL,
    updated_date         DATE,
    name                 VARCHAR(20)      NOT NULL,
    description          VARCHAR(50),
    visibility VARCHAR(10)      NOT NULL DEFAULT 'PUBLIC'
);