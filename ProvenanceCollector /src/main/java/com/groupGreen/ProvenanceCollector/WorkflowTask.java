package com.groupGreen.ProvenanceCollector;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class WorkflowTask {

    private String pod;

    private String processName;

    private long startTime = -1;

    private long completionTime = -1;

    private Map<String, Object> metrics;

    public WorkflowTask(String pod) {
        setPod(pod);
    }

    public WorkflowTask(String pod, long completionTime) {
        setPod(pod);
        setCompletionTime(completionTime);
    }

//    public String getMetricsAsJson() {
//        // TODO implement
//        // return { "process": "RNASEQ", "cpu": 0.40, "mem": 0.10}
//    }

    public void putMetric(String metric, Object value) {
        metrics.put(metric, value);
    }

    public String toString() {
        return "{processName: " + getProcessName() + ", startTime: " + getStartTime() + ", endTime: " + getCompletionTime() + "}";
    }

    public boolean startTimeSet () {
        return getStartTime() >= 0;
    }

    public boolean endTimeSet () {
        return getCompletionTime() >= 0;
    }
}