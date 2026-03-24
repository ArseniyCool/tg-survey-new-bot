CREATE TABLE IF NOT EXISTS user_sessions (
    chat_id BIGINT PRIMARY KEY,
    state VARCHAR(32),
    phone VARCHAR(32),
    project_name VARCHAR(255),
    purpose TEXT,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
