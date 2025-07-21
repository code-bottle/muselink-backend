create schema if not exists "muselink";

CREATE TABLE IF NOT EXISTS muselink.rag_vector (
    id        UUID not null PRIMARY KEY,
    content   TEXT,
    metadata  JSON,
    embedding VECTOR(1536)
);
