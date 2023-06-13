CREATE TABLE "users"
(
    id        uuid PRIMARY KEY NOT NULL,
    first_name VARCHAR(200)     NOT NULL,
    last_name  VARCHAR(200)     NOT NULL,
    email     VARCHAR(255)     NOT NULL,
    time_zone VARCHAR(100)
);