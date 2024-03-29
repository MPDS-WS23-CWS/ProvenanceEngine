package com.groupGreen.ProvenanceCollector;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Component
public class DataSender {

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

    @Value("${pgrest.endpoints.resources_time_series}")
    private String postgrestResourcesTimeSeriesEndpoint;
    private final WebClient webClient;

    public DataSender(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public void sendWorkflows(Workflow workflow) {
        JSONObject workflowData = new JSONObject();

        workflowData.put("workflow_id", workflow.getWorkflowID());
    
        logger.info("Sending workflow data: {}", workflowData);

        webClient.post()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(postgrestServerUrl)
                    .port(postgrestServerPort)
                    .path(postgrestWorkflowsEndpoint)
                    .build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(workflowData.toString())
            .retrieve()
            .bodyToMono(String.class)
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
            .uri(uriBuilder -> uriBuilder.scheme("http")
                    .host(postgrestServerUrl)
                    .port(postgrestServerPort)
                    .path(postgrestTasksEndpoint)
                    .build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(taskData.toString())
            .retrieve()
            .bodyToMono(String.class)
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


    public void sendResources(WorkflowTask task) {
        JSONObject metricsData = new JSONObject();
        metricsData.put("pod_id", task.getPod());

        Map<String, Double> metrics = task.getMetrics();
        for(Map.Entry<String, Double> metric : metrics.entrySet()) {
            String metricName = metric.getKey();
            Double value = metric.getValue();
            metricsData.put(metricName, value);
        }
        
        logger.info("Sending metrics data: {}", metricsData);

        webClient.post()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(postgrestServerUrl)
                    .port(postgrestServerPort)
                    .path(postgrestResourcesEndpoint)
                    .build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(metricsData.toString())
            .retrieve()
            .bodyToMono(String.class)
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


    public void sendResourcesTimeSeries(WorkflowTask task) {
        Map<String, List<TimeSeriesDataPoint>> timeSeriesMetrics = task.getTimeSeriesMetrics();

        if(!timeSeriesMetrics.isEmpty()) {
            JSONArray timeSeriesMetricsData = new JSONArray();
            for (Map.Entry<String, List<TimeSeriesDataPoint>> metric : timeSeriesMetrics.entrySet()) {
                String metricName = metric.getKey();
                List<TimeSeriesDataPoint> dataPoints = metric.getValue();

                for (TimeSeriesDataPoint dataPoint : dataPoints) {
                    JSONObject row = new JSONObject();
                    row.put("metric_name", metricName);
                    row.put("pod_id", task.getPod());
                    row.put("unix_time", dataPoint.getTimestamp());
                    row.put("metric_value", dataPoint.getValue());
                    timeSeriesMetricsData.put(row);
                }
            }

            logger.info("Sending time series metrics data: {}", timeSeriesMetricsData);

            webClient.post()
                    .uri(uriBuilder -> uriBuilder.scheme("http")
                            .host(postgrestServerUrl)
                            .port(postgrestServerPort)
                            .path(postgrestResourcesTimeSeriesEndpoint)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(timeSeriesMetricsData.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(error -> {
                        if (error instanceof WebClientResponseException) {
                            WebClientResponseException ex = (WebClientResponseException) error;
                            logger.error("Error sending time series metrics. Status: {}, Body: {}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
                        } else {
                            logger.error("Error sending time series metrics: {}", error.getMessage());
                        }
                    })
                    .subscribe();
        }
    }

}