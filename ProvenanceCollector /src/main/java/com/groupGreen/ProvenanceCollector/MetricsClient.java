package com.groupGreen.ProvenanceCollector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Component
public class MetricsClient {
    @Autowired
    private final WebClient webClient;

    @Value("${prometheus.server.url}")
    private String prometheusServerUrl;

    @Value("${metrics.instant.profiles}")
    private String [] instantQueries;

    @Value("${metrics.range.profiles}")
    private String [] rangeQueries;

    private Map<String, WorkflowTask> workflowTasks = new HashMap<>();

    public MetricsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void fetchInstantMetrics() {
        List<String> metrics = Arrays.asList(instantQueries);

        for (String query : metrics) {
            String url = prometheusServerUrl + "?query=" + query;
            webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(responseBody -> System.out.println("Response from server: " + responseBody));
    }

}
    public void fetchRangeMetrics() {
        List<String> metrics = Arrays.asList(rangeQueries);

        for (String query : metrics) {
            String url = prometheusServerUrl + "?query=" + query;
            webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(responseBody -> System.out.println("Response from server: " + responseBody));
        }

    }

    public String queryPrometheusBlocking(String query) {
        String url = prometheusServerUrl + "?query=" + query;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void fetchTaskMetadata(){
        String labelsJson = queryPrometheusBlocking("kube_pod_labels[72h]");
        parseProcessNames(labelsJson);
        String startTimesJson = queryPrometheusBlocking("kube_pod_start_time[72h]");
        parseTimes(startTimesJson);
        String endTimesJson = queryPrometheusBlocking("kube_pod_completion_time[72h]");
        parseTimes(endTimesJson);

        for (String name: workflowTasks.keySet()) {
            WorkflowTask value = workflowTasks.get(name);
            System.out.println(name + " " + value);
        }

        String cpuUsageJson = queryPrometheusBlocking("container_cpu_usage_seconds_total[72h]");
        System.out.println(parseMetric(cpuUsageJson));
    }

    private void parseProcessNames(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");
        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");

            if (metric.has("pod") && metric.getString("pod").startsWith("nf-")){
                String pod = metric.getString("pod");
                String processName = metric.getString("label_process_name");

                if(!workflowTasks.containsKey(pod)) {
                    WorkflowTask task = new WorkflowTask(pod);
                    task.setProcessName(processName);
                    workflowTasks.put(pod, task);
                } else {
                    // sanity check
                    assert workflowTasks.get(pod).getProcessName().equals(processName);
                }
            }
        }
    }

    private void parseTimes(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");
        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");

            if (metric.has("pod") && metric.getString("pod").startsWith("nf-")){
                String pod = metric.getString("pod");
                JSONArray values = result.getJSONObject(i).getJSONArray("values");
                // there can be multiple entries in values but they should all have the same start/end time value
                // check this as a sanity check?
                long firstValue = values.getJSONArray(0).getLong(1);

                if (workflowTasks.containsKey(pod)) {
                    WorkflowTask task = workflowTasks.get(pod);
                    if (metric.getString("__name__").equals("kube_pod_start_time")) {
                        task.setStartTime(firstValue);
                    } else if (metric.getString("__name__").equals("kube_pod_completion_time")) {
                        task.setEndTime(firstValue);
                    }
                } else {
                    // TODO proper logging
                    System.out.println("Warning - Task not found: " + pod);
                }
            }
        }
    }

    private Map<String, List<Double>> parseMetric(String jsonString) {
        Map<String, List<Double>> values = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");
        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");

            if (metric.has("pod")) {
                String pod = metric.getString("pod");

                if (pod.startsWith("nf-") && workflowTasks.containsKey(pod)){
                    WorkflowTask task = workflowTasks.get(pod);

                    // we only have to check that values were scraped before endTime
                    long endTime = task.getEndTime();
                    // if we don't have a value for endTime, we don't know which data points to use
                    if(!task.endTimeSet()) {continue;}

                    List<Double> curValues = new ArrayList<>();
                    JSONArray valuesArray = result.getJSONObject(i).getJSONArray("values");
                    for (int j = 0; j < valuesArray.length(); j++) {
                        JSONArray dataPoint = valuesArray.getJSONArray(j);
                        if(dataPoint.getDouble(0) < endTime) {
                            curValues.add(dataPoint.getDouble(1));
                        } else {
                            // all following data points will be newer, i.e. irrelevant
                            break;
                        }
                    }
                    values.put(pod, curValues);
                }
            }
        }
        return values;
    }

}
