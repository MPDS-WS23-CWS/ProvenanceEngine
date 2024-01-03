-- Creating workflows table
CREATE TABLE IF NOT EXISTS workflows (
    workflow_id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    tasks TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(50),
    db_created_at TIMESTAMP,
    db_updated_at TIMESTAMP
);

-- Creating tasks table with foreign key to workflows
CREATE TABLE IF NOT EXISTS tasks (
    task_id INT PRIMARY KEY,
    workflow_id INT,
    pod_name VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    completed BOOLEAN,
    completion_time TIMESTAMP,
    node_assigned VARCHAR(255),
    termination_reason TEXT,
    db_created_at TIMESTAMP,
    db_updated_at TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES workflows(workflow_id)
);

-- Creating resources table with foreign key to tasks
CREATE TABLE IF NOT EXISTS resources (
    resource_id INT PRIMARY KEY,
    task_id INT,
    node_name VARCHAR(255),
    cpu_avg DOUBLE PRECISION,
    cpu_min DOUBLE PRECISION,
    cpu_max DOUBLE PRECISION,
    mem_avg DOUBLE PRECISION,
    mem_min DOUBLE PRECISION,
    mem_max DOUBLE PRECISION,
    mem_requested DOUBLE PRECISION,
    cpu_requested DOUBLE PRECISION,
    db_created_at TIMESTAMP,
    db_updated_at TIMESTAMP, 
    FOREIGN KEY (task_id) REFERENCES tasks(task_id)
);
