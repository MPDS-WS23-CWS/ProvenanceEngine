package com.groupGreen.ProvenanceCollector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

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
//    private Map<String, WorkflowTask> newlyCompletedTasks = new HashMap<>();
    private Map<String, WorkflowTask> transmittedTasks =  new HashMap<>();

    public MetricsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

//    public void fetchInstantMetrics() {
//        List<String> metrics = Arrays.asList(instantQueries);
//
//        for (String query : metrics) {
//            String url = prometheusServerUrl + "?query=" + query;
//            webClient.get()
//                    .uri(url)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .subscribe(responseBody -> System.out.println("Response from server: " + responseBody));
//    }
//
//}
//    public void fetchRangeMetrics() {
//        List<String> metrics = Arrays.asList(rangeQueries);
//
//        for (String query : metrics) {
//            String url = prometheusServerUrl + "?query=" + query;
//            webClient.get()
//                    .uri(url)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .subscribe(responseBody -> System.out.println("Response from server: " + responseBody));
//        }
//
//    }



    public List<WorkflowTask> fetchNewData() {
        Map<String, WorkflowTask> newlyCompletedTasks = fetchNewlyCompletedTasks();
        fetchStartTimes(newlyCompletedTasks);
        fetchProcessNames(newlyCompletedTasks);

        return (List<WorkflowTask>) newlyCompletedTasks.values();
    }

    private Map<String, WorkflowTask> fetchNewlyCompletedTasks() {
        String query = "last_over_time(kube_pod_completion_time{pod=~'nf-.*'}[10h])";
//        String query = "last_over_time%28kube_pod_completion_time%7Bpod%3D%7E%27nf-.%2A%27%7D%5B10h%5D%29";
        String result = queryPrometheusBlocking(query);
        Map<String, Long> completionTimes = parseTimes(result);

        // filter out tasks already written to db
        Map<String, Long> newCompletionTimes = completionTimes.entrySet()
                .stream()
                .filter(e -> ! transmittedTasks.containsKey(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // construct map of WorkflowTask objects
        return newCompletionTimes.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new WorkflowTask(e.getKey(), e.getValue())
                ));
    }

    private void fetchStartTimes(Map<String, WorkflowTask> tasks) {
        String query = "last_over_time(kube_pod_start_time{pod=~'nf-.*'}[72h])";
        String result = queryPrometheusBlocking(query);
        Map<String, Long> startTimes = parseTimes(result);

        tasks.forEach((k, v) -> v.setStartTime(startTimes.get(k)));
    }

    private void fetchProcessNames(Map<String, WorkflowTask> tasks) {
        String query = "kube_pod_labels{pod=~'nf-.*'}[72h])";
        String result = queryPrometheusBlocking(query);
        Map<String, String> processNames = parseProcessNames(result);

        tasks.forEach((k, v) -> v.setProcessName(processNames.get(k)));
    }


    private String queryPrometheusBlocking(String query) {
        String url = prometheusServerUrl + "?query=" + query;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


//    public void fetchTaskMetadata(){
//        String labelsJson = queryPrometheusBlocking("kube_pod_labels{pod=~'nf-.*'}[72h]");
//        parseProcessNames(labelsJson);
//        String startTimesJson = queryPrometheusBlocking("last_over_time(kube_pod_start_time{pod=~'nf-.*'}[72h])");
//        parseTimes(startTimesJson);
//        String endTimesJson = queryPrometheusBlocking("last_over_time(kube_pod_completion_time{pod=~'nf-.*'}[10h])");
//        parseTimes(endTimesJson);
//
//        for (String name: workflowTasks.keySet()) {
//            WorkflowTask value = workflowTasks.get(name);
//            System.out.println(name + " " + value);
//        }
//
//        String cpuUsageJson = queryPrometheusBlocking("container_cpu_usage_seconds_total[72h]");
//        System.out.println(parseMetric(cpuUsageJson));
//    }



    private Map<String, String> parseProcessNames(String jsonString) {
        Map<String, String> processNames = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");
            String processName = metric.getString("label_process_name");

            processNames.put(pod, processName);
        }

        return processNames;
    }

    private Map<String, Long> parseTimes(String jsonString) {
        Map<String, Long> times = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");

            long completionTime = result.getJSONObject(i).getJSONArray("value").getLong(1);
            times.put(pod, completionTime);
        }
        return times;
    }

//    private void parseTimes(String jsonString) {
//        JSONObject jsonObject = new JSONObject(jsonString);
//        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");
//        for (int i = 0; i < result.length(); i++) {
//            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
//
//            if (metric.has("pod") && metric.getString("pod").startsWith("nf-")){
//                String pod = metric.getString("pod");
//                JSONArray values = result.getJSONObject(i).getJSONArray("values");
//                // there can be multiple entries in values but they should all have the same start/end time value
//                // check this as a sanity check?
//                long firstValue = values.getJSONArray(0).getLong(1);
//
//                if (workflowTasks.containsKey(pod)) {
//                    WorkflowTask task = workflowTasks.get(pod);
//                    if (metric.getString("__name__").equals("kube_pod_start_time")) {
//                        task.setStartTime(firstValue);
//                    } else if (metric.getString("__name__").equals("kube_pod_completion_time")) {
//                        task.setCompletionTime(firstValue);
//                    }
//                } else {
//                    // TODO proper logging
//                    System.out.println("Warning - Task not found: " + pod);
//                }
//            }
//        }
//    }

//    private Map<String, List<Double>> parseMetric(String jsonString) {
//        Map<String, List<Double>> values = new HashMap<>();
//
//        JSONObject jsonObject = new JSONObject(jsonString);
//        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");
//        for (int i = 0; i < result.length(); i++) {
//            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
//
//            // we use label "container" instead of "pod", as there might be multiple containers
//            // within a nextflow pod (e.g. k8s pause container)
//            if (metric.has("container")) {
//                String container = metric.getString("container");
//
//                if (container.startsWith("nf-") && workflowTasks.containsKey(container)){
//
//                    WorkflowTask task = workflowTasks.get(container);
//
//                    // we only have to check that values were scraped before endTime
//                    long endTime = task.getCompletionTime();
//                    // if we don't have a value for endTime, we don't know which data points to use
//                    if(!task.endTimeSet()) {continue;}
//
//                    List<Double> curValues = new ArrayList<>();
//                    JSONArray valuesArray = result.getJSONObject(i).getJSONArray("values");
//                    for (int j = 0; j < valuesArray.length(); j++) {
//                        JSONArray dataPoint = valuesArray.getJSONArray(j);
//                        System.out.println(dataPoint.getDouble(1));
//                        if(dataPoint.getDouble(0) < endTime) {
//                            curValues.add(dataPoint.getDouble(1));
//                        } else {
//                            // all following data points will be newer, i.e. irrelevant
//                            break;
//                        }
//                    }
//                    values.put(container, curValues);
//                }
//            }
//        }
//        return values;
//    }

}
