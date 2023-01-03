CREATE TABLE IF NOT EXISTS "VideoTable"(
    "uuid" uuid NOT NULL PRIMARY KEY,
    "username" VARCHAR(255),
    "password" VARCHAR(255),
    "email" VARCHAR(255)
);