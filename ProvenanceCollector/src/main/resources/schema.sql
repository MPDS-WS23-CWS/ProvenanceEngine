CREATE TABLE IF NOT EXISTS workflows (
    workflow_id VARCHAR(255) PRIMARY KEY,
    workflow_name VARCHAR(255), -- we use the process name for now
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    workflow_status VARCHAR(50),
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE IF NOT EXISTS resources (
    resource_key SERIAL PRIMARY KEY,
    pod_id VARCHAR(255),
    cpu_avg DOUBLE PRECISION,
    cpu_min DOUBLE PRECISION,
    cpu_max DOUBLE PRECISION,
    mem_avg DOUBLE PRECISION,
    mem_min DOUBLE PRECISION,
    mem_max DOUBLE PRECISION,
    fs_reads_total DOUBLE PRECISION,
    fs_writes_total DOUBLE PRECISION,
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resources_time_series (
    resource_key SERIAL PRIMARY KEY,
    pod_id VARCHAR(255),
    metric_name VARCHAR(255),
    unix_time DOUBLE PRECISION,
    metric_value DOUBLE PRECISION,
    db_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
