package com.groupGreen.ProvenanceCollector;

public interface InsertData {

    void postWorkflowData(WorkflowTask workflowTask);

    void postTaskData(WorkflowTask workflowTask);

    void postResourceMetrics(WorkflowTask workflowTask);

}