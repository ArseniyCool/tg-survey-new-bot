CREATE TABLE IF NOT EXISTS processed_updates (
    update_id BIGINT PRIMARY KEY,
    status VARCHAR(32) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
