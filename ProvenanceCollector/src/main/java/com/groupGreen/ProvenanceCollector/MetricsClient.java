package com.groupGreen.ProvenanceCollector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.text.StringSubstitutor;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MetricsClient {

    private static final Logger logger = LoggerFactory.getLogger(MetricsClient.class);

    @Autowired
    private final WebClient webClient;

    @Value("${prometheus.server.url}")
    private String prometheusServerUrl;

    @Value("${prometheus.server.port}")
    private String prometheusServerPort;

    @Value("${prometheus.server.endpoint}")
    private String prometheusServerEndpoint;

    @Autowired
    private Metrics metrics;

    @Autowired
    private DataSender dataSender;

    private String prometheusRange = "200h";

    private List<String> returnedTasks = new ArrayList<>();

    private List<String> returnedWorkflowIDs = new ArrayList<>();

    public MetricsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<WorkflowTask> fetchNewData() {
        logger.info("Fetching newly completed tasks");
        List<WorkflowTask> newlyCompletedTasks = fetchNewlyCompletedTasks();
        if(newlyCompletedTasks.isEmpty()) {
            logger.info("No newly completed tasks found");
            return newlyCompletedTasks;
        }

        logger.info("Identified newly completed tasks: {}", newlyCompletedTasks.stream().map(WorkflowTask::getPod).toList());
        logger.info("Fetching metrics");

        fetchStartTimes(newlyCompletedTasks);
        fetchLabels(newlyCompletedTasks);
        fetchNodeNames(newlyCompletedTasks);
        fetchMetrics(newlyCompletedTasks);
        fetchTimeSeriesMetrics(newlyCompletedTasks);

        for (WorkflowTask task : newlyCompletedTasks) {
            dataSender.sendTasks(task);
            dataSender.sendResources(task);
            dataSender.sendResourcesTimeSeries(task);
        }

        List<String> pods = newlyCompletedTasks.stream().map(WorkflowTask::getPod).toList();
        returnedTasks.addAll(pods);
        return newlyCompletedTasks;
    }

    private List<WorkflowTask> fetchNewlyCompletedTasks() {
        String query = replacePlaceholders("last_over_time(kube_pod_completion_time{pod=~'nf-.*',namespace='cws'}[{RANGE}])");
        String result = queryPrometheusBlocking(query);
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
        String query = replacePlaceholders("last_over_time(kube_pod_start_time{pod=~'nf-.*',namespace='cws'}[{RANGE}])");
        String result = queryPrometheusBlocking(query);
        Map<String, Long> startTimes = parseTimes(result);

        tasks.forEach(t -> t.setStartTime(startTimes.get(t.getPod())));
    }


    private void fetchLabels(List<WorkflowTask> tasks) {
        String query = replacePlaceholders("last_over_time(kube_pod_labels{pod=~'nf-.*',namespace='cws'}[{RANGE}])");
        String result = queryPrometheusBlocking(query);

        Map<String, String> workflowIDs = parseWorkflowIDs(result);
        tasks.forEach(t -> t.setWorkflowID(workflowIDs.get(t.getPod())));

        Map<String, String> processNames = parseProcessNames(result);
        tasks.forEach(t -> t.setProcessName(processNames.get(t.getPod())));

        Map<String, Long> inputSizes = parseInputSizes(result);
        tasks.forEach(t -> t.setInputSize(inputSizes.get(t.getPod())));
    }

    private void fetchNodeNames(List<WorkflowTask> tasks) {
        String query = replacePlaceholders("last_over_time(kube_pod_info{pod=~'nf-.*',namespace='cws'}[{RANGE}])");
        String result = queryPrometheusBlocking(query);
        Map<String, String> nodeNames = parseNodeNames(result);

        tasks.forEach(t -> t.setNodeName(nodeNames.get(t.getPod())));
    }

    private void fetchMetrics(List<WorkflowTask> tasks) {
        Map <String, String> resourceMetrics = metrics.getResources();
        for(Map.Entry<String, String> e : resourceMetrics.entrySet()) {
            String result = queryPrometheusBlocking(replacePlaceholders(e.getValue()));
            Map<String, Double> metric = parseMetric(result);
            tasks.forEach(t -> t.putMetric(e.getKey(), metric.get(t.getPod())));
        }
    }

    private void fetchTimeSeriesMetrics(List<WorkflowTask> tasks) {
        Map <String, String> resourceTimeSeriesMetrics = metrics.getResourcesTimeSeries();
        for(Map.Entry<String, String> e : resourceTimeSeriesMetrics.entrySet()) {
            String result = queryPrometheusBlocking(replacePlaceholders(e.getValue()));
            Map<String, List<TimeSeriesDataPoint>> timeSeriesMetric = parseTimeSeriesMetric(result);
            for(WorkflowTask task : tasks) {
                if(timeSeriesMetric.containsKey(task.getPod())) {
                    task.putTimeSeriesMetric(e.getKey(), timeSeriesMetric.get(task.getPod()));
                }
            }
        }
    }

//    private void fetchWorkflowData(Workflow workflow) {
//        String query = replacePlaceholders("last_over_time(kube_pod_labels{pod=~'nf-.*', }[{RANGE}])");
//        String result = queryPrometheusBlocking(replacePlaceholders(e.getValue()));
//        Map<String, Double> metric = parseMetric(result);
//        tasks.forEach(t -> t.putMetric(e.getKey(), metric.get(t.getPod())));
//
//    }

    private String queryPrometheusBlocking(String query) {
        MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();
        bodyValues.add("query", query);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.scheme("http")
                        .host(prometheusServerUrl)
                        .port(prometheusServerPort)
                        .path(prometheusServerEndpoint)
                        .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(bodyValues))
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
            String processName = metric.getString("label_process_name");
            processNames.put(pod, processName);
        }
        return processNames;
    }

    private Map<String, String> parseNodeNames(String jsonString) {
        Map<String, String> nodeNames = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");
            if (metric.has("node")) {
                String nodeName = metric.getString("node");
                nodeNames.put(pod, nodeName);
            }
        }
        return nodeNames;
    }

    private Map<String, String> parseWorkflowIDs(String jsonString) {
        Map<String, String> workflowIDs = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");
            if(metric.has("label_session_id")) {
                String workflowID = metric.getString("label_session_id");
                workflowIDs.put(pod, workflowID);
                checkIfNewWorkflow(workflowID);
            } else {
                workflowIDs.put(pod, "None");
            }
        }
        return workflowIDs;
    }

    private Map<String, Long> parseInputSizes(String jsonString) {
        Map<String, Long> inputSizes = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");
            if(metric.has("label_input_size")) {
                long inputSize = metric.getLong("label_input_size");
                inputSizes.put(pod, inputSize);
            } else {
                inputSizes.put(pod, -1L);
            }
        }
        return inputSizes;
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

    private Map<String, Double> parseMetric(String jsonString) {
        Map<String, Double> values = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");
            double completionTime = result.getJSONObject(i).getJSONArray("value").getDouble(1);
            values.put(pod, completionTime);
        }
        return values;
    }

    private Map<String, List<TimeSeriesDataPoint>> parseTimeSeriesMetric(String jsonString) {
        Map<String, List<TimeSeriesDataPoint>> timeSeriesValues = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray result = jsonObject.getJSONObject("data").getJSONArray("result");

        for (int i = 0; i < result.length(); i++) {
            JSONObject metric = result.getJSONObject(i).getJSONObject("metric");
            String pod = metric.getString("pod");
            JSONArray valuesArray = result.getJSONObject(i).getJSONArray("values");

            List<TimeSeriesDataPoint> dataPoints = new ArrayList<>();
            for (int j = 0; j < valuesArray.length(); j++) {
                JSONArray dataPointArray = valuesArray.getJSONArray(j);
                double timestamp = dataPointArray.getDouble(0);
                double value = Double.parseDouble(dataPointArray.getString(1));
                dataPoints.add(new TimeSeriesDataPoint(timestamp, value));
            }
            timeSeriesValues.put(pod, dataPoints);
        }
        return timeSeriesValues;
    }

    private String replacePlaceholders(String template) {
        Map<String, String> substitutions = new HashMap<>();
        substitutions.put("RANGE", prometheusRange);
        StringSubstitutor sub = new StringSubstitutor(substitutions, "{", "}");
        return sub.replace(template);
    }

    private void checkIfNewWorkflow(String workflowID) {
        if(!returnedWorkflowIDs.contains(workflowID)) {
            logger.info("Identified new workflow run: {}", workflowID);
            Workflow newWorkflow = new Workflow(workflowID);
            dataSender.sendWorkflows(newWorkflow);
            returnedWorkflowIDs.add(workflowID);
        }
    }

}
