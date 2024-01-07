package com.groupGreen.ProvenanceCollector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Component
public class DataSender implements InsertData {

    private static final Logger logger = LoggerFactory.getLogger(DataSender.class);

    @Value("${pgrest.server.url}")
    private String serverUrl;

    @Value("${pgrest.endpoints.workflows}")
    private String workflowsEndpoint;

    @Value("${pgrest.endpoints.tasks}")
    private String tasksEndpoint;

    @Value("${pgrest.endpoints.resources}")
    private String resourcesEndpoint;

    private final WebClient webClient;

    public DataSender(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    @Override
    public void sendWorkflows(Workflow workflow) {
        JSONObject workflowData = new JSONObject();

        workflowData.put("workflow_id", workflow.getWorkflowID());
    
        logger.info("Sending workflow data: {}", workflowData);

        webClient.post()
            .uri(serverUrl + workflowsEndpoint)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(workflowData.toString())
            .retrieve()
            .bodyToMono(String.class)
//            .doOnSuccess(response -> logger.info("Sent workflow: {}", workflowData))
            .doOnError(error -> {
                if (error instanceof WebClientResponseException) {
                    WebClientResponseException ex = (WebClientResponseException) error;
                    logger.error("Error sending workflow. Status: {}, Body: {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
                } else {
                    logger.error("Error sending workflow: {}", error.getMessage());
                }
            })
            .subscribe();

    } 


    @Override
    public void sendTasks(WorkflowTask task) {
        JSONObject taskData = new JSONObject();

        taskData.put("task_name", task.getPod());
        taskData.put("workflow_id", task.getWorkflowID());
        taskData.put("start_time", task.getStartTime());
        taskData.put("end_time", task.getCompletionTime());

        logger.info("Sending task data: {}", taskData);

        webClient.post()
            .uri(serverUrl + tasksEndpoint)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(taskData.toString())
            .retrieve()
            .bodyToMono(String.class)
//            .doOnSuccess(response -> logger.info("Sent task: {}", taskData))
            .doOnError(error -> {
                if (error instanceof WebClientResponseException) {
                    WebClientResponseException ex = (WebClientResponseException) error;
                    logger.error("Error sending task. Status: {}, Body: {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
                } else {
                    logger.error("Error sending task: {}", error.getMessage());
                }
            })
            .subscribe();

    } 

    // TODO: Refactor to directly read metrics map from task object as JSON and reading db keys from application.yaml
    @Override
    public void sendResources(WorkflowTask task) {

        JSONObject metricsData = new JSONObject();

        Map<String, Double> metrics = task.getMetrics();

        metricsData.put("task_name", task.getPod());
        metricsData.put("cpu_avg", metrics.get("cpu_avg"));
        metricsData.put("cpu_min", metrics.get("cpu_min"));
        metricsData.put("cpu_max", metrics.get("cpu_max"));
        metricsData.put("mem_avg", metrics.get("mem_avg"));
        metricsData.put("mem_min", metrics.get("mem_min"));
        metricsData.put("mem_max", metrics.get("mem_max"));
        
        logger.info("Sending metrics data: {}", metricsData);

        webClient.post()
            .uri(serverUrl + resourcesEndpoint)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(metricsData.toString())
            .retrieve()
            .bodyToMono(String.class)
//            .doOnSuccess(response -> logger.info("Sent metrics: {}", metricsData))
            .doOnError(error -> {
                if (error instanceof WebClientResponseException) {
                    WebClientResponseException ex = (WebClientResponseException) error;
                    logger.error("Error sending metrics. Status: {}, Body: {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
                } else {
                    logger.error("Error sending metrics: {}", error.getMessage());
                }
            })
            .subscribe();

    } 
     

}