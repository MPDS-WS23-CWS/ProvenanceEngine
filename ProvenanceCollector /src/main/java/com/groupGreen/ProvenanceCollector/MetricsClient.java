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

/*    
    @Value("${metrics.instant.profiles}")
    private String [] instantQueries;

    @Value("${metrics.range.profiles}")
    private String [] rangeQueries;
*/
    private String prometheusRange = "72h";

    private List<String> returnedTasks = new ArrayList<>();

    public MetricsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<WorkflowTask> fetchNewData() {
        List<WorkflowTask> newlyCompletedTasks = fetchNewlyCompletedTasks();
        fetchStartTimes(newlyCompletedTasks);
        fetchProcessNames(newlyCompletedTasks);
        fetchMetric(newlyCompletedTasks);

        List<String> pods = newlyCompletedTasks.stream().map(WorkflowTask::getPod).toList();
        returnedTasks.addAll(pods);
        return newlyCompletedTasks;
    }

    private List<WorkflowTask> fetchNewlyCompletedTasks() {
        String query = String.format("last_over_time(kube_pod_completion_time{}[%s])", prometheusRange);
        String uriVariable = "{pod=~'nf-.*'}";
        String result = queryPrometheusBlocking(query, uriVariable);
        Map<String, Long> completionTimes = parseTimes(result);

        // filter out tasks already written to db
        Map<String, Long> newCompletionTimes = completionTimes.entrySet()
                .stream()
                .filter(e -> ! returnedTasks.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // construct list of WorkflowTask objects
        return newCompletionTimes.entrySet()
                .stream()
                .map(e -> new WorkflowTask(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private void fetchStartTimes(List<WorkflowTask> tasks) {
        String query = String.format("last_over_time(kube_pod_start_time{}[%s])", prometheusRange);
        String uriVariable = "{pod=~'nf-.*'}";
        String result = queryPrometheusBlocking(query, uriVariable);
        Map<String, Long> startTimes = parseTimes(result);

        tasks.forEach(t -> t.setStartTime(startTimes.get(t.getPod())));
    }

    private void fetchProcessNames(List<WorkflowTask> tasks) {
        String query = String.format("last_over_time(kube_pod_labels{}[%s])", prometheusRange);
        String uriVariable = "{pod=~'nf-.*'}";
        String result = queryPrometheusBlocking(query, uriVariable);
        Map<String, String> processNames = parseProcessNames(result);

        tasks.forEach(t -> t.setProcessName(processNames.get(t.getPod())));
    }

    private void fetchMetric(List<WorkflowTask> tasks) {
        // TODO this is just an example with one metric
        String query = String.format("sum by(pod)(avg_over_time(container_cpu_usage_seconds_total[%s]))", prometheusRange);
        String result = queryPrometheusBlocking(query);
        Map<String, Double> metric = parseMetric(result);

        tasks.forEach(t -> t.putMetric(query, metric.get(t.getPod())));
    }

    private String queryPrometheusBlocking(String query) {
        String url = prometheusServerUrl + "?query=" + query;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    private String queryPrometheusBlocking(String query, String uriVariable) {
        String url = prometheusServerUrl + "?query=" + query;
        return webClient.get()
                // TODO filtering via uriVariable doesn't work as expected
                .uri(url, uriVariable)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private Map<String, String> parseProcessNames(String jsonString) {
        Map<String, String> processNames = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");

            // TODO Filtering of NF pods via uriVariable doesn't work as expected
            //  therefore we have to filter here. need to fix this
            if(pod.startsWith("nf-")) {
                String processName = metric.getString("label_process_name");
                processNames.put(pod, processName);
            }
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

            // TODO Filtering of NF pods via uriVariable doesn't work as expected
            //  therefore we have to filter here. need to fix this
            if(pod.startsWith("nf-")) {
                long completionTime = result.getJSONObject(i).getJSONArray("value").getLong(1);
                times.put(pod, completionTime);
            }
        }
        return times;
    }

    // TODO Check if data type double is okay for all metrics
    private Map<String, Double> parseMetric(String jsonString) {
        Map<String, Double> values = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");

            // TODO Filtering of NF pods via uriVariable doesn't work as expected
            //  therefore we have to filter here. need to fix this
            if(metric.has("pod")) {
                String pod = metric.getString("pod");
                if (pod.startsWith("nf-")) {
                    double completionTime = result.getJSONObject(i).getJSONArray("value").getDouble(1);
                    values.put(pod, completionTime);
                }
            }
        }
        return values;
    }

}
