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

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class DataSender implements InsertData {

    private static final Logger logger = LoggerFactory.getLogger(DataSender.class);

    @Value("${pgrest.server.url}")
    private String postgrestServerUrl;

    @Value("${pgrest.server.port}")
    private String postgrestServerPort;

    @Value("${pgrest.endpoints.workflows}")
    private String postgrestWorkflowsEndpoint;

    @Value("${pgrest.endpoints.tasks}")
    private String postgrestTasksEndpoint;

    @Value("${pgrest.endpoints.resources}")
    private String postgrestResourcesEndpoint;

    private final WebClient webClient;

    public DataSender(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    private void postConstruct() {
        if (Boolean.parseBoolean(System.getenv("LOCAL_DEPLOY"))) {
            this.postgrestServerUrl = "localhost";
        }
    }

    @Override
    public void sendWorkflows(Workflow workflow) {
        JSONObject workflowData = new JSONObject();

        workflowData.put("workflow_id", workflow.getWorkflowID());
    
        logger.info("Sending workflow data: {}", workflowData);

        webClient.post()
            .uri(uriBuilder -> uriBuilder.scheme("http").host(postgrestServerUrl).port(postgrestServerPort).path(postgrestWorkflowsEndpoint).build())
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

        taskData.put("pod_id", task.getPod());
        taskData.put("workflow_id", task.getWorkflowID());
        taskData.put("process_name", task.getProcessName());
        taskData.put("node_name", task.getNodeName());
        taskData.put("start_time", task.getStartTime());
        taskData.put("end_time", task.getCompletionTime());
        taskData.put("input_size", task.getInputSize());

        logger.info("Sending task data: {}", taskData);

        webClient.post()
            .uri(uriBuilder -> uriBuilder.scheme("http").host(postgrestServerUrl).port(postgrestServerPort).path(postgrestTasksEndpoint).build())
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

        metricsData.put("pod_id", task.getPod());
        metricsData.put("cpu_avg", metrics.get("cpu_avg"));
        metricsData.put("cpu_min", metrics.get("cpu_min"));
        metricsData.put("cpu_max", metrics.get("cpu_max"));
        metricsData.put("mem_avg", metrics.get("mem_avg"));
        metricsData.put("mem_min", metrics.get("mem_min"));
        metricsData.put("mem_max", metrics.get("mem_max"));
        metricsData.put("fs_reads_total", metrics.get("fs_reads_total"));
        metricsData.put("fs_writes_total", metrics.get("fs_writes_total"));
        
        logger.info("Sending metrics data: {}", metricsData);

        webClient.post()
            .uri(uriBuilder -> uriBuilder.scheme("http").host(postgrestServerUrl).port(postgrestServerPort).path(postgrestResourcesEndpoint).build())
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