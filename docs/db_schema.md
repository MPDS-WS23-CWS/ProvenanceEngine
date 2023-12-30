# Workflows Table

Contains information about each workflow execution.
## Columns: workflow_id, start_time, end_time, status, etc.
## Relevant Metrics: kube_pod_created, kube_pod_completion_time 

# Tasks Table

Stores details about individual tasks within a workflow.
## Columns: task_id, workflow_id, pod_name, pod_uid, start_time, end_time, node_assigned, termination_reason, etc.
## Relevant Metrics: kube_pod_info (for pod details), kube_pod_created and kube_pod_completion_time (for task start and end times), kube_pod_container_status_last_terminated_reason (for termination info).

# Resources Table

Holds metrics related to resource usage by pods and nodes.
## Columns: record_id, pod_name, node_name, cpu_avg, cpu_min, cpu_max, mem_avg, mem_min, mem_max, mem_requested, cpu_requested.
## Relevant Metrics: container_cpu_usage_seconds_total, container_memory_working_set_bytes, container_memory_max_usage_bytes, container_memory_usage_bytes, kube_pod_container_resource_requests for pods; node_cpu_seconds_total, kube_node_status_allocatable, node_memory_MemFree_bytes, node_memory_Buffers_bytes, node_memory_MemTotal_bytes for nodes.

# Mapping Prometheus Metrics to Tables

## Workflows Table:

kube_pod_created 

kube_pod_completion_time 

## Tasks Table:

kube_pod_info provides pod_name, pod_uid, and node_assigned.
kube_pod_created and kube_pod_completion_time provide start_time and end_time for tasks.
kube_pod_container_status_last_terminated_reason gives insight into why a task (pod) terminated.

## Resources Table:

### For Pods: 

container_cpu_usage_seconds_total and container_memory_working_set_bytes 

calculate cpu_avg, cpu_min, cpu_max, mem_avg, mem_min, mem_max:

### For Nodes:

node_cpu_seconds_total, node_memory_MemFree_bytes, node_memory_Buffers_bytes, and node_memory_MemTotal_bytes 
kube_pod_container_resource_requests provides mem_requested and cpu_requested 