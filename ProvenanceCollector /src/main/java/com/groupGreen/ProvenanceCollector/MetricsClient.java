package com.groupGreen.ProvenanceCollector;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class MetricsClient {

    private final WebClient webClient;

    public MetricsClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    // test logic to fetch metrics from running prom-server

    public void fetchMetrics() {
        String promUrl = "http://localhost:8080/api/v1/query";
        String query = "up";

        String url = promUrl + "?query=" + query;
        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Response from Prometheus-Server: " + response);
    }

}