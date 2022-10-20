CREATE SEQUENCE IF NOT EXISTS user_table_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS chat_table_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS chat_user_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS friend_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS wishlist_seq START WITH 1 INCREMENT BY 1;

CREATE TYPE notification_level AS ENUM ('DAY', 'THREE_DAYS', 'WEEK', 'NEVER');

CREATE TABLE user_table
(
    id                 BIGINT  NOT NULL
        CONSTRAINT pk_user PRIMARY KEY,
    user_id            BIGINT  NOT NULL UNIQUE,
    user_tag           VARCHAR(255),
    user_name          VARCHAR(255),
    user_surname       VARCHAR(255),
    birthday           date,
    day                INTEGER,
    month              INTEGER,
    is_registered      BOOLEAN NOT NULL,
    is_updating        BOOLEAN NOT NULL,
    notification_level BIGINT  NOT NULL
);

CREATE TABLE chat_table
(
    id        BIGINT NOT NULL
        CONSTRAINT pk_chat_table PRIMARY KEY,
    chat_id   BIGINT NOT NULL UNIQUE,
    chat_code INTEGER UNIQUE
);


CREATE TABLE chat_user
(
    id      BIGINT                                                                NOT NULL
        CONSTRAINT pk_chat_user PRIMARY KEY,
    user_id BIGINT REFERENCES user_table (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    chat_id BIGINT REFERENCES chat_table (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    UNIQUE (user_id, chat_id)
);

CREATE TABLE friend
(
    id      BIGINT                                                                NOT NULL
        CONSTRAINT pk_friend PRIMARY KEY,
    user_id BIGINT REFERENCES user_table (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    friend_id BIGINT REFERENCES user_table (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    UNIQUE (user_id, friend_id)
);

CREATE TABLE wishlist_table
(
    id      BIGINT                                                                NOT NULL
        CONSTRAINT pk_wishlist_table PRIMARY KEY,
    user_id BIGINT REFERENCES user_table (id) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    wish    TEXT
);