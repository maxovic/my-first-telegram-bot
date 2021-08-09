--liquibase formatted sql

--changeset maxat:1.0
CREATE TABLE IF NOT EXISTS vlife.user (
    id BIGSERIAL NOT NULL,
    telegram_user_id BIGINT NOT NULL,
    username VARCHAR,
    is_registered BOOLEAN DEFAULT FALSE
)
