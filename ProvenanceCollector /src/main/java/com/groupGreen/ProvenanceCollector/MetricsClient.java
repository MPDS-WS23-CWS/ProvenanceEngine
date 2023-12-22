package com.groupGreen.ProvenanceCollector;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class MetricsClient {

    private final WebClient webClient;

    @Value("${prometheus.server.url}")
    private String prometheusServerUrl;

    @Value("${metrics.instant.profiles}")
    private String [] instantQueries;

    @Value("${metrics.range.profiles}")
    private String [] rangeQueries;

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

}
