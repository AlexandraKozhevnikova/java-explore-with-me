DROP TABLE IF EXISTS event;

DROP TABLE IF EXISTS category;

DROP TABLE IF EXISTS user_ewm;

CREATE TABLE IF NOT EXISTS user_ewm (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title   VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS category (
    cat_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE event (
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title              VARCHAR(120),
    cat_id             BIGINT       NOT NULL REFERENCES category (cat_id) ON DELETE RESTRICT,
    state              VARCHAR(255) NOT NULL,
    initiator_id       BIGINT       NOT NULL REFERENCES user_ewm (user_id) ON DELETE CASCADE,
    annotation         VARCHAR(2000),
    description        VARCHAR(7000),
    event_date         TIMESTAMP,
    lat                FLOAT        NOT NULL,
    lon                FLOAT        NOT NULL,
    paid               BOOLEAN               DEFAULT false,
    participant_limit  INTEGER               DEFAULT 0,
    request_moderation BOOLEAN               DEFAULT TRUE,
    created_on         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_on       timestamp,

    CONSTRAINT title_length CHECK (char_length(title) >= 3),
    CONSTRAINT annotation_length CHECK (char_length(annotation) >= 20),
    CONSTRAINT description_length CHECK (char_length(description) >= 20)
);

COMMENT ON COLUMN event.cat_id IS 'категория не может быть удалена, если есть привязанные события';

COMMENT ON COLUMN event.event_date IS 'не может быть раньше, чем через два часа от текущего момента';
