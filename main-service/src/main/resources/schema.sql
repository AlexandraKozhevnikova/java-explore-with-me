DROP TABLE IF EXISTS bill;

DROP TABLE IF EXISTS request;

DROP TABLE IF EXISTS compilation_event;

DROP TABLE IF EXISTS compilation;

DROP TABLE IF EXISTS event;

DROP TABLE IF EXISTS currency;

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

CREATE TABLE IF NOT EXISTS currency (
    currency_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title       VARCHAR(50) NOT NULL UNIQUE
);

COMMENT ON COLUMN currency.title IS 'Названия валют по ОКВ — Общероссийский классификатор валют';

CREATE TABLE IF NOT EXISTS event (
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title              VARCHAR(120),
    cat_id             BIGINT    NOT NULL REFERENCES category (cat_id) ON DELETE RESTRICT,
    state              INTEGER   NOT NULL,
    initiator_id       BIGINT    NOT NULL REFERENCES user_ewm (user_id) ON DELETE CASCADE,
    annotation         VARCHAR(2000),
    description        VARCHAR(7000),
    event_date         TIMESTAMP,
    lat                FLOAT     NOT NULL,
    lon                FLOAT     NOT NULL,
    paid               BOOLEAN            DEFAULT false,
    participant_limit  INTEGER            DEFAULT 0,
    request_moderation BOOLEAN            DEFAULT TRUE,
    created_on         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_on       TIMESTAMP,
    total              NUMERIC,
    currency_id        BIGINT REFERENCES currency (currency_id) ON DELETE RESTRICT,

    CONSTRAINT title_length CHECK (char_length(title) >= 3),
    CONSTRAINT annotation_length CHECK (char_length(annotation) >= 20),
    CONSTRAINT description_length CHECK (char_length(description) >= 20)
);

COMMENT ON COLUMN event.cat_id IS 'категория не может быть удалена, если есть привязанные события';

COMMENT ON COLUMN event.event_date IS 'не может быть раньше, чем через два часа от текущего момента';

CREATE TABLE IF NOT EXISTS compilation (
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title          VARCHAR(120) NOT NULL UNIQUE,
    pinned         BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT title_blank_check check ( title NOT LIKE '' and title NOT LIKE ' ' )
);

CREATE TABLE IF NOT EXISTS compilation_event (
    compilation_id BIGINT NOT NULL REFERENCES compilation (compilation_id) ON DELETE CASCADE,
    event_id       BIGINT NOT NULL REFERENCES event (event_id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS request (
    request_id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    participant_id BIGINT    NOT NULL REFERENCES user_ewm (user_id) ON DELETE CASCADE,
    event_id       BIGINT    NOT NULL REFERENCES event (event_id) ON DELETE CASCADE,
    state          INTEGER   NOT NULL,
    created_on     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_request UNIQUE (event_id, participant_id, state)
);

INSERT INTO currency (title)
VALUES ('RUB'),
       ('USD');

CREATE TABLE IF NOT EXISTS bill (
    bill_id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    participant_id BIGINT    REFERENCES user_ewm (user_id) ON DELETE SET NULL,
    event_id       BIGINT    REFERENCES event (event_id) ON DELETE SET NULL,
    amount         numeric   NOT NULL,
    currency_id    BIGINT    NOT NULL REFERENCES currency (currency_id) ON DELETE RESTRICT,
    state          INTEGER   NOT NULL,
    created_on     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
