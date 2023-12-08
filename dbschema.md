The following table shows the fields that can be included in the execution report:

task_id
Task ID.

hash
Task hash code.

native_id
Task ID given by the underlying execution system e.g. POSIX process PID when executed locally, job ID when executed by a grid engine, etc.

process
Nextflow process name.

tag
User provided identifier associated this task.

name
Task name.

status
Task status. Possible values are: NEW, SUBMITTED, RUNNING, COMPLETED, FAILED, and ABORTED.

exit
POSIX process exit status.

module
Environment module used to run the task.

container
Docker image name used to execute the task.

cpus
The cpus number request for the task execution.

time
The time request for the task execution

disk
The disk space request for the task execution.

memory
The memory request for the task execution.

attempt
Attempt at which the task completed.

submit
Timestamp when the task has been submitted.

start
Timestamp when the task execution has started.

complete
Timestamp when task execution has completed.

duration
Time elapsed to complete since the submission.

realtime
Task execution time i.e. delta between completion and start timestamp.

queue
The queue that the executor attempted to run the process on.

%cpu
Percentage of CPU used by the process.

%mem
Percentage of memory used by the process.

rss
Real memory (resident set) size of the process. Equivalent to ps -o rss .

vmem
Virtual memory size of the process. Equivalent to ps -o vsize .

peak_rss
Peak of real memory. This data is read from field VmHWM in /proc/$pid/status file.

peak_vmem
Peak of virtual memory. This data is read from field VmPeak in /proc/$pid/status file.

rchar
Number of bytes the process read, using any read-like system call from files, pipes, tty, etc. This data is read from file /proc/$pid/io.

wchar
Number of bytes the process wrote, using any write-like system call. This data is read from file /proc/$pid/io.

syscr
Number of read-like system call invocations that the process performed. This data is read from file /proc/$pid/io.

syscw
Number of write-like system call invocations that the process performed. This data is read from file /proc/$pid/io.

read_bytes
Number of bytes the process directly read from disk. This data is read from file /proc/$pid/io.

write_bytes
Number of bytes the process originally dirtied in the page-cache (assuming they will go to disk later). This data is read from file /proc/$pid/io.

vol_ctxt
Number of voluntary context switches. This data is read from field voluntary_ctxt_switches in /proc/$pid/status file.

inv_ctxt
Number of involuntary context switches. This data is read from field nonvoluntary_ctxt_switches in /proc/$pid/status file.

env
The variables defined in task execution environment.

workdir
The directory path where the task was executed.

script
The task command script.

scratch
The value of the process scratch directive.

error_action
The action applied on errof task failure.

hostname
New in version 22.05.0-edge.

The host on which the task was executed. Supported only for the Kubernetes executor yet. Activate with k8s.fetchNodeName = true in the Nextflow config file.

cpu_model
New in version 22.07.0-edge.

The name of the CPU model used to execute the task. This data is read from file /proc/cpuinfo.
