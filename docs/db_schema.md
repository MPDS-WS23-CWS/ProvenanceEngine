# Kubernetes Metrics Reference

Details of the metric data that Kubernetes components export.
Metrics (v1.29)
This page details the metrics that different Kubernetes components export. You can query the metrics endpoint for these components using an HTTP scrape, and fetch the current metrics data in Prometheus format.

## Pre-Selection of metrics to be used for the CWS-Prov-Storage

container_cpu_usage_seconds_total
Cumulative cpu time consumed by the container in core-seconds


container_memory_working_set_bytes
Current working set of the container in bytes


container_start_time_seconds
Start time of the container since unix epoch in seconds


job_controller_job_pods_finished_total
The number of finished Pods that are fully tracked


kube_pod_resource_limit
Resources limit for workloads on the cluster, broken down by pod. This shows the resource usage the scheduler and kubelet expect per pod for resources along with the unit for the resource if any.


kube_pod_resource_request
Resources requested by workloads on the cluster, broken down by pod. This shows the resource usage the scheduler and kubelet expect per pod for resources along with the unit for the resource if any.


node_cpu_usage_seconds_total
Cumulative cpu time consumed by the node in core-seconds


node_memory_working_set_bytes
Current working set of the node in bytes


pod_cpu_usage_seconds_total
Cumulative cpu time consumed by the pod in core-seconds


pod_memory_working_set_bytes
Current working set of the pod in bytes


scheduler_pod_scheduling_duration_seconds
E2e latency for a pod being scheduled which may include multiple scheduling attempts.


scheduler_queue_incoming_pods_total
Number of pods added to scheduling queues by event and queue type.


scheduler_pod_scheduling_sli_duration_seconds
E2e latency for a pod being scheduled, from the time the pod enters the scheduling queue an d might involve multiple scheduling attempts.


apiserver_storage_size_bytes
Size of the storage database file physically allocated in bytes.


attach_detach_controller_attachdetach_controller_forced_detaches
Number of times the A/D Controller performed a forced detach

attachdetach_controller_total_volumes
Number of volumes in A/D Controller


authenticated_user_requests
Counter of authenticated requests broken out by username.


container_swap_usage_bytes
Current amount of the container swap usage in bytes. Reported only on non-windows systems


ephemeral_volume_controller_create_total
Number of PersistenVolumeClaims creation requests


garbagecollector_controller_resources_sync_error_total
Number of garbage collector resources sync errors


job_controller_terminated_pods_tracking_finalizer_total
`The number of terminated pods (phase=Failed|Succeeded), that have the finalizer batch.kubernetes.io/job-tracking, The event label can be "add" or "delete".`

kubelet_cgroup_manager_duration_seconds
Duration in seconds for cgroup manager operations. Broken down by method.


kubelet_container_log_filesystem_used_bytes
Bytes used by the container's logs on the filesystem.


kubelet_containers_per_pod_count
The number of containers per pod.


kubelet_cpu_manager_pinning_errors_total
The number of cpu core allocations which required pinning failed.


kubelet_cpu_manager_pinning_requests_total
The number of cpu core allocations which required pinning.


kubelet_desired_pods
The number of pods the kubelet is being instructed to run. static is true if the pod is not from the apiserver.


kubelet_managed_ephemeral_containers
Current number of ephemeral containers in pods managed by this kubelet.


kubelet_mirror_pods
The number of mirror pods the kubelet will try to create (one per admitted static pod)


kubelet_node_name
The node's name. The count is always 1.


kubelet_orphan_pod_cleaned_volumes
The total number of orphaned Pods whose volumes were cleaned in the last periodic sweep.


kubelet_orphan_pod_cleaned_volumes_errors
The number of orphaned Pods whose volumes failed to be cleaned in the last periodic sweep.


kubelet_orphaned_runtime_pods_total
Number of pods that have been detected in the container runtime without being already known to the pod worker. This typically indicates the kubelet was restarted while a pod was force deleted in the API or in the local configuration, which is unusual.


kubelet_pod_start_duration_seconds
Duration in seconds from kubelet seeing a pod for the first time to the pod starting to run


kubelet_pod_start_sli_duration_seconds
Duration in seconds to start a pod, excluding time to pull images and run init containers, measured from pod creation timestamp to when all its containers are reported as started and observed via watch


kubelet_pod_worker_duration_seconds
Duration in seconds to sync a single pod. Broken down by operation type: create, update, or sync


kubelet_pod_worker_start_duration_seconds
Duration in seconds from kubelet seeing a pod to starting a worker.


kubelet_preemptions
Cumulative number of pod preemptions by preemption resource


kubelet_restarted_pods_total
Number of pods that have been restarted because they were deleted and recreated with the same UID while the kubelet was watching them (common for static pods, extremely uncommon for API pods)


kubelet_running_containers
Number of containers currently running


kubelet_running_pods
Number of pods that have a running pod sandbox


kubelet_runtime_operations_duration_seconds
Duration in seconds of runtime operations. Broken down by operation type.


kubelet_runtime_operations_errors_total
Cumulative number of runtime operation errors by operation type.


kubelet_runtime_operations_total
Cumulative number of runtime operations by operation type.


kubelet_server_expiration_renew_errors
Counter of certificate renewal errors.


kubelet_started_containers_errors_total
Cumulative number of errors when starting containers


kubelet_started_containers_total
Cumulative number of containers started


kubelet_started_host_process_containers_errors_total
Cumulative number of errors when starting hostprocess containers. This metric will only be collected on Windows.


kubelet_started_host_process_containers_total
Cumulative number of hostprocess containers started. This metric will only be collected on Windows.


kubelet_started_pods_errors_total
Cumulative number of errors when starting pods


kubelet_started_pods_total
Cumulative number of pods started


kubelet_volume_stats_available_bytes
Number of available bytes in the volume


kubelet_volume_stats_capacity_bytes
Capacity in bytes of the volume


kubelet_volume_stats_inodes
Maximum number of inodes in the volume


kubelet_volume_stats_inodes_free
Number of free inodes in the volume


kubelet_volume_stats_inodes_used
Number of used inodes in the volume


kubelet_volume_stats_used_bytes
Number of used bytes in the volume


kubelet_working_pods
Number of pods the kubelet is actually running, broken down by lifecycle phase, whether the pod is desired, orphaned, or runtime only (also orphaned), and whether the pod is static. An orphaned pod has been removed from local configuration or force deleted in the API and consumes resources that are not otherwise visible.


node_controller_initial_node_sync_delay_seconds
Number of seconds after node creation when NodeController finished the initial synchronization of a single node.

Counter measuring total number of CIDR allocations.


pv_collector_bound_pv_count
Gauge measuring number of persistent volume currently bound



pv_collector_bound_pvc_count
Gauge measuring number of persistent volume claim currently bound



pv_collector_total_pv_count
Gauge measuring total number of persistent volumes



pv_collector_unbound_pv_count
Gauge measuring number of persistent volume currently unbound



pv_collector_unbound_pvc_count
Gauge measuring number of persistent volume claim currently unbound


resourceclaim_controller_create_attempts_total
Number of ResourceClaims creation requests
Stability Level:ALPHA


retroactive_storageclass_errors_total
Total number of failed retroactive StorageClass assignments to persistent volume claim


retroactive_storageclass_total
Total number of retroactive StorageClass assignments to persistent volume claim


scheduler_scheduler_cache_size
Number of nodes, pods, and assumed (bound) pods in the scheduler cache.


scheduler_scheduling_algorithm_duration_seconds
Scheduling algorithm latency in seconds


scheduler_volume_binder_cache_requests_total
Total number for request volume binding cache


scheduler_volume_scheduling_stage_error_total
Volume scheduling stage error count


storage_count_attachable_volumes_in_use
Measure number of volumes in use


storage_operation_duration_seconds
Storage operation duration


volume_manager_selinux_container_warnings_total
Number of errors when kubelet cannot compute SELinux context for a container that are ignored. They will become real errors when SELinuxMountReadWriteOncePod feature is expanded to all volume access modes.


volume_manager_total_volumes
Number of volumes in Volume Manager


volume_operation_total_errors
Total volume operation errors


volume_operation_total_seconds
Storage operation end to end duration in seconds


watch_cache_capacity
Total capacity of watch cache broken by resource type.


workqueue_depth
Current depth of workqueue


workqueue_longest_running_processor_seconds
How many seconds has the longest running processor for workqueue been running.


workqueue_queue_duration_seconds
How long in seconds an item stays in workqueue before being requested.


workqueue_retries_total
Total number of retries handled by workqueue


workqueue_unfinished_work_seconds
How many seconds of work has done that is in progress and hasn't been observed by work_duration. Large values indicate stuck threads. One can deduce the number of stuck threads by observing the rate at which this increases.


workqueue_work_duration_seconds
How long in seconds processing an item from workqueue takes.


