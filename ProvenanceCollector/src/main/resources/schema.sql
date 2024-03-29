-- Creating workflows table
CREATE TABLE IF NOT EXISTS workflows (
    workflow_id VARCHAR(255) PRIMARY KEY,
    workflow_name VARCHAR(255), -- we use the process name for now
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    workflow_status VARCHAR(50),
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating tasks table 
CREATE TABLE IF NOT EXISTS tasks (
    task_key SERIAL PRIMARY KEY,
    pod_id VARCHAR(255),
    workflow_id VARCHAR(255),
    process_name VARCHAR(255),
    node_name VARCHAR(255),
    start_time BIGINT,
    end_time BIGINT,
    input_size BIGINT,
    completed BOOLEAN,
    completion_time BIGINT,
    termination_reason TEXT,
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating resources table with foreign key to tasks
CREATE TABLE IF NOT EXISTS resources (
    resource_key SERIAL PRIMARY KEY,
    pod_id VARCHAR(255),
    cpu_avg DOUBLE PRECISION,
    cpu_min DOUBLE PRECISION,
    cpu_max DOUBLE PRECISION,
    mem_avg DOUBLE PRECISION,
    mem_min DOUBLE PRECISION,
    mem_max DOUBLE PRECISION,
    mem_requested DOUBLE PRECISION,
    cpu_requested DOUBLE PRECISION,
    cpu_overcommit DOUBLE PRECISION,
    mem_overcommit DOUBLE PRECISION,
    cpu_idle DOUBLE PRECISION,
    mem_idle DOUBLE PRECISION,
    fs_reads_total DOUBLE PRECISION,
    fs_writes_total DOUBLE PRECISION,
    -- maybe add node information in here 
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    --FOREIGN KEY (task_name) REFERENCES tasks(task_name)
);

CREATE TABLE IF NOT EXISTS resources_time_series (
    resource_key SERIAL PRIMARY KEY,
    metric_name VARCHAR(255),
    pod_id VARCHAR(255),
    unix_time DOUBLE PRECISION,
    metric_value DOUBLE PRECISION,
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -- -- Creating nodes table 
-- CREATE TABLE IF NOT EXISTS node_metrics (
--     node_id SERIAL PRIMARY KEY,
--     node_name VARCHAR(255) UNIQUE NOT NULL,
--     system_user VARCHAR(255),
--     cpu_architecture VARCHAR(255),  
--     operating_system VARCHAR(255),  
--     kernel_version VARCHAR(255),  
--     processor_type VARCHAR(255),  
--     total_cpu_cores INT,  
--     threads_per_core INT, 
--     cpu_utilization_percentage DOUBLE PRECISION,  
--     total_memory_gb DOUBLE PRECISION,  
--     ram_type VARCHAR(255),  
--     used_memory_gb DOUBLE PRECISION, 
--     network_bandwidth_mbps DOUBLE PRECISION, 
--     disk_space_gb DOUBLE PRECISION,  
--     disk_used_gb DOUBLE PRECISION,   
--     uptime_seconds BIGINT,  
--     last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
-- );

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
