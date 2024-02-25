package com.groupGreen.ProvenanceCollector;

public interface InsertData {
    
    void sendWorkflows(Workflow worfklow);
    
    void sendTasks(WorkflowTask task);

    void sendResources(WorkflowTask task);
}