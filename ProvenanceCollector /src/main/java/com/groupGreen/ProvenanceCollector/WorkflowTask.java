package com.groupGreen.ProvenanceCollector;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WorkflowTask {

    private String pod;

    private String processName;

    private long startTime = -1;

    private long endTime = -1;

    public WorkflowTask(String pod) {
        setPod(pod);
    }

    public String toString() {
        return "{processName: " + getProcessName() + ", startTime: " + getStartTime() + ", endTime: " + getEndTime() + "}";
    }

    public boolean startTimeSet () {
        return getStartTime() >= 0;
    }

    public boolean endTimeSet () {
        return getEndTime() >= 0;
    }
}