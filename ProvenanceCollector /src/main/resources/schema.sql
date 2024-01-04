-- Creating workflows table
CREATE TABLE IF NOT EXISTS workflows (
    workflow_id SERIAL PRIMARY KEY, 
    workflow_name VARCHAR(255), -- we use the process name for now
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    workflow_status VARCHAR(50),
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating tasks table 
CREATE TABLE IF NOT EXISTS tasks (
    task_id SERIAL PRIMARY KEY,
    task_name VARCHAR(255),
    workflow_name VARCHAR(255),
    start_time BIGINT,
    end_time BIGINT,
    completed BOOLEAN,
    completion_time BIGINT,
    node_assigned VARCHAR(255),
    termination_reason TEXT,
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating resources table with foreign key to tasks
CREATE TABLE IF NOT EXISTS resources (
    resource_id SERIAL PRIMARY KEY,
    task_name VARCHAR(255),
    node_name VARCHAR(255),
    cpu_avg DOUBLE PRECISION,
    cpu_min DOUBLE PRECISION,
    cpu_max DOUBLE PRECISION,
    mem_avg DOUBLE PRECISION,
    mem_min DOUBLE PRECISION,
    mem_max DOUBLE PRECISION,
    mem_requested DOUBLE PRECISION,
    cpu_requested DOUBLE PRECISION,
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    --FOREIGN KEY (task_name) REFERENCES tasks(task_name)
);

-- db-related functions
-- CREATE OR REPLACE FUNCTION update_task_completion()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     IF (NEW.start_time IS NOT NULL AND NEW.end_time IS NOT NULL) THEN
--         NEW.completion_time = NEW.end_time - NEW.start_time;
--         NEW.completed = true;
--     ELSE
--         NEW.completed = false;
--     END IF;
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;

-- -- trigger the update after insert on tasks
-- CREATE TRIGGER tasks_after_insert
-- AFTER INSERT ON tasks
-- FOR EACH ROW EXECUTE FUNCTION update_task_completion();
