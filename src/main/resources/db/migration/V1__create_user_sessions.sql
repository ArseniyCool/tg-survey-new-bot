CREATE TABLE IF NOT EXISTS user_sessions (
    chat_id BIGINT PRIMARY KEY,
    state VARCHAR(32),
    phone VARCHAR(32),
    project_name VARCHAR(255),
    purpose TEXT,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS processed_updates (
    update_id BIGINT PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
