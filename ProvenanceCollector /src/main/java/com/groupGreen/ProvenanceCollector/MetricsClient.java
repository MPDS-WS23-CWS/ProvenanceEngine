package com.groupGreen.ProvenanceCollector;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

@Component
public class MetricsClient {

    private final WebClient webClient;

    @Value("${prometheus.server.url}")
    private String prometheusServerUrl;

    @Value("${prometheus.queries}")
    private String [] queries;

    public MetricsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void fetchMetrics() {
        List<String> metrics = Arrays.asList(queries);

        for (String query : metrics) {
            String url = prometheusServerUrl + "?query=" + query;
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        System.out.println("Response from Prometheus-Server: " + response);
        }
    }
}