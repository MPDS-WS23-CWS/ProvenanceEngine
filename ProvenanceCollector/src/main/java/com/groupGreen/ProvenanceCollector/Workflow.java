package com.groupGreen.ProvenanceCollector;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Workflow {

    private String workflowID;

    private String workflowName;

    private long startTime = -1;

    private long completionTime = -1;

    public Workflow(String workflowID) {
        setWorkflowID(workflowID);
    }

}
