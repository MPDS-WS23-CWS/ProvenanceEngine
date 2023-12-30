package com.groupGreen.ProvenanceCollector;

public interface InsertData {

    void postWorkflowData(WorkflowTask workflowTask);

    void postTaskDtaa(WorkflowTask workflowTask);

    void postResourceMetrics(WorkflowTask workflowTask);

}