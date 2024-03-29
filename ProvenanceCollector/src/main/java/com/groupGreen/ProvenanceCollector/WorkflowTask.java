package com.groupGreen.ProvenanceCollector;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class WorkflowTask {

    private String pod; // in db: pod_id

    private String workflowID;

    private String processName;

    private String nodeName;

    private long startTime = -1;

    private long completionTime = -1;

    private long inputSize = -1;

    private Map<String, Double> metrics = new HashMap<>();

    private Map<String, List<TimeSeriesDataPoint>> timeSeriesMetrics = new HashMap<>();

    public WorkflowTask(String pod, long completionTime) {
        setPod(pod);
        setCompletionTime(completionTime);
    }

    public void putMetric(String metric, Double value) {
        metrics.put(metric, value);
    }

    public void putTimeSeriesMetric(String metric, List<TimeSeriesDataPoint> dataPoints) {
        timeSeriesMetrics.put(metric, dataPoints);
    }

    public String toString() {
        return "{pod: " + getPod()
                + ", processName: " + getProcessName()
                + ", startTime: " + getStartTime()
                + ", endTime: " + getCompletionTime()
                + ", metrics: " + getMetrics() + "}";
    }

    public boolean startTimeSet () {
        return getStartTime() >= 0;
    }

    public boolean endTimeSet () {
        return getCompletionTime() >= 0;
    }
}