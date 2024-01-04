package com.groupGreen.ProvenanceCollector;

public interface InsertData {
    
    void sendWorkflows(WorkflowTask task);
    
    void sendTasks(WorkflowTask task);

    void sendResources(WorkflowTask task);
}