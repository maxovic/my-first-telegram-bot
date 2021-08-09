--liquibase formatted sql

--changeset maxat:1.0 runOnChange:true
CREATE TABLE IF NOT EXISTS vlife.user_preference (
    id BIGSERIAL NOT NULL,
    telegram_user_id BIGINT NOT NULL,
    breed VARCHAR NOT NULL,
    is_liked BOOLEAN NOT NULL
)
