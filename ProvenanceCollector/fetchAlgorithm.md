Algorithm: ScheduleMetricsFetch
Input: A stream or list of task events (taskStarted, taskEnded) from Kubernetes

Procedure:
1. Initialize an empty map TaskMetricsMap to store task start and end times keyed by task ID.
2. For each event in the stream/list of task events:
   a. If it's a taskStarted event:
      - Extract taskId and startTime from the event.
      - Store taskId and startTime in TaskMetricsMap.
   b. If it's a taskEnded event:
      - Extract taskId and endTime from the event.
      - Retrieve the stored startTime for this taskId from TaskMetricsMap.
      - Schedule a fetchMetrics operation for this task (pass taskId, startTime, and endTime).
      - Remove the entry for this taskId from TaskMetricsMap.

Procedure fetchMetrics(taskId, startTime, endTime):
1. Construct a range query for Prometheus using taskId, startTime, and endTime.
2. Send the query to Prometheus and wait for the response.
3. Process the response to calculate average metrics over the task duration.
4. Store the processed metrics in the provenance storage system.
