CREATE TABLE IF NOT EXISTS workflows (
	workflow_id SERIAL PRIMARY KEY,
	name VARCHAR(255),
	description TEXT,
	created_at TIMESTAMP,
	updated_at TIMESTAMP
);

